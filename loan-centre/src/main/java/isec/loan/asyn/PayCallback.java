package isec.loan.asyn;

import com.alibaba.fastjson.JSONObject;
import isec.base.util.MD5Util;
import isec.base.util.S;
import isec.base.util.Tool;
import isec.loan.configurer.Config;
import isec.loan.entity.PayFlow;
import isec.loan.entity.enums.TradeStatus;
import isec.loan.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author p
 * @date 2019/07/23
 */
@RestController
@RequestMapping(value = "pay")
@Validated
public class PayCallback {

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

    private Logger logger = LoggerFactory.getLogger(PayCallback.class);



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

        PayFlow payFlow = payService.findById(transactionId);
        if (null == payFlow) {
            logger.error("不存在的订单号：{}", transactionId);
            return "不存在的订单号：" + transactionId;
        }
        if(payFlow.getStatus()==TradeStatus.TRADE_SUCCESS.getKey()) {
        	 logger.error("订单已处理成功,无需重复处理");
        	 return "success";
        }
        payFlow.setStatus(TradeStatus.TRADE_SUCCESS.getKey());
        payFlow.setOutTradeNo(outTradeNo);
        payFlow.setNotify(notify.toJSONString());
        payFlow.setUpdateTime(S.getCurrentTimestamp());
        logger.info("==========================异步回调结束=====================");
        return payService.repay(payFlow);
    }




}
