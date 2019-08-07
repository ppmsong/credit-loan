package isec.loan.entity.enums;

/**
 * 验证状态
 *
 * @author Administrator
 */
public enum VerifyStatus {
    // 0-未删除，1 成功  2 认证中  3 失败
    NO(0, "no"), YES(1, "yes"), CHECKING(2, "checking"), FAIL(3, "fail");

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

    VerifyStatus(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

}
