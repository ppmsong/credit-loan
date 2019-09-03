package isec.loan.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.request.AlipayUserInfoShareRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayUserInfoShareResponse;
import isec.loan.core.PromptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AlipayService {

    private Logger logger = LoggerFactory.getLogger(AlipayService.class);


    @Value("${config.aliAppId}")
    private String APP_ID;
    @Value("${config.aliPrivateKey}")
    private String APP_PRIVATE_KEY;
    @Value("${config.aliPublicKey}")
    private String ALIPAY_PUBLIC_KEY;
    @Value("${config.pid}")
    private String pid;

    /**
     * 获取用户授权信息
     *
     * @param authCode
     * @return
     */
    public JSONObject grant(String authCode) {
        JSONObject result = new JSONObject();

        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", APP_ID, APP_PRIVATE_KEY, "json", "UTF-8", ALIPAY_PUBLIC_KEY, "RSA2");
        //创建API对应的request类
        AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
        request.setGrantType("authorization_code");
        request.setCode(authCode);
        //通过alipayClient调用API，获得对应的response类
        AlipaySystemOauthTokenResponse response = null;
        try {
            logger.info("获取授权令牌请求:{}", JSON.toJSON(request));
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        JSONObject oauthTokenResult = JSONObject.parseObject(response.getBody());
        logger.info("获取授权令牌返回:{},{}", response.getUserId(), oauthTokenResult);

        //根据response中的结果继续业务逻辑处理
        JSONObject oauthTokenResponse = oauthTokenResult.getJSONObject("alipay_system_oauth_token_response");
        if (null == oauthTokenResponse) {
            throw new PromptException("支付宝获取授权令牌异常：" + oauthTokenResult);
        }

        //获取支付宝授权信息
        AlipayUserInfoShareRequest request2 = new AlipayUserInfoShareRequest();
        AlipayUserInfoShareResponse response2;
        try {
            response2 = alipayClient.execute(request2, response.getAccessToken());
            logger.info("支付宝获取用户信息返回：{}", response2.getBody());
            if (response.isSuccess()) {
                JSONObject userInfoRespose = JSONObject.parseObject(response2.getBody());
                JSONObject userInfoRes = userInfoRespose.getJSONObject("alipay_user_info_share_response");
                result.put("nick_name", userInfoRes.getString("nick_name"));
                result.put("gender", "F".equals(userInfoRes.getString("gender")) ? 2 : 1);
                result.put("avatar", userInfoRes.getString("avatar"));
            }
        } catch (AlipayApiException e) {
            throw new PromptException("支付宝获取授权令牌异常：{}",e.getMessage());
        }

        result.put("user_id", response.getUserId());
        result.put("access_token", response.getAccessToken());

        return result;

    }



}
