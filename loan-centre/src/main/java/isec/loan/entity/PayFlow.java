package isec.loan.entity;

import isec.base.util.S;
import isec.loan.entity.enums.TradeStatus;

import javax.persistence.Id;

/**
 * 出款记录
 *
 * @author Administrator
 */
public class PayFlow {

    @Id
    private String payId;
    private String tradeNo;
    private String outTradeNo;
    private int tradeType;
    private String userId;
    private String mobile;
    private String title;
    private int totalAmount;
    private Integer payType;
    private String requestParam;
    private String callback;
    private String notify;
    private int status;
    private long createTime;
    private long updateTime;


    public PayFlow() {

    }

    public PayFlow(String tradeNo, int tradeType, String userId, String mobile, String title, int totalAmount, Integer payType, String requestParam) {
        this.payId = S.createReqNo();
        this.tradeNo = tradeNo;
        this.tradeType = tradeType;
        this.userId = userId;
        this.mobile = mobile;
        this.title = title;
        this.totalAmount = totalAmount;
        this.payType = payType;
        this.requestParam = requestParam;
        this.callback="";
        this.notify="";
        this.status = TradeStatus.STATUS_WAITTING.getKey();
        this.createTime = S.getCurrentTimestamp();
        this.updateTime = 0;
    }

    public void setTradeType(int tradeType) {
        this.tradeType = tradeType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPayId() {
        return payId;
    }

    public void setPayId(String payId) {
        this.payId = payId;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRequestParam() {
        return requestParam;
    }

    public void setRequestParam(String requestParam) {
        this.requestParam = requestParam;
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    public String getNotify() {
        return notify;
    }

    public void setNotify(String notify) {
        this.notify = notify;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Integer getTradeType() {
        return tradeType;
    }

    public void setTradeType(Integer tradeType) {
        this.tradeType = tradeType;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }


}
