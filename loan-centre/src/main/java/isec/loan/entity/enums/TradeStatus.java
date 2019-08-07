package isec.loan.entity.enums;

/**
 * payFlow状态
 *
 * @author p
 * @date 2019-07-29
 */
public enum TradeStatus {
    // 1待付款 2 交易成功 3交易失败 4交易关闭 5处理中
    STATUS_WAITTING(1, "status_waitting"),TRADE_SUCCESS(2, "trade_success"),TRADE_FAILED(3, "trade_failed"),TRADE_CLOSED(4, "trade_closed"),TRADE_DEALING(5, "trade_dealing");

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

    TradeStatus(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

}
