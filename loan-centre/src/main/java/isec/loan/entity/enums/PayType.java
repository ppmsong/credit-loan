package isec.loan.entity.enums;

/**
 * 卡状态
 *
 * @author p
 * @date 2019-04-03
 */
public enum PayType {
    // 0收营台 1支付宝 2 微信 3 银联 4 余额
	STATION(0, "station"), ALI(1, "ali"), WX(2, "wx"), UNION(3, "union"), BALANCE(4, "balance"), NO_STATION(5, "no_station");

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

    PayType(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

}
