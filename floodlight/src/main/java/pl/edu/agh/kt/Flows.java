package pl.edu.agh.kt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.projectfloodlight.openflow.protocol.OFFlowMod;
import org.projectfloodlight.openflow.protocol.OFPacketIn;
import org.projectfloodlight.openflow.protocol.action.OFAction;
import org.projectfloodlight.openflow.protocol.action.OFActionOutput;
import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.protocol.match.MatchField;
import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.EthType;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.IpProtocol;
import org.projectfloodlight.openflow.types.MacAddress;
import org.projectfloodlight.openflow.types.OFPort;
import org.projectfloodlight.openflow.types.OFVlanVidMatch;
import org.projectfloodlight.openflow.types.VlanVid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.packet.TCP;
import net.floodlightcontroller.packet.UDP;

public class Flows {

	private static final Logger logger = LoggerFactory.getLogger(Flows.class);

	public static short FLOWMOD_DEFAULT_IDLE_TIMEOUT = 10;
	public static short FLOWMOD_DEFAULT_HARD_TIMEOUT = 0;
	public static short FLOWMOD_DEFAULT_PRIORITY = 100;

	protected static boolean FLOWMOD_DEFAULT_MATCH_VLAN = true;
	protected static boolean FLOWMOD_DEFAULT_MATCH_MAC = true;
	protected static boolean FLOWMOD_DEFAULT_MATCH_IP_ADDR = true;
	protected static boolean FLOWMOD_DEFAULT_MATCH_TRANSPORT = true;

	public Flows() {
		logger.info("Flows() begin/end");
	}
	
	public static void rerouteElephant() {
		IPv4Address srcIp = IPv4Address.of("10.0.0.1");
		IPv4Address dstIp = IPv4Address.of("10.0.0.6");
		
		DatapathId s1 = DatapathId.of("00:00:00:00:00:00:00:01");
		DatapathId s2 = DatapathId.of("00:00:00:00:00:00:00:02");
		DatapathId s3 = DatapathId.of("00:00:00:00:00:00:00:03");
		DatapathId s4 = DatapathId.of("00:00:00:00:00:00:00:04");
		
		IOFSwitch switch1 = SdnLabListener.getRouting().getSwitch(s1);
		IOFSwitch switch2 = SdnLabListener.getRouting().getSwitch(s2);
		IOFSwitch switch3 = SdnLabListener.getRouting().getSwitch(s3);
		IOFSwitch switch4 = SdnLabListener.getRouting().getSwitch(s4);
		
		simpleArpAdd(switch1, OFPort.of(2), dstIp);
		simpleArpAdd(switch2, OFPort.of(3), dstIp);
		simpleArpAdd(switch3, OFPort.of(3), dstIp);
		simpleArpAdd(switch4, OFPort.of(1), dstIp);
		
		simpleArpAdd(switch1, OFPort.of(1), srcIp);
		simpleArpAdd(switch2, OFPort.of(2), srcIp);
		simpleArpAdd(switch3, OFPort.of(2), srcIp);
		simpleArpAdd(switch4, OFPort.of(2), srcIp);
		
		simpleElephantAdd(switch1, OFPort.of(1), OFPort.of(2), srcIp, dstIp);
		simpleElephantAdd(switch2, OFPort.of(2), OFPort.of(3), srcIp, dstIp);
		simpleElephantAdd(switch3, OFPort.of(2), OFPort.of(3), srcIp, dstIp);
		simpleElephantAdd(switch4, OFPort.of(2), OFPort.of(1), srcIp, dstIp);
		
		simpleElephantAdd(switch1, OFPort.of(2), OFPort.of(1), dstIp, srcIp);
		simpleElephantAdd(switch2, OFPort.of(3), OFPort.of(2), dstIp, srcIp);
		simpleElephantAdd(switch3, OFPort.of(3), OFPort.of(2), dstIp, srcIp);
		simpleElephantAdd(switch4, OFPort.of(1), OFPort.of(2), dstIp, srcIp);
	}
	
