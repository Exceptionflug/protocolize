package de.exceptionflug.protocolize.plugin.tests;

import de.exceptionflug.bungeetestsuite.BungeeCordTestSuite;
import de.exceptionflug.protocolize.ProtocolizePlugin;
import de.exceptionflug.protocolize.api.protocol.ProtocolAPI;
import de.exceptionflug.protocolize.inventory.packet.ClickWindow;
import de.exceptionflug.protocolize.items.packet.HeldItemChange;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.ProtocolConstants;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.Preconditions;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

public class BungeeCordLatestTest {

    private static final Executor BUNGEE_EXECUTOR = Executors.newSingleThreadExecutor();
    private static final BungeeCordTestSuite TEST_SUITE = new BungeeCordTestSuite();

    public BungeeCordLatestTest() throws IOException {
        launchBungee();
    }

    public void launchBungee() throws MalformedURLException {
        try {
            Class<?> bungeeCordLauncherClass = urlClassLoader.loadClass("net.md_5.bungee.BungeeCordLauncher");

            BUNGEE_EXECUTOR.execute(() -> {
                try {
                    TEST_SUITE.launchBungeeCord(new String[0]);
                } catch (Exception e) {
                    System.out.println("BungeeCord bootstrap error");
                    e.printStackTrace();
                }
            });
            System.out.println("BungeeCord started");
        } catch (ClassNotFoundException e) {
            System.out.println("BungeeCord cannot be started");
            e.printStackTrace();
        }
    }

    @Test
    public void testEnabled() throws InterruptedException, TimeoutException {
        ProtocolizePlugin plugin;
        long timeout = 5;
        while(ProxyServer.getInstance() == null) {
            if(timeout == 0) {
                throw new TimeoutException("BungeeCord did not start in time");
            }
            timeout -= 1;
            Thread.sleep(1000);
        }
        Thread.sleep(1500);

        System.out.println(" === PROTOCOLIZE TEST ENVIRONMENT ===");
        System.out.println("Testing version: "+plugin.getDescription().getVersion());
        System.out.println("Is enabled: "+plugin.isEnabled());
        System.out.println("=====================================");
        Preconditions.condition(plugin.isEnabled(), "Plugin was not enabled successfully");
    }

}
