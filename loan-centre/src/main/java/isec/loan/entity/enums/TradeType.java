package isec.loan.entity.enums;

/**
 * 支付类型
 *
 * @author p
 * @date 2019-04-03
 */
public enum TradeType {
    // 交易类型 1 借款  2 还款
    TYPE_LOAN(1, "type_loan"), TYPE_REPAYMENT(2, "type_repayment");

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

    TradeType(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

}
