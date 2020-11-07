package de.exceptionflug.protocolize.util;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import net.md_5.bungee.api.*;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ConfigurationAdapter;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.api.scheduler.TaskScheduler;

public class DummyProxyServer extends ProxyServer {

  @Override
  public String getName() {
    return null;
  }

  @Override
  public String getVersion() {
    return null;
  }

  @Override
  public String getTranslation(String s, Object... objects) {
    return null;
  }

  @Override
  public Logger getLogger() {
    return Logger.getLogger("BungeeCord");
  }

  @Override
  public Collection<ProxiedPlayer> getPlayers() {
    return null;
  }

  @Override
  public ProxiedPlayer getPlayer(String s) {
    return null;
  }

  @Override
  public ProxiedPlayer getPlayer(UUID uuid) {
    return null;
  }

  @Override
  public Map<String, ServerInfo> getServers() {
    return null;
  }

  @Override
  public ServerInfo getServerInfo(String s) {
    return null;
  }

  @Override
  public PluginManager getPluginManager() {
    return null;
  }

  @Override
  public ConfigurationAdapter getConfigurationAdapter() {
    return null;
  }

  @Override
  public void setConfigurationAdapter(ConfigurationAdapter configurationAdapter) {

  }

  @Override
  public ReconnectHandler getReconnectHandler() {
    return null;
  }

  @Override
  public void setReconnectHandler(ReconnectHandler reconnectHandler) {

  }

  @Override
  public void stop() {

  }

  @Override
  public void stop(String s) {

  }

  @Override
  public void registerChannel(String s) {

  }

  @Override
  public void unregisterChannel(String s) {

  }

  @Override
  public Collection<String> getChannels() {
    return null;
  }

  @Override
  public String getGameVersion() {
    return null;
  }

  @Override
  public int getProtocolVersion() {
    return 0;
  }

  @Override
  public ServerInfo constructServerInfo(
      String s, InetSocketAddress inetSocketAddress, String s1, boolean b) {
    return null;
  }

  @Override
  public ServerInfo constructServerInfo(
      String s, SocketAddress socketAddress, String s1, boolean b) {
    return null;
  }

  @Override
  public CommandSender getConsole() {
    return null;
  }

  @Override
  public File getPluginsFolder() {
    return null;
  }

  @Override
  public TaskScheduler getScheduler() {
    return null;
  }

  @Override
  public int getOnlineCount() {
    return 0;
  }

  @Override
  public void broadcast(String s) {

  }

  @Override
  public void broadcast(BaseComponent... baseComponents) {

  }

  @Override
  public void broadcast(BaseComponent baseComponent) {

  }

  @Override
  public Collection<String> getDisabledCommands() {
    return null;
  }

  @Override
  public ProxyConfig getConfig() {
    return null;
  }

  @Override
  public Collection<ProxiedPlayer> matchPlayer(String s) {
    return null;
  }

  @Override
  public Title createTitle() {
    return null;
  }
}
