package isec.loan.entity;

import javax.persistence.Id;

import isec.base.util.S;
import isec.loan.entity.enums.IsDelete;

public class User {

    //    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private String userId;
    //    @Column(name = "user_name")
    private String password;
    private String mobile;
    private String salt;
    private int status;
    private long createTime;
    private long updateTime;
    private long isDelete;
//    @Transient
//    private String token;


    public User() {
        super();
    }

    public User(String mobile) {
        this.userId = S.getUuid();
        this.mobile = mobile;
        this.password = "";
        this.salt = S.getChar4();
        this.status = 1;
        this.createTime = S.getCurrentTimestamp();
        this.updateTime = 0;
        this.isDelete = IsDelete.NO.getKey();
    }



    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
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

    public long getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(long isDelete) {
        this.isDelete = isDelete;
    }

/*    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }*/

}