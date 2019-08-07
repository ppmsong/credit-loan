package isec.loan.entity;

import isec.base.util.S;
import isec.loan.entity.enums.IsDelete;
import isec.loan.entity.enums.MessageStatus;

import javax.persistence.Id;

public class Message {

    @Id
    private String msgId;
    private String userId;
    private String title;
    private String content;
    private int status;
    private long createTime;
    private long updateTime;
    private int isDelete;

    public Message() {
    }


    public Message(String userId, String title, String content) {
        this.msgId = S.getUuid();
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.status = MessageStatus.UNREAD.getKey();
        this.createTime = S.getCurrentTimestamp();
        this.updateTime = 0;
        this.isDelete = IsDelete.NO.getKey();
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }
}