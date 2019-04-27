package de.exceptionflug.protocolize.protogen;

import org.objectweb.asm.MethodVisitor;

import java.lang.reflect.Modifier;
import java.util.function.Consumer;

public class Constant extends Field {

    private final Consumer<MethodVisitor> methodVisitorConsumer;
    private final Constructor constructor;

    public Constant(String name, Consumer<MethodVisitor> methodVisitorConsumer, Constructor constructor) {
        super(name, null, Modifier.PUBLIC + Modifier.STATIC + Modifier.FINAL);
        this.methodVisitorConsumer = methodVisitorConsumer;
        this.constructor = constructor;
    }

    public Consumer<MethodVisitor> getMethodVisitorConsumer() {
        return methodVisitorConsumer;
    }

    public Constructor getConstructor() {
        return constructor;
    }
}
