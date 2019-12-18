package de.exceptionflug.protocolize.plugin.tests;

import com.google.common.base.Preconditions;
import de.exceptionflug.bungeetestsuite.BungeeCordTestSuite;
import de.exceptionflug.protocolize.ProtocolizePlugin;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.PluginDescription;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class BungeeCordLatestTest {

    private static BungeeCordTestSuite TEST_SUITE = new BungeeCordTestSuite();

    public BungeeCordLatestTest() throws IOException {
        launchBungee();
    }

    public void launchBungee() {
        try {
            TEST_SUITE.launchBungeeCord(new String[0]);
        } catch (Exception e) {
            System.out.println("BungeeCord bootstrap error");
            e.printStackTrace();
        }
    }

    @Test
    public void testEnabled() throws InterruptedException, ReflectiveOperationException {
        PluginDescription description = new PluginDescription();
        description.setFile(new File("src/main/resources/plugin.yml"));
        description.setName("protocolize-plugin");
        description.setAuthor("Exceptionflug");
        description.setVersion("internal-test");
        description.setMain("de.exceptionflug.protocolize.ProtocolizePlugin");
        Thread.sleep(1500);
        TEST_SUITE.enablePlugin(description);

        ProtocolizePlugin plugin = (ProtocolizePlugin) ProxyServer.getInstance().getPluginManager().getPlugin("protocolize-plugin");
        Preconditions.checkState(plugin.isEnabled(), "Plugin was not enabled successfully");
    }

    @After
    public void cleanUp() throws IOException, InterruptedException {
        System.out.println("Exit test suite...");
        ProxyServer.getInstance().stop();
        Thread.sleep(1000);
        FileUtils.deleteDirectory(new File("plugins"));
        FileUtils.deleteDirectory(new File("modules"));
        FileUtils.deleteQuietly(new File("config.yml"));
        FileUtils.forceDeleteOnExit(new File("proxy.log.0"));
        FileUtils.forceDeleteOnExit(new File("proxy.log.0.lck"));
    }

}
