package isec.loan.service;

import com.github.pagehelper.PageHelper;
import isec.loan.core.AbstractService;
import isec.loan.entity.Message;
import isec.loan.entity.enums.IsDelete;
import isec.loan.mapper.MessageMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 站内信信息
 *
 * @author Administrator
 */
@Service
@Transactional
public class MessageService extends AbstractService<Message> {


    @Resource
    MessageMapper messageMapper;


    /**
     * 获取用户消息列表
     */
    public List<Map<String, String>> getMessageList(String memberId, int page, int pageSize) {

        PageHelper.startPage(page, pageSize);
        PageHelper.orderBy("create_time desc");

        Condition condition = new Condition(Message.class);
        condition.createCriteria().andCondition("user_id = '" + memberId + "'");

        ArrayList<Map<String, String>> data = new ArrayList<>();
        List<Message> messageList = this.getByCondition(condition);
        for (Message message : messageList) {
            Map<String, String> messageData = new HashMap<>();
            messageData.put("msgId", message.getMsgId());
            messageData.put("title", message.getTitle());
            messageData.put("content", message.getContent());
            messageData.put("status", String.valueOf(message.getStatus()));
            messageData.put("createTime", String.valueOf(message.getCreateTime()));

            data.add(messageData);
        }

        return data;
    }


    /**
     * 发送站内信
     *
     * @param memberId
     * @param title
     * @param content
     * @return
     */
    public Boolean sendMessage(String memberId, String title, String content) {
        Message message = new Message(memberId, title, content);
        int a = this.save(message);
        if (a > 0) {
            return true;
        }
        return false;
    }


    /**
     * 获取用户未读信息
     *
     * @param userId
     * @return
     */
    public int getMessageCount(String userId) {
        Condition condition = new Condition(Message.class);
        condition.createCriteria().andCondition("user_id = '" + userId + "' and is_delete = "+IsDelete.NO.getKey() +" and status = 0 ");
        return mapper.selectCountByCondition(condition);
    }
}
