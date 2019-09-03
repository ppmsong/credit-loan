package isec.loan.entity.enums;

/**
 * 奥丁大数据推送类型
 *
 * @author p
 * @date 2019-07-23
 */
public enum OdinPushType {
    //1.申请提交成功;2.申请成功（放款成功）;3.申请失败;4.即将逾期;5.已逾期;6.已还款（部分）;7.已还款；8.贷款审核通过（未放款）；9.订单关闭（未领取）
    APPLY_LOAN(1, "apply_loan"), LOAN_SUCCESS(2, "loan_success"), LOAN_FAIL(3, "loan_fail"), OVERDUING(4, "overduing"), OVERDUED(5, "overdued"), REPAYED_PART(6, "repayed_part"), REPAYED_ALL(7, "repayed_all"), LOAN_PASS(8, "loan_pass"), UN_RECEIVE_CLOSED(9, "un_receive_closed");

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

    OdinPushType(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

}
