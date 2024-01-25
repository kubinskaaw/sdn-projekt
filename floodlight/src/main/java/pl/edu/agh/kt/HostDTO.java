package pl.edu.agh.kt;

public class HostDTO {
	
	private String ip;
	private Integer port;
	private String datapathId;
	
	public HostDTO() {
		super();
	}
	
	public String getIp() {
		return this.ip;
	}
	
	public Integer getPort() {
		return this.port;
	}
	
	public String getDatapathId() {
		return this.datapathId;
	}
}
