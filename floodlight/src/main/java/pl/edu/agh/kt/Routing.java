package pl.edu.agh.kt;

import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.internal.IOFSwitchService;
import net.floodlightcontroller.routing.IRoutingService;
import net.floodlightcontroller.routing.Route;
import net.floodlightcontroller.topology.NodePortTuple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.OFPort;
import org.projectfloodlight.openflow.types.U64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Routing {

	protected static final Logger logger = LoggerFactory.getLogger(Routing.class);
	
	private IRoutingService routingService;
	private IOFSwitchService switchService;
	private List<Host> hostList = new ArrayList<>();
	
	public Routing(IRoutingService routingService, IOFSwitchService switchService) {
		super();
		this.routingService = routingService;
		this.switchService = switchService;
	}
	
	public void addHosts(List<Host> hostList) {
		this.hostList.addAll(hostList);
	}

	public void calculatePathsToHosts() {
		for (Host srcHost : this.hostList) {
			for (Host dstHost : this.hostList) {
				if (!srcHost.equals(dstHost)) {
					Route route = this.calculateSpf(srcHost.getDatapathId(), srcHost.getPortIn(), dstHost.getDatapathId(), dstHost.getPortIn());
					List<NodePortTuple> nodePortTupleList = route.getPath();

					for (int i = 0; i < nodePortTupleList.size(); i += 2) {
						NodePortTuple fromNodePortTuple = nodePortTupleList.get(i);
						NodePortTuple toNodePortTuple = nodePortTupleList.get(i + 1);
						IOFSwitch sw = this.switchService.getSwitch(fromNodePortTuple.getNodeId());
						
						Flows.simpleEthAdd(sw, fromNodePortTuple.getPortId(), toNodePortTuple.getPortId(), srcHost.getSrcIp(), dstHost.getSrcIp());
						Flows.simpleArpAdd(sw, toNodePortTuple.getPortId(), dstHost.getSrcIp());
					}
				}
			}
		}
	}
	
	public IOFSwitch getSwitch(DatapathId datapathId) {
		return this.switchService.getSwitch(datapathId);
	}
	
	private Route calculateSpf(DatapathId src, OFPort srcPort, DatapathId dst, OFPort dstPort) {
		return this.routingService.getRoute(src, srcPort, dst, dstPort, U64.of(0));
	}
}
