package cn.data.entity;

public class AreaResponse {
	private AreaNode data;
	private String message;
	private Integer status;
	private Integer total;
	private String tag;

	public AreaNode getData() {
		return data;
	}

	public void setData(AreaNode data) {
		this.data = data;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
}
