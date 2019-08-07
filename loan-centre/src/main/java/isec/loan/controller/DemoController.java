package isec.loan.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import isec.base.util.S;
import isec.loan.entity.User;
import isec.loan.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Condition;

import java.util.List;

/**
 * Created by p on 2019/07/17.
 */
@RestController
@RequestMapping("/demo")
public class DemoController {
    @Autowired
    UserService userService;

    @RequestMapping("/add")
    public void add() {
        User user = new User();
        user.setUserId(S.getSix());
        user.setMobile("1329998");
        user.setPassword("123456");
        user.setSalt("peng");
        userService.save(user);
    }

    @PostMapping("/delete")
    public void delete(@RequestParam String id) {
        userService.deleteById(id);
    }

    @RequestMapping("/update")
    public void update(User user) {
        userService.update(user);
    }

    @RequestMapping("/detail")
    public User detail(@RequestParam String id) {
        User user = userService.findById(id);
        return user;
    }

    @RequestMapping("/fieldName")
    public User fieldName() {
        User user = userService.findBy("mobile", "18621172474");
        return user;
    }

    @RequestMapping("/findByCondition")
    public List<User> findByCondition() {
        Condition condition = new Condition(User.class);
        condition.createCriteria().andCondition("mobile=18621172474");
        return userService.getByCondition(condition);
    }

    @RequestMapping("/list")
    public PageInfo list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        PageHelper.orderBy("create_time");
        List<User> list = userService.findAll();
        PageInfo pageInfo = new PageInfo(list);
        return pageInfo;
    }
}
