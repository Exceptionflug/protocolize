package de.exceptionflug.protocolize;

import com.google.common.io.ByteStreams;
import de.exceptionflug.protocolize.api.protocol.ProtocolAPI;
import de.exceptionflug.protocolize.command.ProtocolizeCommand;
import de.exceptionflug.protocolize.command.ProxyInvCommand;
import de.exceptionflug.protocolize.command.TrafficCommand;
import de.exceptionflug.protocolize.injector.NettyPipelineInjector;
import de.exceptionflug.protocolize.inventory.InventoryModule;
import de.exceptionflug.protocolize.items.ItemsModule;
import de.exceptionflug.protocolize.listener.PlayerListener;
import de.exceptionflug.protocolize.world.WorldModule;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class ProtocolizePlugin extends Plugin {

  private final NettyPipelineInjector nettyPipelineInjector = new NettyPipelineInjector();
  private boolean enabled = false;

  public static boolean isExceptionCausedByProtocolize(final Throwable e) {
    final List<StackTraceElement> all = getEverything(e, new ArrayList<>());
    for (final StackTraceElement element : all) {
      if (element.getClassName().toLowerCase().contains("de.exceptionflug"))
        return true;
    }
    return false;
  }

  private static List<StackTraceElement> getEverything(final Throwable e, List<StackTraceElement> objects) {
    if (e.getCause() != null)
      objects = getEverything(e.getCause(), objects);
    objects.addAll(Arrays.asList(e.getStackTrace()));
    return objects;
  }

  @Override
  public void onEnable() {
    ProxyServer.getInstance().getLogger().info("======= PROTOCOLIZE =======");
    ProxyServer.getInstance().getLogger().info("Version " + getDescription().getVersion() + " by " + getDescription().getAuthor());
    if (getDescription().getVersion().endsWith(":unknown")) {
      ProxyServer.getInstance().getLogger().warning("WARNING: YOU ARE RUNNING AN UNOFFICIAL BUILD OF PROTOCOLIZE. DON'T REPORT ANY BUGS REGARDING THIS VERSION.");
    }
    if (!ProtocolAPI.getPacketRegistration().isSupportedPlatform()) {
      ProxyServer.getInstance().getLogger().log(Level.SEVERE, "");
      ProxyServer.getInstance().getLogger().log(Level.SEVERE, "!!! This is an unsupported platform !!!");
      ProxyServer.getInstance().getLogger().log(Level.SEVERE, "Protocolize won't enable on this platform. Be sure to use one of the following platforms:");
      ProxyServer.getInstance().getLogger().log(Level.SEVERE, " - BungeeCord");
      ProxyServer.getInstance().getLogger().log(Level.SEVERE, " - Waterfall");
      ProxyServer.getInstance().getLogger().log(Level.SEVERE, " - Aegis");
      ProxyServer.getInstance().getLogger().log(Level.SEVERE, " - Any BungeeCord fork based on commit 9133a6f511b4cfaca5e6a6671b58a6c3b10821ab");
      ProxyServer.getInstance().getLogger().log(Level.SEVERE, "");
      return;
    }
    if (ProtocolAPI.getPacketRegistration().isWaterfall()) {
      ProxyServer.getInstance().getLogger().info("[Protocolize] Running on Waterfall. Please report bugs regarding protocolize at https://github.com/Exceptionflug/protocolize/issues");
    }
    if (ProtocolAPI.getPacketRegistration().isAegis()) {
      ProxyServer.getInstance().getLogger().info("[Protocolize] Running on Aegis. Please report bugs regarding protocolize at https://github.com/Exceptionflug/protocolize/issues");
    }
    ProxyServer.getInstance().getPluginManager().registerListener(this, new PlayerListener(this));

    try {
      if (!getDataFolder().exists()) {
        getDataFolder().mkdir();
      }
      final File file = new File(getDataFolder(), "config.yml");
      if (!file.exists()) {
        file.createNewFile();
        try (final InputStream is = getResourceAsStream("config.yml");
             final OutputStream os = new FileOutputStream(file)) {
          ByteStreams.copy(is, os);
        }
      }
      final Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
      InventoryModule.setSpigotInventoryTracking(configuration.getBoolean("experimental.spigot-gui-inventory-tracking"));
      ItemsModule.setSpigotInventoryTracking(configuration.getBoolean("experimental.spigot-player-inventory-tracking"));
      ProtocolAPI.getEventManager().setFireBungeeEvent(configuration.getBoolean("fireBungeeEvents"));
    } catch (final IOException e) {
      ProxyServer.getInstance().getLogger().log(Level.SEVERE, "[Protocolize] Failed to load config", e);
    }


    // Init system components
    WorldModule.initModule();
    ItemsModule.initModule();
    InventoryModule.initModule();

    ProxyServer.getInstance().getPluginManager().registerCommand(this, new ProxyInvCommand());
    ProxyServer.getInstance().getPluginManager().registerCommand(this, new ProtocolizeCommand(this));
    ProxyServer.getInstance().getPluginManager().registerCommand(this, new TrafficCommand());

    enabled = true;
  }

  public NettyPipelineInjector getNettyPipelineInjector() {
    return nettyPipelineInjector;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(final boolean enabled) {
    this.enabled = enabled;
  }

}
