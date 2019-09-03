package isec.loan.controller;

import isec.base.util.sms.PushSms;
import org.junit.Test;

public class CaptchaControllerTest {


    @Test
    public void verifyCaptcha() {

        PushSms.sendSms("14b463cb38c02f0ea9fc09700e915959", "【有借app】您正在进行登陆操作，需要短信验证身份，验证码为：88888，在5分钟内完成验证，如非本人操作请忽视。", "18621788748", "https://sms.yunpian.com/v2/sms/single_send.json");

    }
}