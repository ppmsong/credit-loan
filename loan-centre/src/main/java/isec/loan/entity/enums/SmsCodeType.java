package isec.loan.entity.enums;

/**
 * 短信验证码类型
 * 
 * @author p
 * @date 2019-04-03
 */
public enum SmsCodeType {
	// 1-注册，2-重置密码,3-提现,4-更换手机号,5-登录，6-支付 7-绑定支付宝
	TYPE_REG(1, "reg"), TYPE_RESET_PWD(2, "reset"), TYPE_WITHDRAW(3, "withdraw"), TYPE_CHANGE_MOBILE(4,
			"changeMobile"), TYPE_LOGIN(5, "login"), TYPE_PAY(6, "pay"), TYPE_BIND_ALIPAY(7, "bind_alipay");

	public static String prefix = "verifyCode";
	private int key;
	private String value;

	public String getStrkey() {
		return String.valueOf(key);
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	SmsCodeType(Integer key, String value) {
		this.key = key;
		this.value = value;
	}

}
