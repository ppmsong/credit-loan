package isec.loan.entity;


import javax.persistence.Id;

/**
 * APP版本
 * 
 * @author p
 * @date 2018-05-02 11：51
 *
 */
public class AppVersion {

	/**
	 * 
	 */
	@Id
	private String objectId;
	private int type;
	private int imposed;
	private int showbox;
	private String version;
	private String description;
	private long addTime;
	private int sort;

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getImposed() {
		return imposed;
	}

	public void setImposed(int imposed) {
		this.imposed = imposed;
	}

	public int getShowbox() {
		return showbox;
	}

	public void setShowbox(int showbox) {
		this.showbox = showbox;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getAddTime() {
		return addTime;
	}

	public void setAddTime(long addTime) {
		this.addTime = addTime;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}
}
