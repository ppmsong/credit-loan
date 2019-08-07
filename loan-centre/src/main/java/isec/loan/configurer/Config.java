package isec.loan.configurer;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "config")
public class Config {

	private String comsunny;
	private String comsunnyBillApi;
	private String appId;// 支付应用ID
	private String appSign;// 支付验签秘钥
	private String masterSecret; // 支付回调秘钥
	private String return_url;
	private String nofify_url;
	private String SMSYUNPIANAPI = "https://sms.yunpian.com/v2/sms/single_send.json";
	private String SMSYUNPIANKEY = "14b463cb38c02f0ea9fc09700e915959";
	private String SMSYUNPIANTEMP = "【橙子卡包】您的验证码是";
	private String smsHead;

	// =======================虚拟卡常量======================
	private String CUSTOMERID;
	private String PRIVATEKEY;
	private String APIURL;
	private String CALLBACKURL;

	// RSA
	private String RSAenable;
	private String RSAPrivateKey;
	private String RSAPublicKey;

	// rabbitMQ
	private String address;
	private String username;
	private String password;
	private String normal;
	private String directExchange;
	private int maxCount;
	private String maxDelayNum;
	
	//wish
	private String wishUrl;
	
	//极验
	private String geetestId;
	private String geetestKey;
	
	//odin
	private String odinPushKey;
	private String odinPushSercet;
	private String odinPushUrl;

	//愿望接口应用
	private String wishAppId;
	private String wishAppSign;
	private String wishMasterSecret;
	
	//签名
	private String signKey;
	private String signOnOff;

	public String getOdinPushKey() {
		return odinPushKey;
	}

	public void setOdinPushKey(String odinPushKey) {
		this.odinPushKey = odinPushKey;
	}

	public String getOdinPushSercet() {
		return odinPushSercet;
	}

	public void setOdinPushSercet(String odinPushSercet) {
		this.odinPushSercet = odinPushSercet;
	}

	public String getOdinPushUrl() {
		return odinPushUrl;
	}

	public void setOdinPushUrl(String odinPushUrl) {
		this.odinPushUrl = odinPushUrl;
	}

	public String getWishAppId() {
		return wishAppId;
	}

	public void setWishAppId(String wishAppId) {
		this.wishAppId = wishAppId;
	}

	public String getWishAppSign() {
		return wishAppSign;
	}

	public void setWishAppSign(String wishAppSign) {
		this.wishAppSign = wishAppSign;
	}

	public String getWishMasterSecret() {
		return wishMasterSecret;
	}

	public void setWishMasterSecret(String wishMasterSecret) {
		this.wishMasterSecret = wishMasterSecret;
	}

	public String getComsunnyBillApi() {
		return comsunnyBillApi;
	}

	public void setComsunnyBillApi(String comsunnyBillApi) {
		this.comsunnyBillApi = comsunnyBillApi;
	}

	public String getComsunny() {
		return comsunny;
	}

	public void setComsunny(String comsunny) {
		this.comsunny = comsunny;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppSign() {
		return appSign;
	}

	public void setAppSign(String appSign) {
		this.appSign = appSign;
	}

	public String getReturn_url() {
		return return_url;
	}

	public void setReturn_url(String return_url) {
		this.return_url = return_url;
	}

	public String getNofify_url() {
		return nofify_url;
	}

	public void setNofify_url(String nofify_url) {
		this.nofify_url = nofify_url;
	}

	public String getSMSYUNPIANAPI() {
		return SMSYUNPIANAPI;
	}

	public void setSMSYUNPIANAPI(String sMSYUNPIANAPI) {
		SMSYUNPIANAPI = sMSYUNPIANAPI;
	}

	public String getSMSYUNPIANKEY() {
		return SMSYUNPIANKEY;
	}

	public void setSMSYUNPIANKEY(String sMSYUNPIANKEY) {
		SMSYUNPIANKEY = sMSYUNPIANKEY;
	}

	public String getSMSYUNPIANTEMP() {
		return SMSYUNPIANTEMP;
	}

	public void setSMSYUNPIANTEMP(String sMSYUNPIANTEMP) {
		SMSYUNPIANTEMP = sMSYUNPIANTEMP;
	}

	public String getCUSTOMERID() {
		return CUSTOMERID;
	}

	public void setCUSTOMERID(String cUSTOMERID) {
		CUSTOMERID = cUSTOMERID;
	}

	public String getPRIVATEKEY() {
		return PRIVATEKEY;
	}

	public void setPRIVATEKEY(String pRIVATEKEY) {
		PRIVATEKEY = pRIVATEKEY;
	}

	public String getAPIURL() {
		return APIURL;
	}

	public void setAPIURL(String aPIURL) {
		APIURL = aPIURL;
	}

	public String getCALLBACKURL() {
		return CALLBACKURL;
	}

	public void setCALLBACKURL(String cALLBACKURL) {
		CALLBACKURL = cALLBACKURL;
	}

	public String getRSAenable() {
		return RSAenable;
	}

	public void setRSAenable(String RSAenable) {
		this.RSAenable = RSAenable;
	}

	public String getRSAPrivateKey() {
		return RSAPrivateKey;
	}

	public void setRSAPrivateKey(String rSAPrivateKey) {
		RSAPrivateKey = rSAPrivateKey;
	}

	public String getRSAPublicKey() {
		return RSAPublicKey;
	}

	public void setRSAPublicKey(String rSAPublicKey) {
		RSAPublicKey = rSAPublicKey;
	}

	public String getNormal() {
		return normal;
	}

	public void setNormal(String normal) {
		this.normal = normal;
	}

	public String getDirectExchange() {
		return directExchange;
	}

	public void setDirectExchange(String directExchange) {
		this.directExchange = directExchange;
	}

	public int getMaxCount() {
		return maxCount;
	}

	public void setMaxCount(int maxCount) {
		this.maxCount = maxCount;
	}

	public String getMaxDelayNum() {
		return maxDelayNum;
	}

	public void setMaxDelayNum(String maxDelayNum) {
		this.maxDelayNum = maxDelayNum;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMasterSecret() {
		return masterSecret;
	}

	public void setMasterSecret(String masterSecret) {
		this.masterSecret = masterSecret;
	}

	public String getWishUrl() {
		return wishUrl;
	}

	public void setWishUrl(String wishUrl) {
		this.wishUrl = wishUrl;
	}

	public String getGeetestId() {
		return geetestId;
	}

	public void setGeetestId(String geetestId) {
		this.geetestId = geetestId;
	}

	public String getGeetestKey() {
		return geetestKey;
	}

	public void setGeetestKey(String geetestKey) {
		this.geetestKey = geetestKey;
	}

	public String getSmsHead() {
		return smsHead;
	}

	public void setSmsHead(String smsHead) {
		this.smsHead = smsHead;
	}

	public String getSignKey() {
		return signKey;
	}

	public void setSignKey(String signKey) {
		this.signKey = signKey;
	}

	public String getSignOnOff() {
		return signOnOff;
	}

	public void setSignOnOff(String signOnOff) {
		this.signOnOff = signOnOff;
	}

}
