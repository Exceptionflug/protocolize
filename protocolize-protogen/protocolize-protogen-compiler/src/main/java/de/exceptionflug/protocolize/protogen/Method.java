package de.exceptionflug.protocolize.protogen;

import org.objectweb.asm.MethodVisitor;

import java.util.Map;
import java.util.function.Consumer;

public class Method {

    private final String name;
    private final int modifier;
    private final Map<String, Class> params;
    private final Class returnType;
    private final Consumer<MethodVisitor> methodVisitorConstructor;

    public Method(String name, int modifier, Map<String, Class> params, Class returnType, Consumer<MethodVisitor> methodVisitorConstructor) {
        this.name = name;
        this.modifier = modifier;
        this.params = params;
        this.returnType = returnType;
        this.methodVisitorConstructor = methodVisitorConstructor;
    }

    public String getName() {
        return name;
    }

    public int getModifier() {
        return modifier;
    }

    public Map<String, Class> getParams() {
        return params;
    }

    public Class getReturnType() {
        return returnType;
    }

    public Consumer<MethodVisitor> getMethodVisitorConstructor() {
        return methodVisitorConstructor;
    }
}
