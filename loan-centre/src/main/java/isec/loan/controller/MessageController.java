package isec.loan.controller;

import isec.base.bean.MapBox;
import isec.loan.common.In;
import isec.loan.entity.Message;
import isec.loan.entity.User;
import isec.loan.service.MessageService;
import isec.loan.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Condition;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author p
 * @date 2019/07/17
 */
@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    UserService userService;

    @Autowired
    MessageService messageService;

    /**
     * 查询消息列表
     *
     * @return
     */
    @RequestMapping("/queryMessageList")
    public Map<String, Object> queryMessageList(@In User user, @RequestParam(defaultValue = "1") Integer page,
                                                @RequestParam(defaultValue = "10") Integer pageSize) {

        List<Map<String, String>> messageList = messageService.getMessageList(user.getUserId(), page, pageSize);
        return MapBox.instance().put("messageList", messageList).toMap();
    }

    /**
     * 查询消息详情
     *
     * @param messageId
     * @return
     */
    @RequestMapping("/queryMessageDetails")
    public Map<String, Object> queryMessageDetails(@In User user, @NotBlank(message = "messageId不能为空") String messageId) {
        Map<String, Object> data = new HashMap<>();

        Condition condition = new Condition(Message.class);
        condition.createCriteria().andCondition("msg_id = '" + messageId + "'");

        Message message = messageService.findById(messageId);

        data.put("title", message.getTitle());
        data.put("content", message.getContent());
        data.put("status", message.getStatus());
        data.put("createTime", message.getCreateTime());

        message.setStatus(1);
        // 修改为已读状态
        messageService.update(message);

        return data;

    }

    /**
     * 获取消息条数
     *
     * @param user
     * @return
     */
    @RequestMapping("/getMessageCount")
    public Map<String, Object> getMessageCount(@In User user) {
        int count = messageService.getMessageCount(user.getUserId());
        return MapBox.instance().put("messageCount", count).toMap();
    }



}
