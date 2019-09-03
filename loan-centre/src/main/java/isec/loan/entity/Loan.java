package isec.loan.entity;

import isec.base.util.S;
import isec.loan.entity.enums.IsDelete;
import isec.loan.entity.enums.LoanStatus;

import javax.persistence.Id;
import java.math.BigDecimal;

public class Loan {

    @Id
    private String loanId;
    private String userId;
    private String productId;
    private int borrowMoney;
    private BigDecimal rete;
    private int riskCost;
    private int days;
    private BigDecimal overdueRate;
    private int postponeEnable;
    private int postponeFee;
    private int postponeManageFee;
    private int loanStatus;
    private long createTime;
    private long receiveLoanTime;
    private long updateTime;
    private long resetUserVerifyTime;
    private int isDelete;

    public Loan() {
    }

    public Loan(String userId, String productId, int borrowMoney, BigDecimal rete, int riskCost, int days, BigDecimal overdueRate) {
        this.loanId = "L"+S.createReqNo();
        this.userId = userId;
        this.productId = productId;
        this.borrowMoney = borrowMoney;
        this.rete = rete;
        this.riskCost = riskCost;
        this.days = days;
        this.overdueRate = overdueRate;
        this.postponeEnable = 0;
        this.postponeFee = 0;
        this.postponeManageFee = 0;
        this.loanStatus = LoanStatus.CHECKING.getKey();
        this.createTime = S.getCurrentTimestamp();
        this.receiveLoanTime = 0;
        this.updateTime = 0;
        this.isDelete = IsDelete.NO.getKey();
    }

    public long getReceiveLoanTime() {
        return receiveLoanTime;
    }

    public void setReceiveLoanTime(long receiveLoanTime) {
        this.receiveLoanTime = receiveLoanTime;
    }

    public String getLoanId() {
        return loanId;
    }

    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getBorrowMoney() {
        return borrowMoney;
    }

    public void setBorrowMoney(int borrowMoney) {
        this.borrowMoney = borrowMoney;
    }

    public BigDecimal getRete() {
        return rete;
    }

    public void setRete(BigDecimal rete) {
        this.rete = rete;
    }

    public int getRiskCost() {
        return riskCost;
    }

    public void setRiskCost(int riskCost) {
        this.riskCost = riskCost;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public BigDecimal getOverdueRate() {
        return overdueRate;
    }

    public void setOverdueRate(BigDecimal overdueRate) {
        this.overdueRate = overdueRate;
    }

    public int getPostponeEnable() {
        return postponeEnable;
    }

    public void setPostponeEnable(int postponeEnable) {
        this.postponeEnable = postponeEnable;
    }

    public int getPostponeFee() {
        return postponeFee;
    }

    public void setPostponeFee(int postponeFee) {
        this.postponeFee = postponeFee;
    }

    public int getPostponeManageFee() {
        return postponeManageFee;
    }

    public void setPostponeManageFee(int postponeManageFee) {
        this.postponeManageFee = postponeManageFee;
    }

    public int getLoanStatus() {
        return loanStatus;
    }

    public void setLoanStatus(int loanStatus) {
        this.loanStatus = loanStatus;
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

	public long getResetUserVerifyTime() {
		return resetUserVerifyTime;
	}

	public void setResetUserVerifyTime(long resetUserVerifyTime) {
		this.resetUserVerifyTime = resetUserVerifyTime;
	}

    
}