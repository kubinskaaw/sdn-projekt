package pl.edu.agh.kt;

import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.OFPort;

public class Host {
	
	private IPv4Address ip;
	private OFPort port;
	private DatapathId datapathId;
	
	public Host() {
	}
	
	public Host(IPv4Address ip, OFPort port, DatapathId datapathId) {
		this.ip = ip;
		this.port = port;
		this.datapathId = datapathId;
	}
	
	public IPv4Address getSrcIp() {
		return this.ip;
	}
	
	public OFPort getPortIn() {
		return this.port;
	}
	
	public DatapathId getDatapathId() {
		return this.datapathId;
	}
	
	public void setSrcIp(IPv4Address ip) {
		this.ip = ip;
	}
	
	public void setPort(OFPort port) {
		this.port = port;
	}
	
	public void setDatapathId(DatapathId datapathId) {
		this.datapathId = datapathId;
	}
	
	
	@Override
	public String toString() {
		return "Host[srcIp=" + this.ip + 
				", portOut=" + this.port +
				", datapathId=" + this.datapathId +
				"]";
	}
}
