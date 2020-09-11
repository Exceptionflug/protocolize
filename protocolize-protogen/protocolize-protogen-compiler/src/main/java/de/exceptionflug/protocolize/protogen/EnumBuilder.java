package de.exceptionflug.protocolize.protogen;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.objectweb.asm.Opcodes.*;

public class EnumBuilder {

  private final ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
  private final List<Constructor> constructors = new ArrayList<>();
  private final List<Method> methods = new ArrayList<>();
  private final List<Constant> constants = new ArrayList<>();
  private final List<Field> fields = new ArrayList<>();
  private final String enumName;

  public EnumBuilder(final String enumName, final String sourceName) {
    classWriter.visit(V1_8, ACC_PUBLIC + ACC_FINAL + ACC_SUPER + ACC_ENUM, enumName, "Ljava/lang/Enum<L" + enumName + ";>;", "java/lang/Enum", null);
    classWriter.visitSource(sourceName, null);
    this.enumName = enumName;
  }

  public EnumBuilder addConstant(final Constant constant) {
    constants.add(constant);
    return this;
  }

  public EnumBuilder addConstructor(final Constructor constructor) {
    constructors.add(constructor);
    return this;
  }

  public EnumBuilder addMethod(final Method method) {
    methods.add(method);
    return this;
  }

  public EnumBuilder addField(final Field field) {
    fields.add(field);
    return this;
  }

  public byte[] buildByteArray() {
    // make constants
    for (final Constant constant : constants) {
      classWriter.visitField(ACC_PUBLIC + ACC_STATIC + ACC_FINAL + ACC_ENUM, constant.getName(), "L" + enumName + ";", null, null).visitEnd();
    }

    // make fields
    for (final Field field : fields) {
      classWriter.visitField(field.getModifier(), field.getName(), getTypeSignature(field.getType()), null, null).visitEnd();
    }

    // make $VALUES array
    classWriter.visitField(ACC_PRIVATE + ACC_FINAL + ACC_STATIC + ACC_SYNTHETIC, "$VALUES", "[L" + enumName + ";", null, null).visitEnd();

    makeValuesMethod();
    makeValueOfMethod();

    // make constructors
    for (final Constructor constructor : constructors) {
      makeConstructor(constructor.getModifier(), constructor.getParams(), constructor.getMethodVisitorConsumer());
    }

    // make methods
    for (final Method method : methods) {
      makeMethod(method);
    }

    makeStaticInitializer();

    classWriter.visitEnd();
    return classWriter.toByteArray();
  }

  private void makeStaticInitializer() {
    final MethodVisitor methodVisitor = classWriter.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
    methodVisitor.visitCode();
    int lineNumber = 9;
    for (final Constant constant : constants) {
      lineNumber++;
      final Label l = new Label();
      methodVisitor.visitLabel(l);
      methodVisitor.visitLineNumber(lineNumber, l);
      methodVisitor.visitTypeInsn(NEW, enumName);
      methodVisitor.visitInsn(DUP);
      methodVisitor.visitLdcInsn(constant.getName());
      methodVisitor.visitInsn(ICONST_0);
      constant.getMethodVisitorConsumer().accept(methodVisitor);
      final List<String> params = new ArrayList<>();
      for (final Class param : constant.getConstructor().getParams().values()) {
        params.add(getTypeSignature(param));
      }
      methodVisitor.visitMethodInsn(INVOKESPECIAL, enumName, "<init>", "(Ljava/lang/String;I" + String.join("", params) + ")V", false);
      methodVisitor.visitFieldInsn(PUTSTATIC, enumName, constant.getName(), "L" + enumName + ";");
    }
    final Label larray = new Label();
    methodVisitor.visitLabel(larray);
    methodVisitor.visitLineNumber(3, larray);
    methodVisitor.visitIntInsn(SIPUSH, constants.size());
    methodVisitor.visitTypeInsn(ANEWARRAY, enumName);
    methodVisitor.visitInsn(DUP);
    int index = 0;
    for (final Constant constant : constants) {
      methodVisitor.visitInsn(DUP);
      methodVisitor.visitIntInsn(SIPUSH, index);
      methodVisitor.visitFieldInsn(GETSTATIC, enumName, constant.getName(), "L" + enumName + ";");
      methodVisitor.visitInsn(AASTORE);
      index++;
    }
    methodVisitor.visitFieldInsn(PUTSTATIC, enumName, "$VALUES", "[L" + enumName + ";");
    methodVisitor.visitInsn(RETURN);
    methodVisitor.visitMaxs(0, 0); // params are ignored
    methodVisitor.visitEnd();
  }

