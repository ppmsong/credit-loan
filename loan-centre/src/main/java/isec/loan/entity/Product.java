package isec.loan.entity;

import javax.persistence.Id;
import java.math.BigDecimal;

public class Product {

    @Id
    private String productId;
    private String name;
    private int borrowMoney;
    private BigDecimal rete;
    private int riskCost;
    private int days;
    private BigDecimal overdueRate;
    private int postponeEnable;
    private int postponeFee;
    private int postponeManageFee;
    private Long createTime;
    private Long updateTime;
    private int isDelete;

    public Product() {
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }
}