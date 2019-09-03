package isec.loan.entity;


import isec.base.util.S;

import javax.persistence.Id;

public class WarnRecord {

    @Id
    private String warnId;
    private String relationId;
    private int warnType;
    private String remark;
    private int status;
    private long warnTime;
    private long dealTime;
    private String dealUser;

    public WarnRecord() {

    }

    public WarnRecord(String relationId, int warnType, String remark) {
        this.warnId = "S"+ S.createReqNo();
        this.relationId = relationId;
        this.warnType = warnType;
        this.remark = remark;
        this.status = 0;
        this.warnTime = S.getCurrentTimestamp();
    }

    public String getWarnId() {
        return warnId;
    }

    public void setWarnId(String warnId) {
        this.warnId = warnId;
    }

    public String getRelationId() {
        return relationId;
    }

    public void setRelationId(String relationId) {
        this.relationId = relationId;
    }

    public int getWarnType() {
        return warnType;
    }

    public void setWarnType(int warnType) {
        this.warnType = warnType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getWarnTime() {
        return warnTime;
    }

    public void setWarnTime(long warnTime) {
        this.warnTime = warnTime;
    }

    public long getDealTime() {
        return dealTime;
    }

    public void setDealTime(long dealTime) {
        this.dealTime = dealTime;
    }

    public String getDealUser() {
        return dealUser;
    }

    public void setDealUser(String dealUser) {
        this.dealUser = dealUser;
    }


}