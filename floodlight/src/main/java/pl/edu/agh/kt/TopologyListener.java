package pl.edu.agh.kt;

import java.util.List;

import net.floodlightcontroller.linkdiscovery.ILinkDiscovery;
import net.floodlightcontroller.linkdiscovery.ILinkDiscovery.LDUpdate;
import net.floodlightcontroller.topology.ITopologyListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TopologyListener implements ITopologyListener {
	
	protected static final Logger logger = LoggerFactory.getLogger(TopologyListener.class);

	@Override
	public void topologyChanged(List<LDUpdate> linkUpdates) {
		
		for (ILinkDiscovery.LDUpdate update : linkUpdates) {
			switch (update.getOperation()) {
				case LINK_UPDATED:
					SdnLabListener.getRouting().calculatePathsToHosts();
					break;
				case LINK_REMOVED:
					SdnLabListener.getRouting().calculatePathsToHosts();
					break;
				case SWITCH_UPDATED:
					SdnLabListener.getRouting().calculatePathsToHosts();
					break;
				case SWITCH_REMOVED:
					SdnLabListener.getRouting().calculatePathsToHosts();
					break;
				default:
					break;
			}
		}
	}

}
