package de.exceptionflug.protocolize.protogen;

import org.objectweb.asm.MethodVisitor;

import java.util.Map;
import java.util.function.Consumer;

public class Constructor {

  private final int modifier;
  private final Consumer<MethodVisitor> methodVisitorConsumer;
  private final Map<String, Class> params;

  public Constructor(final int modifier, final Map<String, Class> params, final Consumer<MethodVisitor> methodVisitorConsumer) {
    this.modifier = modifier;
    this.methodVisitorConsumer = methodVisitorConsumer;
    this.params = params;
  }

  public Map<String, Class> getParams() {
    return params;
  }

  public Consumer<MethodVisitor> getMethodVisitorConsumer() {
    return methodVisitorConsumer;
  }

  public int getModifier() {
    return modifier;
  }

}
