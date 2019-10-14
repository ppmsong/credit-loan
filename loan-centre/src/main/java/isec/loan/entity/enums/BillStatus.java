package isec.loan.entity.enums;

/**
 * 账单状态
 *
 * @author p
 * @date 2019-07-23
 */
public enum BillStatus {
    //还款状态 0 未还款 1 已还款   4 逾期 5 分期还款
    UNREPAY(0, "unrepay"), REPAY(1, "repay"),  OVERDUED(4, "overdued"),STAGE(5, "stage");

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

    BillStatus(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

}
