package isec.loan.controller;

import com.alibaba.fastjson.JSONObject;
import isec.base.util.S;
import isec.loan.common.In;
import isec.loan.core.PromptException;
import isec.loan.core.StatusCodeEnum;
import isec.loan.entity.User;
import isec.loan.entity.UserInfo;
import isec.loan.service.AlipayService;
import isec.loan.service.UserInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.constraints.NotBlank;

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
    @Autowired
    AlipayService alipayService;



    @Value("${config.aliAppId}")
    private String oauthAPPID;
    @Value("${config.aliPrivateKey}")
    private String oauthAPP_PRIVATE_KEY;
    @Value("${config.aliPublicKey}")
    private String oauthALIPAY_PUBLIC_KEY;


    /**
     * 绑定支付宝账号
     *
     * @return
     */
    @PostMapping(value = "bindAliAccount")
    public void bindAliAccount(@In User user, @NotBlank(message = "请传入支付宝授权码") String authCode) {

        JSONObject grant = alipayService.grant(authCode);

        //绑定支付宝号
        UserInfo userInfo = UserInfoService.findBy("alipay_account", grant.getString("user_id"));
        if (null != userInfo) {
            throw new PromptException("该支付宝已被绑定");
        }
        userInfo = UserInfoService.findById(user.getUserId());
        if (null == userInfo) {
            throw new PromptException(StatusCodeEnum.USER_INFO_ERROR);
        }
        userInfo.setAlipayAccount(grant.getString("user_id"));
        userInfo.setAlipayNickName(grant.getString("nick_name"));
        userInfo.setAlipayVerify(1);
        userInfo.setUpdateTime(S.getCurrentTimestamp());
        UserInfoService.update(userInfo);

    }


}
