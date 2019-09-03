package isec.loan.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import isec.base.bean.MapBox;
import isec.base.util.Md5;
import isec.base.util.S;
import isec.loan.common.In;
import isec.loan.configurer.Config;
import isec.loan.core.PromptException;
import isec.loan.entity.Bill;
import isec.loan.entity.Loan;
import isec.loan.entity.PayFlow;
import isec.loan.entity.User;
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

    @Autowired
    TelegramService telegramService;

    private Logger logger = LoggerFactory.getLogger(BillController.class);


    /**
     * 领取借款
     *
     * @param loanId 贷款记录编号
     */
    @PostMapping(value = "receiveLoan")
    public void receiveLoan(@In User user, @NotBlank(message = "请传入贷款编号loanId") String loanId) {
        boolean result = billService.createBill(loanId, "");
        if (!result) {
            throw new PromptException("领取借款失败，请稍后再试");
        }
    }


    /**
     * 重新借款
     */
    @PostMapping(value = "againLoan")
    public void againLoan(@NotBlank(message = "请传入贷款编号loanId") String loanId,
                          @NotBlank(message = "operatorId不能为空") String operatorId) {
        JSONObject remark = new JSONObject();
        remark.put("operatorId", operatorId);
        billService.createBill(loanId, remark.toJSONString());
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
     * @param billId
     * @return
     */
    @PostMapping(value = "billDetail")
    public JSONObject billDetail(String billId) {

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
        result.put("repayment_amount", moneyCalculateService.fenToYuan(moneyCalculateService.getNeedMoney(billId)));
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
     * 用户发起还款支付接口
     *
     * @param user
     * @param billId
     * @param amount
     * @return
     */
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
        Map<String, Object> payParams = payService.buildCommonPayParams("有借支付宝还款", amount, "pay/replaymentNotily");
        String payId = payParams.get("bill_no").toString();
        // 调支付
        Map<String, Object> returnMsg = payService.csPay(config.getComsunnyBillApi(), payParams);
        if (null == returnMsg) {
            telegramService.sendTgMsg3(TgType.REPAY.getKey(), user.getUserId(), payId);
        }
        // 支付返回
        JSONObject returnJson = JSON.parseObject(String.valueOf(returnMsg.get("resp_params")));
        // 创建支付流水
        PayFlow payFlow = new PayFlow(billId, TradeType.TYPE_REPAYMENT.getKey(), user.getUserId(), user.getMobile(),
                "有借支付宝还款", amount.multiply(new BigDecimal(100)).intValue(), 1,
                JSON.toJSONString(returnMsg.get("req_params")));
        payFlow.setPayId(payId);
        payService.save(payFlow);

        if (0 != returnJson.getIntValue("result_code")) {
            telegramService.sendTgMsg3(TgType.REPAY.getKey(), user.getUserId(), payId);
        }

        // 发起支付返回
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("errDetail", returnJson.getString("err_detail"));
        data.put("codeUrl", returnJson.getString("code_url"));
        data.put("billId", billId);
        data.put("payId", payId);
        return data;
    }


    /**
     * 后台部分还款
     *
     * @param outTradeNo
     * @param billId
     * @param amount
     * @param payType
     * @param operatorId
     */
    @PostMapping(value = "manualRePay")
    public void manualRePay(@NotBlank(message = "outTradeNo不能为空") String outTradeNo,
                            @NotBlank(message = "billId不能为空") String billId, @NotNull(message = "amount不能为空") BigDecimal amount,
                            @NotBlank(message = "payType不能为空") String payType, @NotBlank(message = "operatorId不能为空") String operatorId, @NotBlank(message = "请传入tg验证码tgCode") String tgCode) {

        //校验tg验证码
        telegramService.verify(billId, tgCode);

        JSONObject remark = new JSONObject();
        remark.put("operatorId", operatorId);

        PayFlow payFlow = new PayFlow(billId, TradeType.TYPE_MANUAL.getKey(), "", "",
                "手动还款", amount.multiply(new BigDecimal(100)).intValue(), Integer.valueOf(payType), "");
        payFlow.setOutTradeNo(outTradeNo);
        payFlow.setStatus(TradeStatus.TRADE_SUCCESS.getKey());
        payFlow.setRequestParam(remark.toJSONString());
        payFlow.setUpdateTime(S.getCurrentTimestamp());
        String result = payService.repay(payFlow);
        if (!"success".equals(result)) {
            throw new PromptException(result);
        }
    }

    /**
     * 订单核对
     *
     * @param payId
     */
    @PostMapping(value = "checkOrder")
    public void checkOrder(@NotBlank(message = "支付流水号payId不能为空") String payId) {

        PayFlow payFlow = payService.findById(payId);
        if (null == payFlow) {
            throw new PromptException("流水记录payId不存在");
        }
        if (payService.checkOrder(payId)) {
            payFlow.setStatus(2);
            payFlow.setUpdateTime(S.getCurrentTimestamp());
            payService.update(payFlow);
        }
    }

}
