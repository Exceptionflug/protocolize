package de.exceptionflug.protocolize.api.traffic;

import com.google.common.collect.Maps;
import net.md_5.bungee.api.connection.Connection;

import java.util.Collection;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class TrafficManager {

  private final Map<String, TrafficData> trafficDataMap = Maps.newHashMap();

  public TrafficManager() {
    new Timer("TrafficDataPeriodTimer").schedule(new TimerTask() {
      @Override
      public void run() {
        for (final TrafficData data : trafficDataMap.values()) {
          data.setDownstreamInputLastMinute(data.getDownstreamInputCurrentMinute());
          data.setDownstreamOutputLastMinute(data.getDownstreamOutputCurrentMinute());
          data.setUpstreamInputLastMinute(data.getUpstreamInputCurrentMinute());
          data.setUpstreamOutputLastMinute(data.getUpstreamOutputCurrentMinute());
          data.setDownstreamInputCurrentMinute(0);
          data.setDownstreamOutputCurrentMinute(0);
          data.setUpstreamInputCurrentMinute(0);
          data.setUpstreamOutputCurrentMinute(0);
        }
      }
    }, 60000, 60000);
  }

  public void uncache(final String name) {
    trafficDataMap.remove(name);
  }

  public TrafficData getData(final String name, final Connection connection) {
    return trafficDataMap.computeIfAbsent(name, (x) -> new TrafficData(connection));
  }

  public Collection<TrafficData> getTrafficData() {
    return trafficDataMap.values();
  }

  public TrafficData getTrafficData(final String target) {
    return trafficDataMap.get(target);
  }
}
