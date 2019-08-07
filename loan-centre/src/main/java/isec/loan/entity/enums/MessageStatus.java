package isec.loan.entity.enums;

/**
 * 站内信状态
 */
public enum MessageStatus {
    // 0-未读，1-已读
    UNREAD(0, "unread"), READ(1, "read");

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

    MessageStatus(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

}
