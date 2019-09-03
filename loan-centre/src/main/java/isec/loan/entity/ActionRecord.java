package isec.loan.entity;

import isec.base.util.S;
import isec.loan.entity.enums.IsDelete;

import javax.persistence.Id;

public class ActionRecord {

    @Id
    private String actionId;
    private String tradeId;
    private int tradeType;
    private String userId;
    private int actionType;
    private String info;
    private int tradeStatus;
    private int isDelete;
    private long createTime;
    private long updateTime;


    public ActionRecord() {
    }

    public ActionRecord(String tradeId, int tradeType, String userId, int actionType, String info, int tradeStatus) {
        this.actionId = S.getUuid();
        this.tradeId = tradeId;
        this.tradeType = tradeType;
        this.userId = userId;
        this.actionType = actionType;
        this.info = info;
        this.tradeStatus = tradeStatus;
        this.isDelete = IsDelete.NO.getKey();
        this.createTime = S.getCurrentTimestamp();
        this.updateTime = 0;
    }

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public int getTradeType() {
        return tradeType;
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

    public int getActionType() {
        return actionType;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(int tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
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
}