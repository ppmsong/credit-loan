package isec.loan.entity;

import javax.persistence.Id;

public class Settings {

    @Id
    private String setId;
    private String setKey;
    private String setVal;
    private String name;
    private int type;
    private int prop;
    private String remark;

    public Settings() {
    }

    public String getSetId() {
        return setId;
    }

    public void setSetId(String setId) {
        this.setId = setId;
    }

    public String getSetKey() {
        return setKey;
    }

    public void setSetKey(String setKey) {
        this.setKey = setKey;
    }

    public String getSetVal() {
        return setVal;
    }

    public void setSetVal(String setVal) {
        this.setVal = setVal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getProp() {
        return prop;
    }

    public void setProp(int prop) {
        this.prop = prop;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}