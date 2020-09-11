package de.exceptionflug.protocolize.command;

import de.exceptionflug.protocolize.api.protocol.ProtocolAPI;
import de.exceptionflug.protocolize.api.traffic.TrafficData;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.text.DecimalFormat;

public class TrafficCommand extends Command {

  public TrafficCommand() {
    super("traffic", "protocolize.command.traffic");
  }

  @Override
  public void execute(final CommandSender commandSender, final String[] args) {
    if (commandSender instanceof ProxiedPlayer) {
      final ProxiedPlayer p = (ProxiedPlayer) commandSender;
      if (args.length == 0) {
        showStatisticsFor(p, p.getName());
      } else {
        showStatisticsFor(p, args[0]);
      }
    }
  }

  private void showStatisticsFor(final ProxiedPlayer p, final String target) {
    final TrafficData trafficData = ProtocolAPI.getTrafficManager().getTrafficData(target);
    if (trafficData == null) {
      p.sendMessage("§cThere are no traffic information available for §e" + target);
    } else {
      p.sendMessage("Traffic data for §e" + target + "§r:");
      p.sendMessage("§7UPSTREAM (Proxy <-> §e" + target + "§7):");
      p.sendMessage("   Input last minute: §e" + readableFileSize(trafficData.getUpstreamInputLastMinute()) + "§r, Output last minute: §e" + readableFileSize(trafficData.getUpstreamOutputLastMinute()));
      p.sendMessage("   Traffic last minute: §e" + readableFileSize(trafficData.getUpstreamInputLastMinute() + trafficData.getUpstreamOutputLastMinute()));
      p.sendMessage("   Input since connected: §e" + readableFileSize(trafficData.getUpstreamInput()) + "§r, Output since connected: §e" + readableFileSize(trafficData.getUpstreamOutput()));
      p.sendMessage("   Traffic since connected: §e" + readableFileSize(trafficData.getUpstreamOutput() + trafficData.getUpstreamInput()));
      p.sendMessage("§7DOWNSTREAM (Proxy <-> §e" + trafficData.getDownstreamBridgeName() + "§7):");
      p.sendMessage("   Input last minute: §e" + readableFileSize(trafficData.getDownstreamInputLastMinute()) + "§r, Output last minute: §e" + readableFileSize(trafficData.getDownstreamOutputLastMinute()));
      p.sendMessage("   Traffic last minute: §e" + readableFileSize(trafficData.getDownstreamInputLastMinute() + trafficData.getDownstreamOutputLastMinute()));
      p.sendMessage("   Input since connected: §e" + readableFileSize(trafficData.getDownstreamInput()) + "§r, Output since connected: §e" + readableFileSize(trafficData.getDownstreamOutput()));
      p.sendMessage("   Traffic since connected: §e" + readableFileSize(trafficData.getDownstreamOutput() + trafficData.getDownstreamInput()));
      p.sendMessage("Input total: §e" + readableFileSize(trafficData.getDownstreamInput() + trafficData.getUpstreamInput()));
      p.sendMessage("Output total: §e" + readableFileSize(trafficData.getDownstreamOutput() + trafficData.getUpstreamOutput()));
      p.sendMessage("Traffic total: §e" + readableFileSize(trafficData.getDownstreamInput() + trafficData.getUpstreamInput() + trafficData.getDownstreamOutput() + trafficData.getUpstreamOutput()));
    }
  }


  /*
  By Mr Ed: https://stackoverflow.com/questions/3263892/format-file-size-as-mb-gb-etc
   */
  public String readableFileSize(final long size) {
    if (size <= 0) return "0";
    final String[] units = new String[]{"B", "kB", "MB", "GB", "TB"};
    final int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
    return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
  }

}
