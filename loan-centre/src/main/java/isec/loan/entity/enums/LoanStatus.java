package isec.loan.entity.enums;

/**
 * 贷款申请状态
 *
 * @author p
 * @date 2019-07-23
 */
public enum LoanStatus {
    //还款状态 1 待审核 2 待放款  3 还款中  4 展期  5已关闭 6申请失败 7-领取过期关闭
    CHECKING(1, "checking"), LOANING(2, "loaning"), REPAYING(3, "repaying"), OVERDUW(4,
            "overdue"), FINISHED(5, "finished"), FAILED(6, "failed"), CLOSED(7, "closed");

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

    LoanStatus(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

}