	public static void simpleElephantAdd(IOFSwitch sw, OFPort inPort,
			OFPort outPort, IPv4Address srcIp, IPv4Address dstIp) {
		OFFlowMod.Builder fmb = sw.getOFFactory().buildFlowAdd();
		Match m = createEthMatch(sw, srcIp, dstIp, inPort);

		OFActionOutput.Builder aob = sw.getOFFactory().actions().buildOutput();
		List<OFAction> actions = new ArrayList<OFAction>();

		aob.setPort(outPort);
		actions.add(aob.build());

		fmb.setMatch(m).setIdleTimeout(FLOWMOD_DEFAULT_IDLE_TIMEOUT)
				.setHardTimeout(FLOWMOD_DEFAULT_HARD_TIMEOUT)
				.setOutPort(outPort)
				.setPriority(FLOWMOD_DEFAULT_PRIORITY);
		fmb.setActions(actions);

		try {
			sw.write(fmb.build());
		} catch (Exception ex) {
			logger.error("Error {}", ex);
		}
	}

	public static void simpleEthAdd(IOFSwitch sw, OFPort inPort,
			OFPort outPort, IPv4Address srcIp, IPv4Address dstIp) {
		OFFlowMod.Builder fmb = sw.getOFFactory().buildFlowAdd();
		Match m = createEthMatch(sw, srcIp, dstIp, inPort);

		OFActionOutput.Builder aob = sw.getOFFactory().actions().buildOutput();
		List<OFAction> actions = new ArrayList<OFAction>();

		aob.setPort(outPort);
		actions.add(aob.build());

		fmb.setMatch(m).setIdleTimeout(FLOWMOD_DEFAULT_IDLE_TIMEOUT)
				.setHardTimeout(FLOWMOD_DEFAULT_HARD_TIMEOUT)
				.setOutPort(outPort)
				.setPriority(FLOWMOD_DEFAULT_PRIORITY);
		fmb.setActions(actions);

		try {
			sw.write(fmb.build());
		} catch (Exception ex) {
			logger.error("Error {}", ex);
		}
	}

	public static void simpleArpAdd(IOFSwitch sw, OFPort outPort,
			IPv4Address dstIp) {
		OFFlowMod.Builder fmb = sw.getOFFactory().buildFlowAdd();
		Match m = createArpMatch(sw, dstIp);

		OFActionOutput.Builder aob = sw.getOFFactory().actions().buildOutput();
		List<OFAction> actions = new ArrayList<OFAction>();

		aob.setPort(outPort);
		actions.add(aob.build());

		fmb.setMatch(m).setIdleTimeout(FLOWMOD_DEFAULT_IDLE_TIMEOUT)
				.setHardTimeout(FLOWMOD_DEFAULT_HARD_TIMEOUT)
				.setOutPort(outPort).setPriority(FLOWMOD_DEFAULT_PRIORITY);

		fmb.setActions(actions);

		try {
			sw.write(fmb.build());
		} catch (Exception ex) {
			logger.error("Error {}", ex);
		}
	}

	public static Match createEthMatch(IOFSwitch sw, IPv4Address srcIp,
			IPv4Address dstIp, OFPort inPort) {
		Match.Builder mb = sw.getOFFactory().buildMatch();

		// dodac UDP/TCP i Port docelowy i wychodzacy
		mb.setExact(MatchField.ETH_TYPE, EthType.IPv4)
				.setExact(MatchField.IPV4_SRC, srcIp)
				.setExact(MatchField.IPV4_DST, dstIp)
				.setExact(MatchField.IN_PORT, inPort);

		return mb.build();
	}

	public static Match createArpMatch(IOFSwitch sw, IPv4Address dstIp) {
		Match.Builder mb = sw.getOFFactory().buildMatch();

		mb.setExact(MatchField.ETH_TYPE, EthType.ARP)
			.setExact(MatchField.ARP_TPA, dstIp);

		return mb.build();
	}
}
