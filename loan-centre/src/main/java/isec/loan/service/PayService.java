package isec.loan.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import isec.base.util.Md5;
import isec.base.util.S;
import isec.base.util.http.HttpClientManager;
import isec.loan.configurer.Config;
import isec.loan.core.AbstractService;
import isec.loan.entity.*;
import isec.loan.entity.enums.BillStatus;
import isec.loan.entity.enums.LoanStatus;
import isec.loan.entity.enums.OdinPushType;
import isec.loan.entity.enums.TradeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 */
@Service
public class PayService extends AbstractService<PayFlow> {

    private Logger logger = LoggerFactory.getLogger(PayService.class);

    @Value("${transfer.key}")
    private String key;
    @Value("${transfer.merName}")
    private String merName;
    @Value("${transfer.url}")
    private String url;
    @Value("${config.appId}")
    private String appId;
    @Value("${config.appSign}")
    private String appSign;
    @Value("${config.comsunny}")
    private String payUrl;


    @Autowired
    Config config;
    @Autowired
    BillService billService;
    @Autowired
    MoneyCalculateService moneyCalculateService;
    @Autowired
    LoanService loanService;
    @Autowired
    ActionRecordService actionRecordService;
    @Autowired
    MessageService messageService;
    @Autowired
    OdinService odinService;
    @Autowired
    UserInfoService userInfoService;
    @Autowired
    UserService userService;
    @Autowired
    TelegramService telegramService;

    /**
     * 交易成功返回模板：
     * <p>
     * {"req_params":{"payee_account":"13207199825","sign":"45580b428648ab67c15c0fe3eeb550a2","bill_no":"20181020112732312461967","trans_amt":"13","mer_name":"czkb","payee_type":"ALI","timestamp":1540006052312},"resp_params":{"pay_date":1540006052000,"channel_bill_no":"20181020110070001502700042892703","err_detail":"交易成功","result_code":"1","requset_params":"{\"out_biz_no\":20181020112732312461967,\"payee_type\":\"ALIPAY_LOGONID\",\"payee_account\":\"13207199825\",\"amount\":\"0.13\"}","response_result":{"msg":"Success","pay_date":"2018-10-20
     * 11:27:32","code":"10000","out_biz_no":"20181020112732312461967","order_id":"20181020110070001502700042892703"}}}
     * <p>
     * <p>
     * 交易失败返回模板：
     * <p>
     * {"pay_date":0,"err_detail":"{\"alipay_fund_trans_toaccount_transfer_response\":{\"code\":\"40004\",\"msg\":\"Business
     * Failed\",\"sub_code\":\"PAYER_BALANCE_NOT_ENOUGH\",\"sub_msg\":\"付款方余额不足\",\"out_biz_no\":\"20181102110634145739653\"},\"sign\":\"XRmWX4WZfFaU/4DCOLao4GGFYd4PpjtEa8HVmyyo0i0oZeqxw9cvfSFrMPOG7boXUMR7zMjJuCFz42FPZiwd7XonkFKL+ckv5iPsGl2fBn1y1/zjgP/u+nXJS308CgkVozLwu1dmAcau845h+pHBisMomIxL/mRjXwGB1b0L0521zrhy6RHkh7ToV9TuaXd5o5VDYq+SneD3JEUEZ2uwmhyLUFyqgIn9Gk+vRIddH21wFUR+mro5E0Q0zrNMvhrj0m+AoSf7pZEWRkU9mmSoGTz8eeInG1mF48cJwiNyoy+v4iQChcvVA7eDtLSs7ggCxjDiz/tElYQc8dQzbvaJRw==\"}","result_code":"2","requset_params":"{\"out_biz_no\":20181102110634145739653,\"payee_type\":\"ALIPAY_LOGONID\",\"payee_account\":\"13207199825\",\"amount\":\"4.95\"}","response_result":{"msg":"Business
     * Failed","code":"40004","sub_msg":"付款方余额不足","sub_code":"PAYER_BALANCE_NOT_ENOUGH","out_biz_no":"20181102110634145739653"}}
     */

