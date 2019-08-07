package isec.loan.controller;

import com.alibaba.fastjson.JSONObject;
import isec.loan.common.In;
import isec.loan.core.PromptException;
import isec.loan.core.StatusCodeEnum;
import isec.loan.entity.User;
import isec.loan.entity.UserInfo;
import isec.loan.service.RemoteService;
import isec.loan.service.SmsCodeService;
import isec.loan.service.UserInfoService;
import isec.loan.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by p on 2019/07/22.
 */
@RestController
@RequestMapping(value = "userInfo")
@Validated
public class UserInfoController {

    @Autowired
    UserService userService;
    @Autowired
    SmsCodeService smsCodeService;
    @Autowired
    UserInfoService userInfoService;
    @Autowired
    RemoteService remoteService;

    @PostMapping(value = "getUserInfo")
    public Map<String, Object> getUserInfo(@In User user) {

        UserInfo userInfo = userInfoService.findById(user.getUserId());
        if (null == userInfo) {
            throw new PromptException(StatusCodeEnum.USER_INFO_ERROR);
        }

        Map data = new HashMap<String, Object>();
        data.put("mobile", user.getMobile());
        data.put("userId", user.getUserId());
        data.put("name", userInfo.getName());
        data.put("idcard", userInfo.getIdcard());
        data.put("alipayAccount", userInfo.getAlipayAccount());
        data.put("zhimaScore", userInfo.getZhimaScore());
        data.put("bankName", userInfo.getBankName());
        data.put("bankCardno", userInfo.getBankCardno());
        data.put("bankMobile", userInfo.getBankMobile());
        data.put("contacter", userInfo.getContacter());
        data.put("idcardVerify", userInfo.getRealIdcardVerify(userInfo));
        data.put("zhimaVerify", userInfo.getZhimaVerify());
        data.put("contactVerify", userInfo.getContactVerify());
        data.put("bankVerify", userInfo.getBankVerify());
        data.put("operatorVerify", userInfo.getOperatorVerify());

        return data;

    }

    /**
     * 实名认证
     *
     * @param name   真实姓名
     * @param idcard 身份证号码
     * @return
     */
    @PostMapping(value = "verifyRealName")
    public void verifyRealName(@In User user, @NotBlank(message = "请输入真实姓名") String name,
                               @NotBlank(message = "请输入身份证号码") String idcard) {

        UserInfo userInfo = userInfoService.findById(user.getUserId());
        if (null == userInfo) {
            throw new PromptException(StatusCodeEnum.USER_INFO_ERROR);
        }
        if (1 == userInfo.getIdcardVerify()) {
            throw new PromptException(StatusCodeEnum.USER_INFO_VERIFIED);
        }

        if (2 == userInfo.getIdcardVerify()) {
            throw new PromptException(StatusCodeEnum.USER_INFO_VERIFING);
        }

        userInfo.setName(name);
        userInfo.setIdcard(idcard);
        userInfo.setIdcardVerify(0);
        userInfoService.update(userInfo);

        JSONObject postData = new JSONObject();
        postData.put("name", name);
        postData.put("idcard", idcard);

        JSONObject retJson = remoteService.callDx(remoteService.DX_BASE_URL + remoteService.DX_NAME_CARD_VALIDATE_URL, postData);
        String validateCode = retJson.getJSONObject("data").getString("validateCode");
        if ("1".equals(validateCode)) {
            userInfo.setIdcardVerify(1);
        } else {
            userInfo.setIdcardVerify(3);
        }
        userInfoService.update(userInfo);

    }

}
