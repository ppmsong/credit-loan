package isec.loan.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

import isec.base.util.Md5;
import isec.base.util.S;
import isec.loan.common.In;
import isec.loan.common.MapBox;
import isec.loan.core.PromptException;
import isec.loan.core.StatusCodeEnum;
import isec.loan.entity.CallApi;
import isec.loan.entity.Risk;
import isec.loan.entity.User;
import isec.loan.entity.UserAppLog;
import isec.loan.entity.UserInfo;
import isec.loan.entity.enums.SmsCodeType;
import isec.loan.entity.enums.VerifyStatus;
import isec.loan.service.RemoteService;
import isec.loan.service.RiskService;
import isec.loan.service.SmsCodeService;
import isec.loan.service.UserInfoService;
import isec.loan.service.UserService;

/**
 * Created by p on 2019/07/17.
 */
@RestController
@RequestMapping(value = "user")
@Validated
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    SmsCodeService smsCodeService;

    @Autowired
    UserInfoService userInfoService;
    
    @Autowired
    RiskService riskService;
    
    @Autowired
    RemoteService remoteService;

    /**
     * 短信验证码登录
     *
     * @param mobile 手机号码
     * @param code   短信验证码
     * @return
     */
    @PostMapping(value = "loginBySmsCode")
    public Map<String, Object> loginBySmsCode(@NotBlank(message = "请输入手机号码") @Length(min = 11, max = 11) String mobile, @NotBlank(message = "请输入短信验证码") String code) {
        User user = userService.findBy("mobile", mobile);
        if (!smsCodeService.verifySmsCode(SmsCodeType.TYPE_LOGIN.getKey(), mobile, code, "")) {
            throw new PromptException(StatusCodeEnum.SMS_CODE_VALIDATE_FAIL);
        }
        if (null == user) {
            if (!S.isMob(mobile)) {
                throw new PromptException(StatusCodeEnum.MOBILE_VALIDATE_FAIL);
            }
            user = new User(mobile);
            userService.save(user);

            UserInfo UserInfo = new UserInfo(user.getUserId());
            UserInfo.setUpdateTime(S.getCurrentTimestamp());
            userInfoService.save(UserInfo);
        }

        return MapBox.instance().putAll(userService.login(user)).toMap();

    }


    /**
     * 密码登录
     *
     * @param mobile   手机号码
     * @param password 密码
     * @return
     */
    @PostMapping(value = "loginByPassword")
    public Map<String, Object> loginByPassword(@NotBlank(message = "请输入手机号码") @Length(min = 11, max = 11) String mobile, @NotBlank(message = "请输入登录密码") String password) {
        User user = userService.findBy("mobile", mobile);
        if (null == user) {
            throw new PromptException(StatusCodeEnum.ACCOUNT_NONEXISTENCE);
        }
        if (S.isBlank(user.getPassword())) {
            throw new PromptException("您未设置账号密码，请短信验证码登录后设置账号密码");
        }

        if (!Md5.md5(password + user.getSalt()).equals(user.getPassword())) {
            throw new PromptException(StatusCodeEnum.MOBILE_OR_PASSWORD_ERROR);
        }
        return MapBox.instance().putAll(userService.login(user)).toMap();

    }


    /**
     * 修改密码
     *
     * @param user
     * @param password
     * @return
     */
    @PostMapping(value = "editPassword")
    public Map<String, Object> editPassword(@In User user, @NotBlank(message = "请输入新密码") String password) {
        user.setPassword(Md5.md5(password + user.getSalt()));
        user.setUpdateTime(S.getCurrentTimestamp());
        int affectRows = userService.update(user);

        return MapBox.instance().put("affectRows", affectRows).toMap();
    }


    /**
     * 修改联系人
     *
     * @param user
     * @param contacterList
     * @return
     */
    @RequestMapping("/saveContacter")
    public Map<String, Object> saveContacter(@In User user, String contacterList) {


        UserInfo userInfo = userInfoService.findById(user.getUserId());
        userInfo.setContacter(contacterList);

        if (S.isBlank(contacterList)) {
            throw new PromptException("联系人信息不能为空");
        } else {
            userInfo.setContactVerify(VerifyStatus.YES.getKey());
        }

        int affectRows = 0;

        if (userInfoService.findById(user.getUserId()) == null) {
            affectRows = userInfoService.save(userInfo);
        } else {
            affectRows = userInfoService.update(userInfo);
        }

        return MapBox.instance().put("affectRows", affectRows).toMap();

    }


    /**
     * 银行卡绑定
     *
     * @param user
     * @param bankName
     * @param bankCardno
     * @param bankMobile
     * @return
     */
    @RequestMapping("/saveBank")
    public Map<String, Object> saveBank(@In User user, @NotBlank(message = "请输入银行名称") String bankName
            , @NotBlank(message = "请输入银行卡号") String bankCardno
            , @NotBlank(message = "请输入银行预留手机号") String bankMobile) {

        UserInfo userInfo = userInfoService.findById(user.getUserId());

        // 先实名认证
        if (userInfo.getIdcardVerify() != VerifyStatus.YES.getKey()) {
            throw new PromptException("请先实名认证");
        }

        userInfo.setBankName(bankName);
        userInfo.setBankCardno(bankCardno);
        userInfo.setBankMobile(bankMobile);
       
        //四要素认证
        JSONObject postData = new JSONObject();
        postData.put("userId",user.getUserId());
        postData.put("name", userInfo.getName());
        postData.put("idcard", userInfo.getIdcard());
        postData.put("bankcard", bankCardno);
        postData.put("mobile", bankMobile);
        JSONObject retJson = remoteService.callDx(remoteService.DX_BASE_URL + remoteService.DX_BANK_CARD_FOUR_VERIFY_URL, postData);
        String validateCode = retJson.getJSONObject("data").getString("validateCode");
        if ("1".equals(validateCode)) {
        	 userInfo.setBankVerify(VerifyStatus.YES.getKey());
        } else {
        	 userInfo.setBankVerify(VerifyStatus.FAIL.getKey());
        }
        
        int affectRows = 0;
        if (userInfoService.findById(user.getUserId()) == null) {
            affectRows = userInfoService.save(userInfo);
        } else {
            affectRows = userInfoService.update(userInfo);
        }

        return MapBox.instance().put("affectRows", affectRows).toMap();
    }

    
    
	@RequestMapping("/savePhoneBook")
	public void savePhoneBook(@In User user, String phoneBook) {
		if (S.isBlank(phoneBook)) {
			throw new PromptException("phoneBook不能为空");
		}
		Risk risk = new Risk();
		risk.setUserId(user.getUserId());
		risk.setApiKey(RiskService.PHONE_BOOK_API_KEY);
		risk.setMobile(user.getMobile());
		risk.setResponse(phoneBook);
		risk.setStatus("success");
		riskService.saveOrUpdateRisk(risk);
		
		CallApi callApi=new CallApi();
		callApi.setUserId(user.getUserId());
		callApi.setApiProvider("loan");
		callApi.setApiKey(RiskService.PHONE_BOOK_API_KEY);
 		callApi.setStatus("success");
 		riskService.saveCallApi(callApi);
 	}
	
	
	@RequestMapping("/saveUserAppLog")
	public void saveUserAppLog(@In User user,HttpServletRequest request, UserAppLog userAppLog) {
		userAppLog.setUserId(user.getUserId());
		userAppLog.setIpAddress(getCliectIp(request));
		userAppLog.setCreateTime(S.getCurrentTimestamp());
		userService.saveUserAppLog(userAppLog);
	}
	
	
	/**
     * 获取客户端ip地址
     * @param request
     * @return
     */
    public static String getCliectIp(HttpServletRequest request)
    {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.trim() == "" || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.trim() == "" || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.trim() == "" || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 多个路由时，取第一个非unknown的ip
        final String[] arr = ip.split(",");
        for (final String str : arr) {
            if (!"unknown".equalsIgnoreCase(str)) {
                ip = str;
                break;
            }
        }
        return ip;
    }
 
}
