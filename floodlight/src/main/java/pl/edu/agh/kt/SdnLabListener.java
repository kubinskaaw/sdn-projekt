package pl.edu.agh.kt;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFPacketIn;
import org.projectfloodlight.openflow.protocol.OFType;
import org.projectfloodlight.openflow.types.OFPort;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.internal.IOFSwitchService;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;

import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.restserver.IRestApiService;
import net.floodlightcontroller.routing.IRoutingService;
import net.floodlightcontroller.statistics.IStatisticsService;
import net.floodlightcontroller.statistics.StatisticsCollector;
import net.floodlightcontroller.statistics.SwitchPortBandwidth;
import net.floodlightcontroller.topology.ITopologyService;
import net.floodlightcontroller.topology.NodePortTuple;

import pl.edu.agh.kt.ProjectStatisticsCollector;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SdnLabListener implements IFloodlightModule, IOFMessageListener {

	protected IFloodlightProviderService floodlightProvider;
	protected static Logger logger;
	protected ITopologyService topologyService;
	protected IRoutingService routingService;
	protected IRestApiService restApiService;
	protected IOFSwitchService switchService;
	protected IStatisticsService statisticsService;
	protected static Routing routing;

	@Override
	public String getName() {
		return SdnLabListener.class.getSimpleName();
	}

	public static Routing getRouting() {
		return routing;
	}

	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public net.floodlightcontroller.core.IListener.Command receive(
			IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {
		return Command.STOP;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
		l.add(IFloodlightProviderService.class);
		l.add(IRestApiService.class);
		l.add(IRoutingService.class);
		l.add(IOFSwitchService.class);
		l.add(ITopologyService.class);
		l.add(IStatisticsService.class);
		return l;
	}

	@Override
	public void init(FloodlightModuleContext context)
			throws FloodlightModuleException {
		this.floodlightProvider = context
				.getServiceImpl(IFloodlightProviderService.class);
		this.topologyService = context.getServiceImpl(ITopologyService.class);
		this.routingService = context.getServiceImpl(IRoutingService.class);
		this.switchService = context.getServiceImpl(IOFSwitchService.class);
		this.restApiService = context.getServiceImpl(IRestApiService.class);
		this.statisticsService = context
				.getServiceImpl(IStatisticsService.class);
		logger = LoggerFactory.getLogger(SdnLabListener.class);
	}

	@Override
	public void startUp(FloodlightModuleContext context)
			throws FloodlightModuleException {
		this.floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
		this.restApiService.addRestletRoutable(new RestLab());
		this.topologyService.addListener(new TopologyListener());
		this.statisticsService.collectStatistics(true);
		routing = new Routing(this.routingService, this.switchService);
		logger.info("******************* START **************************");

	}
}



















