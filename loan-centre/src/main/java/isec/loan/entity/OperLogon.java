package isec.loan.entity;

import java.io.Serializable;

public class OperLogon implements Serializable {

	private static final long serialVersionUID = -7105461584791967309L;

	private String token;
	private String operId;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getOperId() {
		return operId;
	}

	public void setOperId(String operId) {
		this.operId = operId;
	}

}
