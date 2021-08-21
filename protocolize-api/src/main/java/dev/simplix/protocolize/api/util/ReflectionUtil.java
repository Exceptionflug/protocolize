package dev.simplix.protocolize.api.util;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReflectionUtil {

    private static final String EXCEPTION_OCCURRED = "Exception occurred";

    private static final Map<Map.Entry<Class<?>, String>, Field> CACHED_FIELDS = new HashMap<>();
    private static final Map<String, Class<?>> CACHED_CLASSES = new HashMap<>();

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
        log.info(object.getClass().getName() + " contains " + object
                .getClass()
                .getDeclaredFields().length + " declared fields.");
        log.info(object.getClass().getName() + " contains " + object
                .getClass()
                .getDeclaredClasses().length + " declared classes.");
        Field[] declaredFields = object.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            try {
                log.info(field.getName() + " -> " + field.get(object));
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
}
