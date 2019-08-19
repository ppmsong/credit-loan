package isec.loan.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import isec.base.bean.MapBox;
import isec.base.util.MD5Util;
import isec.base.util.Md5;
import isec.base.util.S;
import isec.base.util.Tool;
import isec.loan.common.In;
import isec.loan.configurer.Config;
import isec.loan.core.PromptException;
import isec.loan.entity.*;
import isec.loan.entity.enums.*;
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
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
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
    ActionRecordService actionRecordService;

    @Autowired
    Config config;

    @Autowired
    MessageService messageService;

    @Autowired
    OdinService odinService;

    private Logger logger = LoggerFactory.getLogger(BillController.class);

    /**
     * 贷款审核成功->生成账单
     *
     * @param loanId
     * @return
     */
    @PostMapping(value = "createBill")
    public void createBill(@NotBlank(message = "请传入贷款编号loanId") String loanId, @NotBlank(message = "请传入操作员operatorId") String operatorId) {

        boolean succ = billService.createBill(loanId, operatorId);
        if (!succ) {
            throw new PromptException("放款出现异常，请检查");
        }

    }


    /**
     * 我的账单
     *
     * @param user
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    @PostMapping(value = "myBill")
    public Map<String, Object> myBill(@In User user, @RequestParam(defaultValue = "1") Integer page,
                                      @RequestParam(defaultValue = "10") Integer pageSize,
                                      @RequestParam(defaultValue = "1") int status) {

        List<Map<String, String>> billList = billService.getMyBill(user.getUserId(), page, pageSize, status);
        return MapBox.instance().put("billList", billList).toMap();

    }

    /**
     * 账单详情
     *
     * @param user
     * @param billId
     * @return
     */
    @PostMapping(value = "billDetail")
    public JSONObject billDetail(@In User user, String billId) {

        Bill bill = billService.findById(billId);
        if (null == bill) {
            throw new PromptException("账单记录不存在");
        }
        Loan loan = loanService.findById(bill.getLoanId());
        if (null == loan) {
            throw new PromptException("贷款记录不存在");
        }

        JSONObject result = new JSONObject();
        //放款时间
        result.put("fkTime", bill.getCreateTime());
        //还款到期时间
        result.put("hkTime", bill.getDeadline());
        //逾期天数（精确到秒）
        int overdueSeconds = moneyCalculateService.overDueSecond(bill.getDeadline());
        //逾期天数（秒）
        result.put("overDueDays", moneyCalculateService.overdueDays(overdueSeconds));
        BigDecimal overDue = moneyCalculateService.getTotalOverdueMoney(loan.getOverdueRate(), bill.getrBasic(), overdueSeconds);
        //应还金额
        result.put("repayment_amount", moneyCalculateService.getBillRepayMoney(billId));
        //滞纳金每天
        result.put("dayOverdue", moneyCalculateService.getDayOverdueMoney(loan.getOverdueRate(), bill.getrBasic()));
        result.put("allOverdue", overDue);
        //本金，利息
        result.put("r_basic", new BigDecimal(bill.getrBasic()).divide(new BigDecimal(100), 2, RoundingMode.DOWN));
        result.put("r_interest", new BigDecimal(bill.getrInterest()).divide(new BigDecimal(100), 2, RoundingMode.DOWN));
        //状态
        result.put("status", bill.getStatus());

        return result;

    }


    /**
     * 创建预支付流水接口
     */
    @PostMapping(value = "addPayFlow")
    public JSONObject addPayFlow(@In(required = false) User user,
                                 @NotBlank(message = "请传入账单ID") String billId,
                                 @NotBlank(message = "请传入账单金额") String repayment) {

        // ----------验证开始-----------------
        Bill bill = billService.findById(billId);
        if (null == bill) {
            throw new PromptException("账单不存在");
        }
        Loan loan = loanService.findById(bill.getLoanId());
        if (bill.getStatus() != BillStatus.UNREPAY.getKey()) {
            throw new PromptException("【未还款】状态");
        }

        // 还款金额
        BigDecimal money = moneyCalculateService.fenToYuan(bill.getRepaymentAmount());

        // 逾期
        if (bill.getStatus() == BillStatus.UNREPAY.getKey() && S.getCurrentTimestamp() > bill.getDeadline()) {
            int overDueDays = (int) Math.ceil((S.getCurrentTimestamp() - bill.getDeadline()) / 86400);
            money = moneyCalculateService.getTotalOverdueMoney(loan.getOverdueRate(), bill.getrBasic(),
                    overDueDays).multiply(BigDecimal.valueOf(100));
        }

        // 判断账单是否逾期、 还款金额是否满足
        if (new BigDecimal(repayment).multiply(new BigDecimal(100)).compareTo(money) < 0) {
            throw new PromptException("还款金额不足");
        }


        // ----------验证结束-----------------

        String title = "账单-用户还款";
        String payFlowMemeberId = user.getUserId();
        String payFlowMobile = user.getMobile();

        PayFlow payFlow = new PayFlow(billId, TradeType.TYPE_REPAYMENT.getKey(), payFlowMemeberId, payFlowMobile,
                title, (money.multiply(new BigDecimal(100))).intValue(), PayType.ALI.getKey(), "");

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
        payFlow.setRequestParam(obj.toJSONString());
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
    @RequestMapping("/replaymentNotily")
    public synchronized String replaymentNotily(HttpServletRequest req) throws Exception {
        logger.info("==========================异步回调开始=====================");
        ServletInputStream in = req.getInputStream();
        String params = Tool.convertInputStream2String(in);
        logger.info("还款异步回调信息：" + params);
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
            logger.error("还款回调签名错误");
            return "签名错误";
        }

        /**
         *
         * 1.更新payFlow表状态
         * 2.更新bill表状态
         * 3.更新贷款订单loan表状态
         *
         */
        PayFlow payFlow = payService.findById(transactionId);
        if (null == payFlow) {
            logger.error("不存在的订单号：{}", transactionId);
            return "不存在的订单号：" + transactionId;
        }
        if (TradeStatus.STATUS_WAITTING.getKey() != payFlow.getStatus()) {
            logger.warn("订单{}已处理，请不要重复操作", transactionId);
            return "订单" + transactionId + "已处理，请不要重复操作";
        }
        payFlow.setStatus(TradeStatus.TRADE_SUCCESS.getKey());
        payFlow.setOutTradeNo(outTradeNo);
        payFlow.setNotify(notify.toJSONString());
        payFlow.setUpdateTime(S.getCurrentTimestamp());
        payService.update(payFlow);


        Bill bill = billService.findById(payFlow.getTradeNo());
        if (null == bill) {
            logger.warn("未找到账单记录：{}", transactionId);
            return "未找到账单记录：" + transactionId;
        }
        //非未还款、展期状态
        if (BillStatus.UNREPAY.getKey() != bill.getStatus()) {
            logger.warn("账单已经处理过，请不要重复操作：{}", transactionId);
            return "账单已经处理过，请不要重复操作:" + transactionId;
        }
        //判断是否是提前还款
        bill.setStatus(BillStatus.REPAY.getKey());
        bill.setRepayTime(S.getCurrentTimestamp());
        bill.setUpdateTime(S.getCurrentTimestamp());
        if (S.getCurrentTimestamp() < bill.getDeadline()) {
            bill.setStatus(BillStatus.AHEAD.getKey());
        }
        billService.update(bill);

        //更新贷款订单状态
        Loan loan = loanService.findById(bill.getLoanId());
        if (null != loan) {
            loan.setLoanStatus(LoanStatus.CLOSED.getKey());
            loan.setUpdateTime(S.getCurrentTimestamp());
            loanService.update(loan);
        }

        //创建还款操作记录
        ActionRecord actionRecord = new ActionRecord(bill.getBillId(), 2, bill.getUserId(), 2, "", bill.getStatus());
        actionRecordService.save(actionRecord);

        //修改用户认证状态
        UserInfo userInfo = userInfoService.findById(bill.getUserId());
        if (null != userInfo) {
            userInfo.setZhimaVerify(0);
            userInfo.setOperatorVerify(0);
            userInfo.setBankVerify(0);
            userInfo.setContactVerify(0);
            userInfo.setUpdateTime(S.getCurrentTimestamp());
            userInfoService.update(userInfo);
        }

        String content = "尊敬的客户，您申请的借款 " + loan.getLoanId() + " 已与 " + S.getCurDate() + " 还款 " + moneyCalculateService.fenToYuan(Integer.valueOf(transactionFee)) + " 元，其中账单累计应还金额 " + moneyCalculateService.fenToYuan(Integer.valueOf(transactionFee)) + " 元，本期借款账单已还请，感谢您的使用。";
        //站内消息
        messageService.sendMessage(bill.getUserId(), "您的账单已还清，感谢使用", content);

        //推送
        odinService.sendCommonPush(bill.getUserId(), content, OdinPushType.REPAYED_ALL.getStrkey(), new HashMap<>());

        logger.info("==========================异步回调结束=====================");
        return "success";
    }


    @PostMapping(value = "rePay")
    public Map<String, Object> rePay(@In User user, @NotBlank(message = "billId不能为空") String billId,
                                     @NotNull(message = "amount不能为空") BigDecimal amount) {
        Bill bill = billService.findById(billId);
        if (null == bill) {
            throw new PromptException("非法的账单号");
        }

        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            throw new PromptException("支付金额不能为0");
        }

        // 支付请求参数
        Map<String, Object> payParams = payService.buildCommonPayParams("有借用户还款", amount, "bill/replaymentNotily");
        String payId = payParams.get("bill_no").toString();
        // 调支付
        Map<String, Object> returnMsg = payService.csPay(config.getComsunnyBillApi(), payParams);
        // 支付返回
        JSONObject returnJson = JSON.parseObject(String.valueOf(returnMsg.get("resp_params")));
        // 创建支付流水
        PayFlow payFlow = new PayFlow(billId, TradeType.TYPE_REPAYMENT.getKey(), user.getUserId(), user.getMobile(),
                "有借用户还款", amount.multiply(new BigDecimal(100)).intValue(), 1,
                JSON.toJSONString(returnMsg.get("req_params")));
        payFlow.setPayId(payId);
        payService.save(payFlow);
        // 发起支付返回
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("errDetail", returnJson.getString("err_detail"));
        data.put("codeUrl", returnJson.getString("code_url"));
        data.put("billId", billId);
        data.put("payId", payId);
        return data;
    }

}
