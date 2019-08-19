package isec.loan.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import isec.base.util.S;
import isec.loan.core.AbstractService;
import isec.loan.entity.OperLogon;
import isec.loan.entity.User;
import isec.loan.entity.UserAppLog;
import isec.loan.mapper.UserAppLogMapper;
import isec.loan.mapper.UserMapper;


/**
 * Created by p on 2019/07/17.
 */
@Service
@Transactional
public class UserService extends AbstractService<User> {

    @Autowired
    UserMapper userMapper;
    @Autowired
    OperLogonService operLogonService;
    
    @Autowired
    UserAppLogMapper userAppLogMapper;

    /**
     * 用户登录
     *
     * @param user
     * @return
     */
    public Map<String, Object> login(User user) {
        Map<String, Object> data = new HashMap<>();

        // 构造登录信息并保存到缓存
        String token = S.getUuid();
        OperLogon operLogon = new OperLogon();
        operLogon.setToken(token);
        operLogon.setOperId(user.getUserId());
        operLogonService.saveOperLogon(operLogon);

        user.setUpdateTime(S.getCurrentTimestamp());
        userMapper.updateByPrimaryKeySelective(user);

        data.put("token", token);
        data.put("userId", user.getUserId());
        data.put("mobile", user.getMobile());

        return data;

    }
    
    public void saveUserAppLog(UserAppLog userAppLog) {
    	userAppLogMapper.insert(userAppLog);
    }
    
}
