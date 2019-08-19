package isec.loan.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import isec.base.util.Md5;
import isec.base.util.S;
import isec.base.util.http.HttpClientManager;
import isec.loan.configurer.Config;
import isec.loan.core.AbstractService;
import isec.loan.entity.PayFlow;

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
    
    @Autowired
    Config config;

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
            result.put("bill_no",JSONObject.parseObject(response).getString("bill_no"));
            result.put("req", JSONObject.parse(params));
            result.put("callback", JSONObject.parse(response));
            result.put("code", "ok");

            //交易失败
            if (null == response || (!JSONObject.parseObject(response).getString("result_code").equals("1"))) {
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

}
