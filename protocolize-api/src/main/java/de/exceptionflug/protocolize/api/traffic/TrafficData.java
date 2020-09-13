package de.exceptionflug.protocolize.api.traffic;

import net.md_5.bungee.api.connection.Connection;

public class TrafficData {

  private final Connection connection;
  private long downstreamInput, downstreamOutput, upstreamInput, upstreamOutput;
  private long downstreamInputLastMinute, downstreamOutputLastMinute, upstreamInputLastMinute, upstreamOutputLastMinute;
  private long downstreamInputCurrentMinute, downstreamOutputCurrentMinute, upstreamInputCurrentMinute, upstreamOutputCurrentMinute;

  private String downstreamBridgeName;

  public TrafficData(final Connection connection) {
    this.connection = connection;
  }

  public Connection getConnection() {
    return connection;
  }

  public long getDownstreamInput() {
    return downstreamInput;
  }

  public void setDownstreamInput(final long downstreamInput) {
    this.downstreamInput = downstreamInput;
  }

  public long getDownstreamOutput() {
    return downstreamOutput;
  }

  public void setDownstreamOutput(final long downstreamOutput) {
    this.downstreamOutput = downstreamOutput;
  }

  public long getUpstreamInput() {
    return upstreamInput;
  }

  public void setUpstreamInput(final long upstreamInput) {
    this.upstreamInput = upstreamInput;
  }

  public long getUpstreamOutput() {
    return upstreamOutput;
  }

  public void setUpstreamOutput(final long upstreamOutput) {
    this.upstreamOutput = upstreamOutput;
  }

  public long getDownstreamInputLastMinute() {
    return downstreamInputLastMinute;
  }

  public void setDownstreamInputLastMinute(final long downstreamInputLastMinute) {
    this.downstreamInputLastMinute = downstreamInputLastMinute;
  }

  public long getDownstreamOutputLastMinute() {
    return downstreamOutputLastMinute;
  }

  public void setDownstreamOutputLastMinute(final long downstreamOutputLastMinute) {
    this.downstreamOutputLastMinute = downstreamOutputLastMinute;
  }

  public long getUpstreamInputLastMinute() {
    return upstreamInputLastMinute;
  }

  public void setUpstreamInputLastMinute(final long upstreamInputLastMinute) {
    this.upstreamInputLastMinute = upstreamInputLastMinute;
  }

  public long getUpstreamOutputLastMinute() {
    return upstreamOutputLastMinute;
  }

  public void setUpstreamOutputLastMinute(final long upstreamOutputLastMinute) {
    this.upstreamOutputLastMinute = upstreamOutputLastMinute;
  }

  public long getDownstreamInputCurrentMinute() {
    return downstreamInputCurrentMinute;
  }

  public void setDownstreamInputCurrentMinute(final long downstreamInputCurrentMinute) {
    this.downstreamInputCurrentMinute = downstreamInputCurrentMinute;
  }

  public long getDownstreamOutputCurrentMinute() {
    return downstreamOutputCurrentMinute;
  }

  public void setDownstreamOutputCurrentMinute(final long downstreamOutputCurrentMinute) {
    this.downstreamOutputCurrentMinute = downstreamOutputCurrentMinute;
  }

  public long getUpstreamInputCurrentMinute() {
    return upstreamInputCurrentMinute;
  }

  public void setUpstreamInputCurrentMinute(final long upstreamInputCurrentMinute) {
    this.upstreamInputCurrentMinute = upstreamInputCurrentMinute;
  }

  public long getUpstreamOutputCurrentMinute() {
    return upstreamOutputCurrentMinute;
  }

  public void setUpstreamOutputCurrentMinute(final long upstreamOutputCurrentMinute) {
    this.upstreamOutputCurrentMinute = upstreamOutputCurrentMinute;
  }

  public String getDownstreamBridgeName() {
    return downstreamBridgeName;
  }

  public void setDownstreamBridgeName(final String downstreamBridgeName) {
    this.downstreamBridgeName = downstreamBridgeName;
  }
}
