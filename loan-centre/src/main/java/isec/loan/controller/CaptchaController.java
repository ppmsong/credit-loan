package isec.loan.controller;

import com.alibaba.fastjson.JSONObject;
import isec.base.util.S;
import isec.base.util.geetest.GeetestLib;
import isec.base.util.sms.PushSms;
import isec.loan.common.redis.Redis;
import isec.loan.configurer.Config;
import isec.loan.core.PromptException;
import isec.loan.core.StatusCodeEnum;
import isec.loan.entity.User;
import isec.loan.entity.enums.SmsCodeType;
import isec.loan.service.SmsCodeService;
import isec.loan.service.UserService;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 极验验证码
 * Created by p on 2019/07/18.
 */
@RestController
@RequestMapping(value = "captcha")
@Validated
public class CaptchaController {

    @Autowired
    Config config;
    @Autowired
    Redis redis;
    @Autowired
    SmsCodeService smsCodeService;
    @Autowired
    UserService userService;

    @PostMapping(value = "startCaptcha")
    public String startCaptcha(@NotEmpty(message = "请传入用户标识") @RequestParam(name = "user_id") String userId, @NotEmpty(message = "请传入客户端IP") @RequestParam(name = "ip_address") String ipAddress) {
        GeetestLib gtSdk = new GeetestLib(config.getGeetestId(), config.getGeetestKey(),
                true);
        //自定义参数,可选择添加
        HashMap<String, String> param = new HashMap<String, String>();
        //网站用户id
        param.put("user_id", userId);
        //web:电脑上的浏览器；h5:手机上的浏览器，包括移动应用内完全内置的web_view；native：通过原生SDK植入APP应用的方式
        param.put("client_type", "native");
        //传输用户请求验证时所携带的IP
        param.put("ip_address", ipAddress);

        //进行验证预处理
        int gtServerStatus = gtSdk.preProcess(param);

        //将服务器状态设置到redis中
        redis.setObject(gtSdk.gtServerStatusSessionKey, gtServerStatus, -1);

        String resStr = gtSdk.getResponseStr();

        return resStr;

    }

    @PostMapping(value = "verifyCaptcha")
    public Map<String, Object> verifyCaptcha(@NotEmpty(message = "手机号码不能为空") @Length(min = 11, max = 11) String mobile, @RequestParam(name = "verify_type") @NotEmpty(message = "验证码类型不能为空") String verifyType, @NotEmpty(message = "请传入用户标识") @RequestParam(name = "user_id") String userId, @NotEmpty(message = "请传入客户端IP") @RequestParam(name = "ip_address") String ipAddress, @NotEmpty(message = "请传入极验验证二次验证表单数据geetest_challenge") @RequestParam(name = "geetest_challenge") String challenge, @NotEmpty(message = "请传入极验验证二次验证表单数据geetest_validate") @RequestParam(name = "geetest_validate") String validate, @NotEmpty(message = "请传入极验验证二次验证表单数据geetest_seccode") @RequestParam(name = "geetest_seccode") String seccode) {
        // 已注册过的手机号不发送注册验证码
        User user = userService.findBy("mobile", mobile);
        if (SmsCodeType.TYPE_REG.getStrkey().equals(verifyType)) {
            if (null != user) {
                throw new PromptException(StatusCodeEnum.MOBILE_HAS_REGED);
            }
        }
        GeetestLib gtSdk = new GeetestLib(config.getGeetestId(), config.getGeetestKey(),
                true);

        //获取gt-server状态
        Object obj = redis.getObject(gtSdk.gtServerStatusSessionKey, Object.class);
        int gt_server_status_code = -1;
        if (obj != null) {
            gt_server_status_code = (Integer) obj;
        }

        //自定义参数,可选择添加
        HashMap<String, String> param = new HashMap<String, String>();
        //网站用户id
        param.put("user_id", userId);
        //web:电脑上的浏览器；h5:手机上的浏览器，包括移动应用内完全内置的web_view；native：通过原生SDK植入APP应用的方式
        param.put("client_type", "native");
        //传输用户请求验证时所携带的IP
        param.put("ip_address", ipAddress);

        int gtResult = 0;
        if (gt_server_status_code == 1) {
            //gt-server正常，向gt-server进行二次验证
            gtResult = gtSdk.enhencedValidateRequest(challenge, validate, seccode, param);
        } else {
            // gt-server非正常情况下，进行failback模式验证,使用钱包自己的验证
            gtResult = gtSdk.failbackValidateRequest(challenge, validate, seccode);
        }

        JSONObject data = new JSONObject();
        data.put("result", gtResult);
        data.put("version", gtSdk.getVersionInfo());

        redis.del(gtSdk.gtServerStatusSessionKey);

        if (gtResult == 1) {
            // 验证成功
            data.put("status", "success");

            // #发送短信
            // 1.生成验证码
            String smsCode = S.getSix();
            // 2.验证码存入缓存
            smsCodeService.sendSmsCode(Integer.parseInt(verifyType), mobile, smsCode, "");
            // 3.向手机发送验证码
            // 密码找回
            String text = "";
            if (SmsCodeType.TYPE_RESET_PWD.getStrkey().equals(verifyType)) {
                text = config.getSmsHead() + "您正在进行手机密码找回，验证码为：" + smsCode + "，请在5分钟内完成验证。";
            }
            // 提现
            if (SmsCodeType.TYPE_WITHDRAW.getStrkey().equals(verifyType)) {
                text = config.getSmsHead() + "您正在进行提现操作，需要短信验证身份，验证码为：" + smsCode + "，在5分钟内完成验证，如非本人操作请忽视。";
            }
            //注册
            if (SmsCodeType.TYPE_REG.getStrkey().equals(verifyType) || SmsCodeType.TYPE_BIND_ALIPAY.getStrkey().equals(verifyType)) {
                text = config.getSmsHead() + "您正在进行手机验证码注册，验证码为：" + smsCode + "，请在5分钟内完成验证。";
            }
            //登录
            if (SmsCodeType.TYPE_LOGIN.getStrkey().equals(verifyType)) {
                text = config.getSmsHead() + "您正在进行登陆操作，需要短信验证身份，验证码为：" + smsCode + "，在5分钟内完成验证，如非本人操作请忽视。";
            }
            PushSms.sendSms(config.getSMSYUNPIANKEY(), text, mobile, config.getSMSYUNPIANAPI());

            data.put("smsCode", smsCode);
            return data;
        } else {
            // 验证失败
            data.put("status", "fail");
            throw new PromptException(StatusCodeEnum.FAIL);
        }

    }

}
