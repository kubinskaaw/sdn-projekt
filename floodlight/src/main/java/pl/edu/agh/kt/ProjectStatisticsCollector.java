package pl.edu.agh.kt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import net.floodlightcontroller.core.IOFSwitch;

import org.projectfloodlight.openflow.protocol.OFPortStatsEntry;
import org.projectfloodlight.openflow.protocol.OFPortStatsReply;
import org.projectfloodlight.openflow.protocol.OFStatsReply;
import org.projectfloodlight.openflow.protocol.OFStatsRequest;
import org.projectfloodlight.openflow.types.OFPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.TimeoutException;

public class ProjectStatisticsCollector {

	private static final Logger logger = LoggerFactory.getLogger(ProjectStatisticsCollector.class);
	private IOFSwitch sw;
	public static final int PORT_STATISTICS_POLLING_INTERVAL = 3000; // in ms
	private static ProjectStatisticsCollector singleton;

	public class PortStatisticsPoller extends TimerTask {
		
		private List<Long> lastStatsTx = new ArrayList<>(Arrays.asList(0L, 0L, 0L, 0L));
		private List<Long> lastStatsRx = new ArrayList<>(Arrays.asList(0L, 0L, 0L, 0L));
		
		private final Logger logger = LoggerFactory.getLogger(PortStatisticsPoller.class);

		@Override
		public void run() {
			logger.debug("run() begin");
			synchronized (ProjectStatisticsCollector.this) {
				if (sw == null) { // no switch
					logger.error("run() end (no switch)");
					return;
				}
				ListenableFuture<?> future;
				List<OFStatsReply> values = null;
				OFStatsRequest<?> req = null;
				req = sw.getOFFactory().buildPortStatsRequest()
						.setPortNo(OFPort.ANY).build();
				try {
					if (req != null) {
						future = sw.writeStatsRequest(req);
						values = (List<OFStatsReply>) future.get(PORT_STATISTICS_POLLING_INTERVAL * 1000 / 2, TimeUnit.MILLISECONDS);
					}
					OFPortStatsReply psr = (OFPortStatsReply) values.get(0);
					
					for (int i = 1; i < 5; i++) {
						Long portStatTx = psr.getEntries().get(i).getTxBytes().getValue();
						Long portStatRx = psr.getEntries().get(i).getRxBytes().getValue();
						
						logger.info("Stats for port {}", i);
						logger.info("Tx={} bytes/s Rx={} bytes/s", portStatTx - this.lastStatsTx.get(i), portStatRx - this.lastStatsRx.get(i));
						
						this.lastStatsTx.set(i, portStatTx);
						this.lastStatsRx.set(i, portStatRx);
					}
					
					logger.info("Switch id: {}", sw.getId());
					for (OFPortStatsEntry pse : psr.getEntries()) {
						if (pse.getPortNo().getPortNumber() > 0) {
							logger.info("\tport number: {}, txPackets: {}", pse
									.getPortNo().getPortNumber(), pse
									.getTxPackets().getValue());
						}
					}
				} catch (InterruptedException | ExecutionException | TimeoutException ex) {
					logger.error("Error during statistics polling", ex);
				}
			}
			logger.debug("run() end");
		}
	}

	private ProjectStatisticsCollector(IOFSwitch sw) {
		this.sw = sw;
		new Timer().scheduleAtFixedRate(new PortStatisticsPoller(), 0, PORT_STATISTICS_POLLING_INTERVAL);
	}

	public static ProjectStatisticsCollector getInstance(IOFSwitch sw) {
		logger.debug("getInstance() begin");
		synchronized (ProjectStatisticsCollector.class) {
			if (singleton == null) {
				logger.debug("Creating StatisticsCollector singleton");
				singleton = new ProjectStatisticsCollector(sw);
			}
		}
		logger.debug("getInstance() end");
		return singleton;
	}
}
