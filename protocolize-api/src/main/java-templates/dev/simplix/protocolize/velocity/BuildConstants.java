package dev.simplix.protocolize.velocity;

// The constants are replaced before compilation
public interface BuildConstants {
    String VERSION = "${project.version}:${build.number}";
}
