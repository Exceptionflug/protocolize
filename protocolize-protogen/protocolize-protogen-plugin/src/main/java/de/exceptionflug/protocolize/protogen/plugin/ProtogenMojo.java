package de.exceptionflug.protocolize.protogen.plugin;

import de.exceptionflug.protocolize.protogen.ProtogenItemTypeCompiler;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;

@Mojo(name = "compile")
public class ProtogenMojo extends AbstractMojo {

  public void execute() throws MojoExecutionException, MojoFailureException {
    getLog().info("Making AST (ItemType.proto)...");
    final File targetDir = new File("protocolize-items/target/classes/de/exceptionflug/protocolize/items");
    try {
      targetDir.mkdirs();
      ProtogenItemTypeCompiler.compile(new File("protocolize-items/src/main/json/ItemType.json"), targetDir);
      getLog().info("Compilation of ItemType.proto done!");
    } catch (final Exception e) {
      throw new MojoFailureException("Compilation failed", e);
    }
    getLog().info("Compilation done");
  }

}
