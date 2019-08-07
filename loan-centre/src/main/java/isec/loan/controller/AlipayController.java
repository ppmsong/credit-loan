package isec.loan.controller;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.request.ZhimaCreditScoreBriefGetRequest;
import com.alipay.api.request.ZhimaCreditScoreGetRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.ZhimaCreditScoreBriefGetResponse;
import com.alipay.api.response.ZhimaCreditScoreGetResponse;
import isec.base.util.S;
import isec.loan.common.In;
import isec.loan.core.PromptException;
import isec.loan.core.StatusCodeEnum;
import isec.loan.entity.User;
import isec.loan.entity.UserInfo;
import isec.loan.entity.enums.VerifyStatus;
import isec.loan.service.UserInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

/**
 * 芝麻信用分
 * Created by p on 2019/07/22.
 */
@RestController
@RequestMapping(value = "alipay")
@Validated
public class AlipayController {

    private Logger logger = LoggerFactory.getLogger(AlipayController.class);


    @Autowired
    UserInfoService UserInfoService;

    private String zhimaAPPID;
    private String zhimaAPP_PRIVATE_KEY;
    private String zhimaALIPAY_PUBLIC_KEY;

    @Value("${alipay.oauthAPPID}")
    private String oauthAPPID;
    @Value("${alipay.oauthAPP_PRIVATE_KEY}")
    private String oauthAPP_PRIVATE_KEY;
    @Value("${alipay.oauthALIPAY_PUBLIC_KEY}")
    private String oauthALIPAY_PUBLIC_KEY;


    /**
     * 芝麻授信
     *
     * @param authCode 授权码
     * @return
     */
    @PostMapping(value = "grantCreditScore")
    public Map<String, Object> grantCreditScore(@In User user, @NotBlank(message = "请输入授权码") String authCode) {
        Map<String, Object> data = new HashMap<>();


        //初始化sdk
        //AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", "app_id", "your_private_key", "json", "GBK", "alipay_public_key");
        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", oauthAPPID, oauthAPP_PRIVATE_KEY, "json", "UTF-8", oauthALIPAY_PUBLIC_KEY, "RSA2");

        //授权
        AlipaySystemOauthTokenRequest grantReq = new AlipaySystemOauthTokenRequest();
        grantReq.setCode(authCode);
        grantReq.setGrantType("authorization_code");
        String accessToken = null;
        try {
            AlipaySystemOauthTokenResponse oauthTokenResponse = alipayClient.execute(grantReq);
            accessToken = oauthTokenResponse.getAccessToken();
            logger.info("授权返回accessToken：{}", accessToken);
            //ISV权限不足，建议在开发者中心检查签约是否已经生效
            //获取芝麻信用
            ZhimaCreditScoreGetRequest creditScoreRequest = new ZhimaCreditScoreGetRequest();
            creditScoreRequest.setBizContent("{" +
                    " \"transaction_id\":\"" + S.createReqNo() + "\"," +
                    " \"product_code\":\"w1010100100000000001\"" +
                    " }");
            ZhimaCreditScoreGetResponse response = alipayClient.execute(creditScoreRequest, accessToken);
            if (!response.isSuccess()) {
                throw new PromptException("查询芝麻分错误:" + response.getBody());
            }
            UserInfo userInfo = UserInfoService.findById(user.getUserId());
            if (null == userInfo) {
                throw new PromptException(StatusCodeEnum.USER_INFO_ERROR);
            }

            userInfo.setZhimaScore(Integer.parseInt(response.getZmScore()));
            userInfo.setZhimaVerify(1);
            userInfo.setUpdateTime(S.getCurrentTimestamp());
            UserInfoService.update(userInfo);

        } catch (AlipayApiException e) {
            //处理异常
            logger.error("获取芝麻信用异常{}", e);
            throw new PromptException("获取芝麻信用异常" + e.getErrMsg());
        }

        return data;
    }


    /**
     * 绑定支付宝账号
     *
     * @return
     */
    @PostMapping(value = "bindAliAccount")
    public void bindAliAccount(@In User user, @NotBlank(message = "请传入支付宝授权码") String authCode) {

        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", oauthAPPID, oauthAPP_PRIVATE_KEY, "json", "UTF-8", oauthALIPAY_PUBLIC_KEY, "RSA2");
        //创建API对应的request类
        AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
        request.setGrantType("authorization_code");
        request.setCode(authCode);
        //通过alipayClient调用API，获得对应的response类
        AlipaySystemOauthTokenResponse response = null;
        try {
            logger.info("获取授权令牌请求:{}", JSON.toJSON(request));
            response = alipayClient.execute(request);
            logger.info("获取授权令牌返回:{}", response.getBody());
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        //2088开头支付宝号
        String userId = response.getUserId();
        if (S.isBlank(userId)) {
            throw new PromptException("支付宝授权失败");
        }

        //绑定支付宝号
        UserInfo UserInfo = UserInfoService.findBy("alipay_account", userId);
        if (null != UserInfo) {
            throw new PromptException("该支付宝已被绑定");
        }
        UserInfo = UserInfoService.findById(user.getUserId());
        if (null == UserInfo) {
            throw new PromptException(StatusCodeEnum.USER_INFO_ERROR);
        }
        UserInfo.setAlipayAccount(userId);
        UserInfo.setUpdateTime(S.getCurrentTimestamp());
        UserInfoService.update(UserInfo);

    }

    /**
     * 获取芝麻分(惠普版)
     *
     * @param authCode
     * @return
     */
    @PostMapping(value = "1111")
    public Map<String, Object> queryCreditScore2(@RequestParam(name = "auth_code") String authCode) {
        Map<String, Object> data = new HashMap<>();
        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", zhimaAPPID, zhimaAPP_PRIVATE_KEY, "json", "UTF-8", zhimaALIPAY_PUBLIC_KEY);

        try {
            ZhimaCreditScoreBriefGetRequest request = new ZhimaCreditScoreBriefGetRequest();
            request.setBizContent("{" +
                    "    \"transaction_id\":\"201512100936588040000000465158\"," +
                    "    \"product_code\":\"w1010100000000002733\"," +
                    "    \"cert_type\":\"IDENTITY_CARD\"," +
                    "    \"cert_no\":\"5425211970****0009\"," +
                    "    \"name\":\"张三\"," +
                    "    \"admittance_score\":650" +
                    "  }");
            ZhimaCreditScoreBriefGetResponse response = alipayClient.execute(request);
            if (response.isSuccess()) {
                logger.info("调用成功");
            } else {
                logger.warn("调用失败");
            }
        } catch (Exception e) {
            logger.error("获取芝麻信用异常{}", e);
        }


        return data;
    }
}
