package isec.loan.controller;

import com.alibaba.fastjson.JSONObject;
import isec.base.bean.MapBox;
import isec.base.util.MD5Util;
import isec.base.util.Md5;
import isec.base.util.S;
import isec.base.util.Tool;
import isec.loan.common.In;
import isec.loan.configurer.Config;
import isec.loan.core.PromptException;
import isec.loan.entity.Bill;
import isec.loan.entity.Loan;
import isec.loan.entity.PayFlow;
import isec.loan.entity.User;
import isec.loan.entity.enums.BillStatus;
import isec.loan.entity.enums.PayType;
import isec.loan.entity.enums.TradeType;
import isec.loan.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author p
 * @date 2019/07/23
 */
@RestController
@RequestMapping(value = "bill")
@Validated
public class BillController {

    @Autowired
    BillService billService;

    @Autowired
    UserService userService;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    LoanService loanService;

    @Autowired
    LoanUserInfoService loanUserInfoService;

    @Autowired
    PayService payService;

    @Autowired
    MoneyCalculateService moneyCalculateService;

    @Autowired
    Config config;

    private Logger logger = LoggerFactory.getLogger(BillController.class);

    /**
     * 生成账单
     *
     * @param loanId
     * @return
     */
    @PostMapping(value = "createBill")
    public void createBill(@NotBlank(message = "请传入贷款编号loanId") String loanId) {

        billService.createBill(loanId);

    }


    @PostMapping(value = "myBill")
    public Map<String, Object> myBill(@In User user, @RequestParam(defaultValue = "1") Integer page,
                                      @RequestParam(defaultValue = "10") Integer pageSize,
                                      @RequestParam(defaultValue = "1") int status) {

        List<Map<String, String>> billList = billService.getMyBill(user.getUserId(), page, pageSize, status);
        return MapBox.instance().put("billList", billList).toMap();

    }


    /**
     * 创建预支付流水接口
     */
    @PostMapping(value = "addPayFlow")
    public JSONObject addPayFlow(@In(required = false) User user,
                                 @NotBlank(message = "请传入账单ID") String billId,
                                 @NotBlank(message = "请传入账单金额") String repayment,
                                 @NotBlank(message = "请传入req_param") String req_param) {

        // ----------验证开始-----------------
        Bill bill = billService.findById(billId);
        Loan loan = loanService.findById(bill.getLoanId());
        if (null == bill) {
            throw new PromptException("账单不存在");
        }

        if (bill.getStatus() != BillStatus.UNREPAY.getKey()) {
            throw new PromptException("账单不在还款状态");
        }

        // 还款金额
        BigDecimal money = BigDecimal.valueOf(bill.getRepaymentAmount());

        // 逾期
        if (bill.getStatus() == BillStatus.UNREPAY.getKey() && S.getCurrentTimestamp() > bill.getDeadline()) {
            int overDueDays = (int) Math.ceil((S.getCurrentTimestamp() - bill.getDeadline()) / 86400);
            money = moneyCalculateService.getTotalOverdueMoney(loan.getOverdueRate(), bill.getRepaymentAmount(),
                    overDueDays).multiply(BigDecimal.valueOf(100));
        }

        // 判断账单是否逾期、 还款金额是否满足
        if (new BigDecimal(moneyCalculateService.transferMoneyToYuan(new BigDecimal(repayment).multiply(BigDecimal.valueOf(100)).intValue())).compareTo(money) < 0) {
//            throw new PromptException("还款金额不足");
        }


        // ----------验证结束-----------------

        String title = "账单-用户还款";
        String payFlowMemeberId = user.getUserId();
        String payFlowMobile = user.getMobile();

        PayFlow payFlow = new PayFlow(billId, TradeType.TYPE_REPAYMENT.getKey(), payFlowMemeberId, payFlowMobile,
                title, money.intValue(), PayType.ALI.getKey(), req_param);

        // 流水号
        String billNo = payFlow.getPayId();

        // 请求收银台参数
        JSONObject obj = new JSONObject();
        obj.put("billNo", billNo);
        obj.put("title", title);

        long timeSt = System.currentTimeMillis();
        obj.put("timeStamp", timeSt);

        String app_sign = Md5.md5(config.getAppId() + timeSt + config.getAppSign());
        obj.put("appSign", app_sign);
        payFlow.setRequestParam(req_param);
        payService.save(payFlow);
        return obj;
    }


    /**
     * 还款异步回调
     *
     * @param req
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @RequestMapping("/replaymentNotily")
    public synchronized String callBack(HttpServletRequest req) throws Exception {
        logger.info("==========================异步回调开始=====================");
        ServletInputStream in = req.getInputStream();
        String params = Tool.convertInputStream2String(in);
        logger.info("支付回调回调信息：" + params);
        JSONObject notify = JSONObject.parseObject(params);
        if (null == notify) {
            logger.error("回调参数为空，直接返回");
            return "回调参数为空";
        }
        Map<String, Object> messageDetail = (Map<String, Object>) notify.get("message_detail");
        // cs订单号
        String outTradeNo = "";
        if (null != messageDetail) {
            outTradeNo = String.valueOf(messageDetail.get("cs_merbill_id"));
        }
        // 商户订单号
        String transactionId = String.valueOf(notify.get("transaction_id"));
        String transactionType = String.valueOf(notify.get("transaction_type"));
        String tradeSuccess = String.valueOf(notify.get("trade_success"));
        String channelType = String.valueOf(notify.get("channel_type"));
        String transactionFee = String.valueOf(notify.get("transaction_fee"));
        // 签名
        String signature = String.valueOf(notify.get("signature"));
        logger.info("签名signature：" + signature);
        // 验证签名
        String toSign = config.getAppId() + transactionId + transactionType + channelType + transactionFee
                + config.getMasterSecret();
        logger.info("签名前：" + toSign);
        String mySign = MD5Util.getMD5(toSign, "UTF-8");
        logger.info("签名后：" + mySign);
        if (!mySign.equals(signature)) {
            logger.warn("支付回调签名错误");
            return "签名错误";
        }

        logger.info("==========================异步回调结束=====================");
        return "success";
    }


}
