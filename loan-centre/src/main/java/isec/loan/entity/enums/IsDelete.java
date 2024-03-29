package isec.loan.entity.enums;

/**
 * 是否删除状态
 */
public enum IsDelete {
    // 1-未删除，2-已删除
    NO(1, "no"), YES(2, "yes");

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

    IsDelete(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

}
