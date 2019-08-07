package isec.loan.service;

import isec.loan.common.redis.Redis;
import isec.loan.entity.OperLogon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OperLogonService {

    @Autowired
    private Redis<OperLogon> redis;

    private final int seconds = -1;

    public void saveOperLogon(OperLogon operLogon) {
        String key = "operLogon:" + operLogon.getOperId();
        redis.batchDel(key + "*");
        redis.setObject(key + ":" + operLogon.getToken(), operLogon, seconds);
        // 在线
        redis.setObject("online:" + operLogon.getOperId(), operLogon, 15 * 60);
    }

    public OperLogon getOperLogon(String token) {
        OperLogon operLogon = null;
        String[] keys = redis.keys("*" + token);
        if (null != keys && keys.length > 0) {
            operLogon = redis.getObject(keys[0], OperLogon.class);
        }
        return operLogon;
    }

    public void deleteOperLogon(String token) {
        String[] keys = redis.keys("*" + token);
        if (null != keys && keys.length > 0) {
            OperLogon operLogon = redis.getObject(keys[0], OperLogon.class);
            if (null != operLogon) {
                redis.del("online:" + operLogon.getOperId());
            }
            redis.del(keys[0]);
        }
    }

    public void updateOperLogon(String token) {
        String[] keys = redis.keys("*" + token);
        if (null != keys && keys.length > 0) {
            OperLogon operLogon = redis.getObject(keys[0], OperLogon.class);
            if (null != operLogon) {
                redis.setObject(keys[0], operLogon, seconds);
                //在线
                redis.setObject("online:" + operLogon.getOperId(), operLogon, 15 * 60);
            }
        }
    }

}
