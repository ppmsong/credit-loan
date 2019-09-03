package isec.loan.entity.enums;

/**
 * tg验证码状态
 *
 * @author p
 * @date 2019-08-26
 */
public enum TgType {
    //1-部分还款验证码  2-出款预警  3-支付预警 4-申请借款预警  5-请订单累计达到10的倍数时预警
    PART_REPAY(1, "part_repay"), OUT_MONEY(2, "out_money"), REPAY(3, "repay"),APPLY_LOAN(4, "apply_loan"),APPLY_LOAN_2(5, "apply_loan_2");

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

    TgType(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

}
