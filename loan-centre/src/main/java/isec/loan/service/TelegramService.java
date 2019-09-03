package isec.loan.service;

import com.google.common.collect.Maps;
import isec.base.util.S;
import isec.base.util.http.HttpClientManager;
import isec.loan.common.redis.Redis;
import isec.loan.core.AbstractService;
import isec.loan.core.PromptException;
import isec.loan.core.StatusCodeEnum;
import isec.loan.entity.PayFlow;
import isec.loan.entity.User;
import isec.loan.entity.UserInfo;
import isec.loan.entity.WarnRecord;
import isec.loan.entity.enums.IsDelete;
import isec.loan.entity.enums.TgType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

/**
 * @author Administrator
 */
@Service
public class TelegramService extends AbstractService<PayFlow> {

    private Logger logger = LoggerFactory.getLogger(TelegramService.class);

    private final static String prefix = "telegram";

    @Value("${spring.profiles.active}")
    private String env;
    @Value("${tg.proxyPort}")
    private int proxyPort;
    @Autowired
    Redis redis;
    @Autowired
    UserService userService;
    @Autowired
    UserInfoService userInfoService;
    @Autowired
    LoanService loanService;
    @Autowired
    WarnRecordService warnRecordService;


    /**
     * 发送tg消息
     *
     * @param content
     * @return
     * @throws IOException
     */
    public boolean sendTgMsg(String content) {

        Map<String, String> datas = Maps.newHashMap();
        String token = "805272506:AAHQLoDQSk3In7sl4VTJcmdLwlBHI3Qjg_8";
        String api = String.format("https://api.telegram.org/bot%s/sendMessage", token);
        String chatId = "-202324455";

        datas.put("chat_id", chatId);
        datas.put("text", content);
        datas.put("parse_mode", "Markdown");
        try {
            HttpClientManager client = HttpClientManager.getClient();
            if ("prod".equals(env)) {
                client.httpPost(api, datas);
            } else {
                client.httpPostSocket5Proxy(api, proxyPort, datas);
            }
            return true;
        } catch (Exception e) {
            logger.error("发送tg消息异常：{}", e);
        }
        return false;
    }


    /**
     * 发送tg验证码
     *
     * @param mark   标识
     * @param tgType 参考枚举 TgType
     * @return
     */
    public boolean sendTgMsg2(String mark, int tgType) {

        String key = prefix + ":" + mark;

        if (null != redis.getObject(key, Object.class)) {
            throw new PromptException("已发送过tg验证码，请60秒后再试");
        }

        //生成六位验证码
        String six = S.getSix();
        //放到缓存(有效期：1分钟)
        redis.setObject(key, six, 1 * 60);
        //发送内容
        String content = "";
        if (TgType.PART_REPAY.getKey() == tgType) {
            content = "【yj】" + mark + "部分还款验证码:" + six;
            WarnRecord warnRecord = new WarnRecord(mark, TgType.PART_REPAY.getKey(), content);
            warnRecordService.save(warnRecord);
        }

        //发送tg
        return sendTgMsg(content);

    }

    /**
     * 发送预警消息
     *
     * @param tgType 参考枚举 TgType
     * @param userId 用户id
     * @param billNo 交易流水号
     * @return
     */
    public boolean sendTgMsg3(int tgType, String userId, String billNo) {

        //发送内容
        String content = "";

        User user = userService.findById(userId);

        if (null == user) {
            return false;
        }

        UserInfo userInfo = userInfoService.findById(userId);

        if (null == userInfo) {
            return false;
        }

        if (TgType.OUT_MONEY.getKey() == tgType) {
            content = "【出款异常】姓名" + userInfo.getName() + ",手机尾号" + user.getMobile().substring(5) + ",交易尾号" + billNo.substring(billNo.length() - 8) + ",出款发生异常，请及时处理";
        }

        if (TgType.REPAY.getKey() == tgType) {
            content = "【还款异常】姓名" + userInfo.getName() + ",手机尾号" + user.getMobile().substring(5) + ",交易尾号" + billNo.substring(billNo.length() - 8) + ",还款发生异常，请及时处理";
        }

        if (TgType.APPLY_LOAN.getKey() == tgType) {
            content = "【有新的申请账单】当前有新的账单请及时处理。";
        }
        if (TgType.APPLY_LOAN_2.getKey() == tgType) {
            content = "【申请账单请处理】当前有" + loanService.findByWhere(" loan_status = 1 and is_delete = " + IsDelete.NO.getKey()).size() + "个未处理的申请账单，请及时处理";
        }

        WarnRecord warnRecord = new WarnRecord(billNo, tgType, content);
        warnRecordService.save(warnRecord);

        //发送tg
        return sendTgMsg(content);

    }


    /**
     * 校验tg验证码的真实性
     *
     * @param mark   标识
     * @param tgCode tg验证码
     * @return
     */
    public boolean verify(String mark, String tgCode) {

        String key = prefix + ":" + mark;

        String realCode = String.valueOf(redis.getObject(key, Object.class));

        if (S.isBlank(realCode)) {
            throw new PromptException(StatusCodeEnum.SMS_CODE_EXPIRED);

        }
        if (!realCode.equals(tgCode)) {
            throw new PromptException(StatusCodeEnum.SMS_CODE_VALIDATE_FAIL);
        }
        return true;
    }


}