    public JSONObject bill(String billNo, String payeeAccount, String transAmt) {
        JSONObject result = new JSONObject();

        Map<String, Object> requestParam = new HashMap<String, Object>();
        String payee_type = "ALI";
        //手机号或者邮箱
//		String ali_payee_type = "ALIPAY_LOGONID";
        //以2088开头的16位纯数字组成
        String ali_payee_type = "ALIPAY_USERID";
        String payee_account = payeeAccount;
        long timestamp = System.currentTimeMillis();
        String s = merName + billNo + transAmt + payee_type + payee_account + timestamp + key;

        logger.info("待签名字符串:" + s);
        String sign = Md5.md5(s);
        logger.info("签名后字符串:" + sign);
        requestParam.put("mer_name", merName);
        requestParam.put("bill_no", billNo);
        // 分
        requestParam.put("trans_amt", transAmt);
        requestParam.put("payee_type", payee_type);
        // 支付宝收款账号类型
        requestParam.put("ali_payee_type", ali_payee_type);
        // 支付宝账号
        requestParam.put("payee_account", payee_account);
        requestParam.put("sign", sign);
        requestParam.put("timestamp", timestamp);
        String params = JSONObject.toJSONString(requestParam);


        try {

            logger.info("出款请求参数：{}", params);
            String response = HttpClientManager.getClient().httpPost(url, params);
            logger.info("出款返回参数：{}", response);

            //封装返回参数
            result.put("bill_no", JSONObject.parseObject(response).getString("bill_no"));
            result.put("req", JSONObject.parse(params));
            result.put("callback", JSONObject.parse(response));

            if (!S.isBlank(response) && JSONObject.parseObject(response).getString("result_code").equals("1")) {
                result.put("code", "ok");
            } else {
                result.put("code", "fail");
            }
        } catch (Exception e) {
            result.put("code", "fail");
        }

        return result;

    }


    /**
     * 普通支付
     * 拼装发起支付参数
     *
     * @param title
     * @param payAmount
     * @param notifySuffix
     */
    public Map<String, Object> buildCommonPayParams(String title, BigDecimal payAmount, String notifySuffix) {
        Map<String, Object> payParams = new HashMap<>();
        payParams.put("app_id", config.getAppId());
        payParams.put("app_sign", config.getAppSign());
        payParams.put("bill_no", "P" + S.createReqNo());
        payParams.put("title", title);
        payParams.put("total_fee", payAmount);
        payParams.put("notify_url", config.getNofify_url() + notifySuffix);
        return payParams;
    }


    /**
     * 发起支付
     * { "app_url": null, "code_url":
     * "https://qr.alipay.com/bax07163nxvhz8nunab9807e?t=1542792356611",
     * "err_detail": "OK", "id": "92064334-d2e2-466a-b7c6-17c3ecd6217c",
     * "result_code": 0 }
     */
    public Map<String, Object> csPay(String csUrl, Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long timestamp = System.currentTimeMillis();
        String appId = String.valueOf(params.get("app_id"));
        String appSign = String.valueOf(params.get("app_sign"));
        String billNo = String.valueOf(params.get("bill_no"));
        String realIp = String.valueOf(params.get("real_ip"));
        String title = String.valueOf(params.get("title"));
        String totalFee = String.valueOf(params.get("total_fee"));
        String notifyUrl = String.valueOf(params.get("notify_url"));
        Map<String, Object> createOrderParam = new HashMap<>();
        // CS_WX_WAP 微信
        createOrderParam.put("channel", "CS_ALI_WAP");
        createOrderParam.put("app_id", appId);
        logger.info("appId:{} timestamp:{} appSign:{}", appId, timestamp, appSign);
        String str = appId + timestamp + appSign;
        logger.info("本地加密前：" + str);
        String md5 = Md5.md5(str);
        logger.info("本地加密后:" + md5);
        createOrderParam.put("app_sign", md5);
        createOrderParam.put("bill_timeout", 3000);
        createOrderParam.put("bill_no", billNo);
        createOrderParam.put("title", title);
        createOrderParam.put("real_ip", "39.108.90.207");
        createOrderParam.put("total_fee",
                new BigDecimal(totalFee).multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_DOWN));
        createOrderParam.put("timestamp", timestamp);
        createOrderParam.put("expressType", "0");
        createOrderParam.put("userId", "123");
        createOrderParam.put("expressPcOrMobile", "mobile");
        createOrderParam.put("return_url", config.getReturn_url());
        createOrderParam.put("notify_url", notifyUrl);
        String createOrderParams = JSONObject.toJSONString(createOrderParam);
        logger.info("发起支付参数：" + JSONObject.toJSONString(createOrderParam));
        String returnMsg = HttpClientManager.getClient().httpPost(csUrl, createOrderParams);
        logger.info("cs支付接口返回：" + returnMsg);

