package pl.edu.agh.kt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.OFPort;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class LabRestServer extends ServerResource {
	
	protected static Logger logger = LoggerFactory.getLogger(LabRestServer.class);
	
	private static final ObjectMapper mapper;
	
	static {
		mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
	}

	@Post("json")
	public String addHosts(String text) throws JsonParseException, JsonMappingException, IOException {
		HostDTO[] hostDTOArray = deserialize(text);
		List<HostDTO> hostDTOList = Arrays.asList(hostDTOArray);
		List<Host> hostList = new ArrayList<>();
		for (HostDTO host : hostDTOList) {
			hostList.add(new Host(IPv4Address.of(host.getIp()), OFPort.of(host.getPort()), DatapathId.of(host.getDatapathId())));
		}
		
		SdnLabListener.getRouting().addHosts(hostList);
		return "Successfully added host";
	}
	
	@Get("json")
	public String calculatePaths() {
		SdnLabListener.getRouting().calculatePathsToHosts();
		
		return "Successfully recalculated paths";
	}
	
	public static HostDTO[] deserialize(String text) throws JsonParseException, JsonMappingException, IOException {
		return mapper.readValue(text, HostDTO[].class);
	}
}