  private void makeMethod(final Method method) {
    final List<String> params = new ArrayList<>();
    for (final Class clazz : method.getParams().values()) {
      params.add(getTypeSignature(clazz));
    }
    final MethodVisitor methodVisitor = classWriter.visitMethod(method.getModifier(), method.getName(), "(" + String.join("", params) + ")" + (method.getReturnType() != null ? getTypeSignature(method.getReturnType()) : "V"), null, null);
    methodVisitor.visitCode();
    method.getMethodVisitorConstructor().accept(methodVisitor);
    methodVisitor.visitMaxs(0, 0); // params are ignored
    methodVisitor.visitEnd();
  }

  private void makeConstructor(final int modifier, final Map<String, Class> paramTypes, final Consumer<MethodVisitor> methodVisitorConsumer) {
    final List<String> params = new ArrayList<>();
    for (final Class clazz : paramTypes.values()) {
      params.add(getTypeSignature(clazz));
    }
    final String join = String.join("", params);
    final MethodVisitor methodVisitor = classWriter.visitMethod(modifier, "<init>", "(Ljava/lang/String;I" + join + ")V", "(" + join + ")V", null);
    methodVisitor.visitCode();
    final Label l0 = new Label();
    methodVisitor.visitLabel(l0);
    methodVisitor.visitLineNumber(4, l0);
    methodVisitor.visitVarInsn(ALOAD, 0);
    methodVisitor.visitVarInsn(ALOAD, 1);
    methodVisitor.visitVarInsn(ILOAD, 2);
    methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Enum", "<init>", "(Ljava/lang/String;I)V", false);
    if (methodVisitorConsumer != null)
      methodVisitorConsumer.accept(methodVisitor);
    final Label lthis = new Label();
    methodVisitor.visitLabel(lthis);
    methodVisitor.visitLocalVariable("this", "L" + enumName + ";", null, l0, lthis, 0);
    int stackIndex = 2;
    for (final String paramName : paramTypes.keySet()) {
      stackIndex++;
      methodVisitor.visitLocalVariable(paramName, getTypeSignature(paramTypes.get(paramName)), null, l0, lthis, stackIndex);
    }
    methodVisitor.visitMaxs(0, 0); // params are ignored
    methodVisitor.visitEnd();
  }

  private String getTypeSignature(final Class clazz) {
    String param = "";
    if (clazz.isArray()) {
      return clazz.getName().replace(".", "/");
    }
    if (clazz.isPrimitive()) {
      if (clazz.equals(int.class)) {
        param = param + "I";
      } else if (clazz.equals(long.class)) {
        param = param + "J";
      } else if (clazz.equals(boolean.class)) {
        param = param + "Z";
      } else if (clazz.equals(byte.class)) {
        param = param + "B";
      } else if (clazz.equals(short.class)) {
        param = param + "S";
      } else if (clazz.equals(float.class)) {
        param = param + "F";
      } else if (clazz.equals(double.class)) {
        param = param + "D";
      } else {
        throw new RuntimeException("Cannot convert to signature primitive: " + clazz.getName());
      }
    } else {
      param = param + "L" + clazz.getName().replace(".", "/") + ";";
    }
    return param;
  }

  private void makeValueOfMethod() {
    final MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_STATIC, "valueOf", "(Ljava/lang/String;)L" + enumName + ";", null, null);
    methodVisitor.visitCode();
    final Label l0 = new Label();
    methodVisitor.visitLabel(l0);
    methodVisitor.visitLineNumber(3, l0);
    methodVisitor.visitLdcInsn(Type.getType("L" + enumName + ";"));
    methodVisitor.visitVarInsn(ALOAD, 0);
    methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Enum", "valueOf", "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Enum;", false);
    methodVisitor.visitTypeInsn(CHECKCAST, enumName);
    methodVisitor.visitInsn(ARETURN);
    final Label l1 = new Label();
    methodVisitor.visitLabel(l1);
    methodVisitor.visitLocalVariable("name", "Ljava/lang/String;", null, l0, l1, 0);
    methodVisitor.visitMaxs(0, 0); // params are ignored
    methodVisitor.visitEnd();
  }

  private void makeValuesMethod() {
    final MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_STATIC, "values", "()[L" + enumName + ";", null, null);
    methodVisitor.visitCode();
    final Label l0 = new Label();
    methodVisitor.visitLabel(l0);
    methodVisitor.visitLineNumber(3, l0);
    methodVisitor.visitFieldInsn(GETSTATIC, enumName, "$VALUES", "[L" + enumName + ";");
    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "[L" + enumName + ";", "clone", "()Ljava/lang/Object;", false);
    methodVisitor.visitTypeInsn(CHECKCAST, "[L" + enumName + ";");
    methodVisitor.visitInsn(ARETURN);
    methodVisitor.visitMaxs(0, 0); // params are ignored
    methodVisitor.visitEnd();
  }

}
