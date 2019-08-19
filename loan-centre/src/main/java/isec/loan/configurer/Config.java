package isec.loan.configurer;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "config")
public class Config {

    private String comsunny;
    private String comsunnyBillApi;
    private String appId;
    private String appSign;
    private String masterSecret;
    private String return_url;
    private String nofify_url;
    private String SMSYUNPIANAPI;
    private String SMSYUNPIANKEY;
    private String SMSYUNPIANTEMP;
    private String smsHead;


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


    //极验
    private String geetestId;
    private String geetestKey;

    //odin
    private String odinPushKey;
    private String odinPushSercet;
    private String odinPushUrl;

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
