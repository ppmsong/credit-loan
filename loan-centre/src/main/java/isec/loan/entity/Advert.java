package isec.loan.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

public class Advert {

	@Id
	@GeneratedValue(generator = "UUID")
	private String advertId;
	private String name;
	private int clientType;
	private int type;
	private int advPosition;
	private String info;
	private String imgUrl;
	private String remark;
	private int sort;
	private long addTime;
	private long updateTime;
	private int displayRate;
	private int status;

	public String getAdvertId() {
		return advertId;
	}

	public void setAdvertId(String advertId) {
		this.advertId = advertId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getClientType() {
		return clientType;
	}

	public void setClientType(int clientType) {
		this.clientType = clientType;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getAdvPosition() {
		return advPosition;
	}

	public void setAdvPosition(int advPosition) {
		this.advPosition = advPosition;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public long getAddTime() {
		return addTime;
	}

	public void setAddTime(long addTime) {
		this.addTime = addTime;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

	public int getDisplayRate() {
		return displayRate;
	}

	public void setDisplayRate(int displayRate) {
		this.displayRate = displayRate;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
