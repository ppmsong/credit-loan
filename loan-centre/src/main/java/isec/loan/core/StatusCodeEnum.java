package isec.loan.core;

public enum StatusCodeEnum {


    // 系统系统相关
    SYSTEM_EXCEPTION("9999", "服务器内部错误"),
    PARAMS_ILLEGAL("1000", "参数非法"),
    API_INVALID("000001", "请求的接口不存在"),
    TOKEN_NOTEMPTY("000002", "TOKEN 参数不能为空"),
    TOKEN_INVALID("000003", "TOKEN 无效"),
    MEMBER_INVALID("000004", "当前用户不存在"),

    // 常用公共
    SMS_CODE_NOTEMPTY("1001", "请输入短信验证码"),
    SMS_CODE_EXPIRED("1002", "验证码已过期"),
    SMS_CODE_VALIDATE_FAIL("1003", "验证码错误"),
    PARAMS_NOTEMPTY("1004", "参数不能为空"),
    PARAMS_TYPE_ERROR("1005", "参数类型不正确"),
    APP_VERSION_NONE("1006", "版本不存在"),
    QUERY_MOBILE_ZONE_FAIL("1007", "归属地查询失败"),
    PARAM_VALIDATE_FAIL("1008", "参数验证失败"),
    ENUM_EMPTY("1009", "枚举值为空，请检查！"),
    SUCCESS("ok", "操作成功"),
    FAIL("fail", "操作失败"),
    NOT_FOUND("not_found","接口不存在"),
    INTERNAL_SERVER_ERROR("internal_server_error","服务器内部报错"),
    UNAUTHORIZED("unauthorized","签名错误"),

    // 用户账户相关
    USERNAME_NOTEMPTY("2001", "用户名不能为空"),
    MOBILE_NOTEMPTY("2002", "手机号不能为空"),
    MOBILE_VALIDATE_FAIL("2003", "手机号格式不正确"),

    PASSWD_NOTEMPTY("2004", "请输入密码"),
    NEW_PASSWD_NOTEMPTY("2005", "请输入新密码"),
    OLD_PASSWD_NOTEMPTY("2006", "您输入的旧密码错误"),
    PASSWD_VALIDATE_FAIL("2007", "密码需包含数字与字母组合"),
    PASSWD_NOTENOUGH_LENGTH("2008", "请输入6-16位密码"),
    ACCOUNT_NONEXISTENCE("2009", "该账号尚未注册"),
    ALIPAY_ACCOUNT_BOUND("2010", "该手机号已绑定过其他支付宝"),
    ALIPAY_ACCOUNT_NOTEMPTY("2011", "支付宝用户号不能为空"),
    ACCOUNT_FORZEN("2012", "抱歉！连续5次密码错误，该账号已锁定，重置密码可解除锁定"),
    MOBILE_OR_PASSWORD_ERROR("2013", "手机号或密码错误"),
    MOBILE_HAS_REGED("2014", "手机号已注册"),
    USER_INFO_ERROR("2015", "用户数据有误"),
    USER_INFO_VERIFIED("2016", "用户已实名认证，请不要重复认证"),
    USER_INFO_VERIFING("2017", "用户实名认证中，请耐心等待");



    private final String message;
    private final String code;

    StatusCodeEnum(String code, String message) {
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }


}