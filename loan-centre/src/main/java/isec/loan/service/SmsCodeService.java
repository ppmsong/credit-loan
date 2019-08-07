package isec.loan.service;

import isec.base.util.S;
import isec.loan.common.redis.Redis;
import isec.loan.entity.enums.SmsCodeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmsCodeService {

    //5分钟
    private final static int seconds = 5 * 60;

    @Autowired
    private Redis redis;


    /**
     * 短信验证码放入缓存
     *
     * @param verifyType
     * @param mobile
     * @param verifyCode
     * @param deviceno
     * @return
     */
    public String sendSmsCode(int verifyType, String mobile, String verifyCode, String deviceno) {
        String key = getKey(verifyType, mobile, deviceno);
        redis.setObject(key, verifyCode, seconds);
        return verifyCode;
    }

    /**
     * 获取短信验证码
     *
     * @param verifyType
     * @param mobile
     * @param deviceno
     * @return
     */
    public String getSmsCode(int verifyType, String mobile, String deviceno) {
        String key = getKey(verifyType, mobile, deviceno);
        String smsCode = (String) redis.getObject(key, Object.class);
        return smsCode;
    }

    /**
     * 验证短信验证码
     *
     * @param verifyType
     * @param mobile
     * @param verifyCode
     * @param deviceno
     * @return
     */
    public boolean verifySmsCode(int verifyType, String mobile, String verifyCode, String deviceno) {
        String key = getKey(verifyType, mobile, deviceno);
        String code = (String) redis.getObject(key, Object.class);
        return null != code && verifyCode.equals(code);
    }

    /**
     * 获取短信验证码的key
     *
     * @param verifyType 验证码类型
     * @param mobile     手机号
     * @return
     */
    private String getKey(int verifyType, String mobile, String deviceno) {
        String key = SmsCodeType.prefix;
        key += ":" + verifyType + ":" + mobile;
        deviceno = S.isBlank(deviceno) ? "deviceno" : deviceno;
        key += ":" + deviceno;
        return key;
    }


    public void delKey(String key) {
        redis.del(key);
    }

}
