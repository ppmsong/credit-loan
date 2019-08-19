package isec.loan.entity;

import isec.base.util.S;
import isec.loan.entity.enums.IsDelete;

import javax.persistence.Id;

public class Bill {

    @Id
    private String billId;
    private String userId;
    private String loanId;
    private int repaymentAmount;
    private int rBasic;
    private int rInterest;
    private long deadline;
    private long repayTime;
    private int status;
    private int overdueFine;
    private int noticTimes;
    private long createTime;
    private long updateTime;
    private int isDelete;

    public Bill() {
    }

    public Bill(String userId, String loanId, int repaymentAmount, int rBasic, int rInterest, long deadline) {
        this.billId = "B"+S.createReqNo();
        this.userId = userId;
        this.loanId = loanId;
        this.repaymentAmount = repaymentAmount;
        this.rBasic = rBasic;
        this.rInterest = rInterest;
        this.deadline = deadline;
        this.repayTime = 0;
        this.status = 0;
        this.overdueFine = 0;
        this.createTime = S.getCurrentTimestamp();
        this.updateTime = 0;
        this.isDelete = IsDelete.NO.getKey();
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLoanId() {
        return loanId;
    }

    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }

    public int getRepaymentAmount() {
        return repaymentAmount;
    }

    public void setRepaymentAmount(int repaymentAmount) {
        this.repaymentAmount = repaymentAmount;
    }

    public int getrBasic() {
        return rBasic;
    }

    public void setrBasic(int rBasic) {
        this.rBasic = rBasic;
    }

    public int getrInterest() {
        return rInterest;
    }

    public void setrInterest(int rInterest) {
        this.rInterest = rInterest;
    }

    public long getDeadline() {
        return deadline;
    }

    public void setDeadline(long deadline) {
        this.deadline = deadline;
    }

    public long getRepayTime() {
        return repayTime;
    }

    public void setRepayTime(long repayTime) {
        this.repayTime = repayTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getOverdueFine() {
        return overdueFine;
    }

    public void setOverdueFine(int overdueFine) {
        this.overdueFine = overdueFine;
    }

    public int getNoticTimes() {
        return noticTimes;
    }

    public void setNoticTimes(int noticTimes) {
        this.noticTimes = noticTimes;
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