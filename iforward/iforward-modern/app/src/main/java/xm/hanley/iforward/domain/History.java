package cn.xm.hanley.iforward.domain;

public class History {

	private String hname;
	
	private String hnumber;
	
	private String hdate;
	
	private String htime;
	
	//呼叫被转到的号码
	private String htransfer;
	
	
	public String getHtransfer() {
		return htransfer;
	}

	public void setHtransfer(String htransfer) {
		this.htransfer = htransfer;
	}

	public String getHdate() {
		return hdate;
	}

	public void setHdate(String hdate) {
		this.hdate = hdate;
	}

	public String getHtime() {
		return htime;
	}

	public void setHtime(String htime) {
		this.htime = htime;
	}

	public String getHname() {
		return hname;
	}

	public void setHname(String hname) {
		this.hname = hname;
	}

	public String getHnumber() {
		return hnumber;
	}

	public void setHnumber(String hnumber) {
		this.hnumber = hnumber;
	}

	
	
}
