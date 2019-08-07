package isec.loan.common;

import java.util.HashMap;
import java.util.Map;

public class MapBox {

    private Map<String, Object> map = new HashMap<String, Object>();

    private MapBox() {
    }

    public static MapBox instance() {
        return new MapBox();
    }

    public MapBox put(String key, Object value) {
        map.put(key, value);
        return this;
    }

    public MapBox putAll(Map map) {
        this.map.putAll(map);
        return this;
    }

    public Map<String, Object> toMap() {
        return map;
    }

}
