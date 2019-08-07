package isec.loan.service;

import isec.base.util.S;
import isec.loan.core.AbstractService;
import isec.loan.entity.OperLogon;
import isec.loan.entity.User;
import isec.loan.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;


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
}