        result.put("req_params", JSON.toJSON(createOrderParam));
        result.put("resp_params", JSON.toJSON(returnMsg));
        return result;
    }

    /**
     * 订单核对
     *
     * @param billNo 要核对的订单号
     * @return
     */
    public boolean checkOrder(String billNo) {

        JSONObject param = new JSONObject();
        String url = payUrl + "/2/rest/bills";
        long timestamp = System.currentTimeMillis();
        param.put("app_id", appId);
        param.put("timestamp", timestamp);
        param.put("app_sign", Md5.getCsMD5(appId + timestamp + appSign, "UTF-8"));
        param.put("bill_no", billNo);
        param.put("spay_result", true);
        logger.info("订单{}核对发起参数：{},地址：{}", billNo, JSON.toJSONString(param), url);
        url = url + "?para=" + URLEncoder.encode(JSON.toJSONString(param));
        String result = HttpClientManager.getClient().httpsGet(url);
        logger.info("订单{}核对返回参数：{}", billNo, result);
        //调用成功
        if (!S.isBlank(result) && 0 == JSONObject.parseObject(result).getIntValue("result_code")) {
            JSONObject json = JSONObject.parseObject(result).getJSONArray("bills").getJSONObject(0);
            if (json.getBoolean("spay_result")) {
                return true;
            }

        }
        return false;
    }

    public String repay(PayFlow payFlow) {
        logger.info("还款 payFlow=>" + JSONObject.toJSONString(payFlow));

        String billId = payFlow.getTradeNo();
        Bill bill = billService.findById(billId);
        if (null == bill) {
            logger.error("未找到账单记录：{}", billId);
            return "未找到账单记录：" + billId;
        }

        if (BillStatus.REPAY.getKey() == bill.getStatus()) {
            logger.warn("账单已还清");
            return "success";
        }

        int actionType = 0;
        // 手动还款
        if (TradeType.TYPE_MANUAL.getKey() == payFlow.getTradeType()) {
            logger.info("手动还款");
            User user = userService.findById(bill.getUserId());
            payFlow.setUserId(user.getUserId());
            payFlow.setMobile(user.getMobile());
            save(payFlow);
            actionType = 4;

        } else {// 支付宝付款
            logger.info("有借支付宝还款");
            update(payFlow);
            actionType = 2;
        }

        int needMoney = moneyCalculateService.getNeedMoney(bill.getBillId());
        logger.info("needMoney=" + needMoney);
        // 更新账单 订单状态 重置用户状态
        Loan loan = loanService.findById(bill.getLoanId());
        loan.setUpdateTime(S.getCurrentTimestamp());
        bill.setRepayTime(S.getCurrentTimestamp());
        bill.setUpdateTime(S.getCurrentTimestamp());
        if (needMoney <= 0) {
            logger.info("欠款已还清");
            bill.setStatus(BillStatus.REPAY.getKey());
            loan.setLoanStatus(LoanStatus.FINISHED.getKey());
            userInfoService.resetUserInfo(bill.getUserId());
        } else {
            logger.info("欠款未还清");
            bill.setStatus(BillStatus.STAGE.getKey());
        }
        billService.update(bill);
        loanService.update(loan);

        // 创建还款操作记录
        ActionRecord actionRecord = new ActionRecord(bill.getBillId(), 2, bill.getUserId(), actionType, "",
                bill.getStatus());
        actionRecordService.save(actionRecord);

        // 推送消息
        String title = "您的账单已还清，感谢使用";
        String content = "尊敬的客户，您申请的借款 " + loan.getLoanId() + " 已与 " + S.getCurDate() + " 还款 "
                + moneyCalculateService.fenToYuan(payFlow.getTotalAmount()) + " 元，其中账单累计应还金额 "
                + moneyCalculateService.fenToYuan(payFlow.getTotalAmount()) + " 元，本期借款账单已还请，感谢您的使用。";
        String odinPushType = OdinPushType.REPAYED_ALL.getStrkey();

        if (needMoney > 0) {
            title = "已成功还款" + moneyCalculateService.fenToYuan(payFlow.getTotalAmount()) + "元";
            content = "尊敬的客户，您申请的借款 " + loan.getLoanId() + " 已与 " + S.getCurDate() + " 还款 " + moneyCalculateService.fenToYuan(payFlow.getTotalAmount()) + " 元，其中账单累计应还金额 " + moneyCalculateService.getBillRepayMoney(payFlow.getTradeNo()) + " 元，还剩 " + moneyCalculateService.fenToYuan(moneyCalculateService.getNeedMoney(payFlow.getTradeNo())) + " 元未还。为维护您良好的借款信用记录并避免造成更多损失，请务必尽早还款。感谢您的使用。";
            odinPushType = OdinPushType.REPAYED_PART.getStrkey();
        }

        logger.info("还款成功开始推送消息");
        try {

            // 站内消息
            messageService.sendMessage(bill.getUserId(), title, content);
            // 推送
            odinService.sendCommonPush(bill.getUserId(), content, odinPushType,
                    new HashMap<>());

        } catch (Exception e) {
            logger.error("还款成功推送消息失败", e);
        }
        logger.info("还款成功");
        return "success";
    }


}
