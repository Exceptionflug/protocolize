package dev.simplix.protocolize.api.util;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import sun.misc.Unsafe;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReflectionUtil {

    private static final String EXCEPTION_OCCURRED = "Exception occurred";

    private static final Map<Map.Entry<Class<?>, String>, Field> CACHED_FIELDS = new HashMap<>();
    private static final Map<String, Class<?>> CACHED_CLASSES = new HashMap<>();
    private static final Map<String, Constructor<?>> CACHED_CONSTRUCTORS = new HashMap<>();

    private static MethodHandle modifierSetterMethodHandle;

    static {
        MethodHandles.Lookup lookup = obtainLookup();
        try {
            modifierSetterMethodHandle = lookup.findSetter(Field.class, "modifiers", int.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.warn("Unable to find java 9+ modifier setter");
        }
    }

    private static MethodHandles.Lookup obtainLookup() {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            Unsafe unsafe = (Unsafe) theUnsafe.get(null);
            Field trustedLookup = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            return (MethodHandles.Lookup) unsafe.getObject(unsafe.staticFieldBase(trustedLookup), unsafe.staticFieldOffset(trustedLookup));
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            log.warn("Using untrusted lookup");
            return MethodHandles.lookup();
        }
    }

    public static Class<?> getClass(String classname) throws ClassNotFoundException {
        Class<?> out = CACHED_CLASSES.get(classname);
        if (out == null) {
            out = Class.forName(classname);
            CACHED_CLASSES.put(classname, out);
        }
        return out;
    }

    @SneakyThrows
    public static Class<?> getClassUnchecked(String classname) {
        return getClass(classname);
    }

    public static Class<?> getClassOrNull(String classname) {
        try {
            return getClass(classname);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static Object fieldValue(@NonNull Object instance, @NonNull String fieldName)
        throws IllegalAccessException {
        final Map.Entry<Class<?>, String> key = new AbstractMap.SimpleEntry<>(
            instance.getClass(),
            fieldName);
        final Field field = CACHED_FIELDS.computeIfAbsent(key, i -> {
            try {
                return instance.getClass().getDeclaredField(fieldName);
            } catch (final NoSuchFieldException e) {
                log.error(EXCEPTION_OCCURRED, e);
            }
            return null;
        });
        if (field == null) {
            return null;
        }
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        return field.get(instance);
    }

    @SneakyThrows
    public static void makeNonFinal(Field field) {
        if (!Modifier.isFinal(field.getModifiers())) {
            return;
        }
        if (modifierSetterMethodHandle == null) {
            Field modifiers = field(Field.class, "modifiers");
            modifiers.setAccessible(true);
            modifiers.set(field, field.getModifiers() & Modifier.FINAL);
        } else {
            modifierSetterMethodHandle.invoke(field, field.getModifiers() & Modifier.FINAL);
        }
    }

    public static <T> T fieldValue(@NonNull Field field, @NonNull Object obj) {
        try {
            return (T) field.get(obj);
        } catch (Exception exception) {
            log.error(EXCEPTION_OCCURRED, exception);
            return null;
        }
    }

    public static Field field(@NonNull Class<?> clazz, @NonNull String fieldName)
        throws NoSuchFieldException {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }

    @SneakyThrows
    public static Field fieldUnchecked(@NonNull Class<?> clazz, @NonNull String fieldName) {
        return field(clazz, fieldName);
    }

    public static Field fieldOrNull(Class<?> clazz, @NonNull String fieldName, boolean accessible) {
        if (clazz == null) {
            return null;
        }
        try {
            Field field = field(clazz, fieldName);
            if (accessible) {
                field.setAccessible(true);
            }
            return field;
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    public static void value(Object instance, String field, Object value) {
        try {
            Field f = instance.getClass().getDeclaredField(field);
            f.setAccessible(true);
            f.set(instance, value);
        } catch (Exception exception) {
            log.error(EXCEPTION_OCCURRED, exception);
        }
    }

    public static void value(
        @NonNull Class<?> clazz,
        @NonNull Object instance,
        @NonNull String field,
        @NonNull Object value) {
        try {
            Field declaredField = clazz.getDeclaredField(field);
            declaredField.setAccessible(true);
            declaredField.set(instance, value);
        } catch (Exception e) {
            log.error(EXCEPTION_OCCURRED, e);
        }
    }

    public static void valueSubclass(
        @NonNull Class<?> clazz,
        @NonNull Object instance,
        @NonNull String field,
        @NonNull Object value) {
        try {
            Field declaredField = clazz.getDeclaredField(field);
            declaredField.setAccessible(true);
            declaredField.set(instance, value);
        } catch (Exception e) {
            log.error(EXCEPTION_OCCURRED, e);
        }
    }

    public static void listFields(@NonNull Object object) {
        log.info("{} contains {} declared fields.", object.getClass().getName(), object
            .getClass()
            .getDeclaredFields().length);
        log.info("{} contains {} declared classes.", object.getClass().getName(), object
            .getClass()
            .getDeclaredClasses().length);
        Field[] declaredFields = object.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            try {
                log.info("{} -> {}", field.getName(), field.get(object));
            } catch (IllegalArgumentException | IllegalAccessException exception) {
                log.error(EXCEPTION_OCCURRED, exception);
            }
        }
    }

    public static Object fieldValue(
        @NonNull Class<?> superclass,
        @NonNull Object instance,
        @NonNull String fieldName)
        throws IllegalAccessException, NoSuchFieldException {
        Field field = superclass.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(instance);
    }

    public static Object newInstance(Class<?> clazz) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        return CACHED_CONSTRUCTORS.computeIfAbsent(clazz.getName(), s -> {
            try {
                return clazz.getConstructor();
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException("The class " + clazz.getName() + " has no accessible default constructor");
            }
        }).newInstance();
    }
}
