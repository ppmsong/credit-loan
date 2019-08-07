package isec.loan.entity.enums;

/**
 * 贷款状态
 *
 * @author p
 * @date 2019-07-23
 */
public enum LoanStatus {
    //1 待审核 2 带放款  3 还款中  4 展期  5已关闭 6申请失败
    CHECKING(1, "checking"), LOANING(2, "loaning"), REPAYING(3, "repaying"), OVERDUW(4,
            "overdue"), CLOSED(5, "colse"), FAILED(6, "failed");

    public static String prefix = "verifyCode";
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
