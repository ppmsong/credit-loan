package isec.loan.entity;


import isec.base.util.S;
import isec.loan.entity.enums.IsDelete;

import javax.persistence.Id;

public class UserInfo {

    @Id
    private String userId;
    private String name;
    private String idcard;
    private String alipayAccount;
    private String alipayNickName;
    private int zhimaScore;
    private String bankName;
    private String bankCardno;
    private String bankMobile;
    private String contacter;
    private int idcardVerify;
    private int zhimaVerify;
    private int contactVerify;
    private int bankVerify;
    private int operatorVerify;
    private int alipayVerify;
    private long createTime;
    private long updateTime;
    private int isDelete;


    public UserInfo() {
        super();
    }

    public UserInfo(String userId) {
        this.userId = userId;
        this.name = "";
        this.idcard = "";
        this.alipayAccount = "";
        this.alipayNickName = "";
        this.zhimaScore = 0;
        this.bankName = "";
        this.bankCardno = "";
        this.bankMobile = "";
        this.contacter = "";
        this.idcardVerify = 0;
        this.zhimaVerify = 0;
        this.contactVerify = 0;
        this.bankVerify = 0;
        this.operatorVerify = 0;
        this.alipayVerify = 0;
        this.createTime = S.getCurrentTimestamp();
        this.updateTime = 0;
        this.isDelete = IsDelete.NO.getKey();
    }

    public String getAlipayNickName() {
        return alipayNickName;
    }

    public void setAlipayNickName(String alipayNickName) {
        this.alipayNickName = alipayNickName;
    }

    public int getAlipayVerify() {
        return alipayVerify;
    }

    public void setAlipayVerify(int alipayVerify) {
        this.alipayVerify = alipayVerify;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getAlipayAccount() {
        return alipayAccount;
    }

    public void setAlipayAccount(String alipayAccount) {
        this.alipayAccount = alipayAccount;
    }

    public int getZhimaScore() {
        return zhimaScore;
    }

    public void setZhimaScore(int zhimaScore) {
        this.zhimaScore = zhimaScore;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankCardno() {
        return bankCardno;
    }

    public void setBankCardno(String bankCardno) {
        this.bankCardno = bankCardno;
    }

    public String getBankMobile() {
        return bankMobile;
    }

    public void setBankMobile(String bankMobile) {
        this.bankMobile = bankMobile;
    }

    public String getContacter() {
        return contacter;
    }

    public void setContacter(String contacter) {
        this.contacter = contacter;
    }

    public int getIdcardVerify() {
        return idcardVerify;
    }

    public void setIdcardVerify(int idcardVerify) {
        this.idcardVerify = idcardVerify;
    }

    public int getZhimaVerify() {
        return zhimaVerify;
    }

    public void setZhimaVerify(int zhimaVerify) {
        this.zhimaVerify = zhimaVerify;
    }

    public int getContactVerify() {
        return contactVerify;
    }

    public void setContactVerify(int contactVerify) {
        this.contactVerify = contactVerify;
    }

    public int getBankVerify() {
        return bankVerify;
    }

    public void setBankVerify(int bankVerify) {
        this.bankVerify = bankVerify;
    }

    public int getOperatorVerify() {
        return operatorVerify;
    }

    public void setOperatorVerify(int operatorVerify) {
        this.operatorVerify = operatorVerify;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }
}