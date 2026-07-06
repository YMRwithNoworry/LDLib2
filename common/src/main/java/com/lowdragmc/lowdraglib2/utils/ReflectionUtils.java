package com.lowdragmc.lowdraglib2.utils;

import com.lowdragmc.lowdraglib2.LDLib2;
import lombok.experimental.UtilityClass;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.ModFileScanData;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.*;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URI;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

@UtilityClass
public final class ReflectionUtils {
    private static final String FALLBACK_SCAN_PACKAGE = "com.lowdragmc.lowdraglib2";
    private static final Map<String, RuntimeAnnotationTargets> RUNTIME_ANNOTATION_TARGET_CACHE = new ConcurrentHashMap<>();
    private static final Map<Class<? extends Annotation>, List<Class<?>>> ANNOTATED_CLASS_CACHE = new ConcurrentHashMap<>();
    private static final Map<Class<? extends Annotation>, List<Field>> ANNOTATED_FIELD_CACHE = new ConcurrentHashMap<>();
    private static final Map<Class<? extends Annotation>, List<Method>> ANNOTATED_METHOD_CACHE = new ConcurrentHashMap<>();

    public static Class<?> getRawType(Type type, Class<?> fallback) {
        var rawType = getRawType(type);
        return rawType != null ? rawType : fallback;
    }

    public static Class<?> getRawType(Type type) {
        return switch (type) {
            case Class<?> aClass -> aClass;
            case GenericArrayType genericArrayType -> getRawType(genericArrayType.getGenericComponentType());
            case ParameterizedType parameterizedType -> getRawType(parameterizedType.getRawType());
            case null, default -> null;
        };
    }

    public static <A extends Annotation> void findAnnotationClasses(Class<A> annotationClass,
                                                                    @Nullable Predicate<Map<String, Object>> annotationPredicate,
                                                                    Consumer<Class<?>> consumer,
                                                                    Runnable onFinished) {
        org.objectweb.asm.Type annotationType = org.objectweb.asm.Type.getType(annotationClass);
        var found = false;
        for (ModFileScanData data : ModList.get().getAllScanData()) {
            for (ModFileScanData.AnnotationData annotation : data.getAnnotations()) {
                if (annotationType.equals(annotation.annotationType()) && annotation.targetType() == ElementType.TYPE) {
                    if (annotationPredicate == null || annotationPredicate.test(annotation.annotationData())) {
                        try {
                            consumer.accept(Class.forName(annotation.memberName(), false, ReflectionUtils.class.getClassLoader()));
                            found = true;
                        } catch (Throwable throwable) {
                            LDLib2.LOGGER.error("Failed to load class for notation: {}", annotation.memberName(), throwable);
                        }
                    }
                }
            }
        }
        if (!found) {
            for (var clazz : findRuntimeAnnotatedClasses(annotationClass)) {
                var annotation = clazz.getAnnotation(annotationClass);
                if (annotation != null && testRuntimeAnnotation(annotation, annotationPredicate)) {
                    consumer.accept(clazz);
                }
            }
        }
        onFinished.run();
    }

    public static <A extends Annotation> void findAnnotationStaticField(Class<A> annotationClass,
                                                                        @Nullable Predicate<Map<String, Object>> annotationPredicate,
                                                                        BiConsumer<Field, Object> consumer,
                                                                        Runnable onFinished) {
        org.objectweb.asm.Type annotationType = org.objectweb.asm.Type.getType(annotationClass);
        var found = false;
        for (ModFileScanData data : ModList.get().getAllScanData()) {
            for (ModFileScanData.AnnotationData annotation : data.getAnnotations()) {
                if (annotationType.equals(annotation.annotationType()) && annotation.targetType() == ElementType.FIELD) {
                    if (annotationPredicate == null || annotationPredicate.test(annotation.annotationData())) {
                        var clazz = annotation.clazz();
                        var fieldName = annotation.memberName();
                        try {
                            var field = Class.forName(annotation.clazz().getClassName()).getDeclaredField(fieldName);
                            if (Modifier.isStatic(field.getModifiers())) {
                                consumer.accept(field, field.get(null));
                                found = true;
                            } else {
                                LDLib2.LOGGER.error("Field is not static for notation: {} in {}", fieldName, clazz);
                            }
                        } catch (Throwable throwable) {
                            LDLib2.LOGGER.error("Failed to load static field for notation: {} in {}", fieldName, clazz, throwable);
                        }
                    }
                }
            }
        }
        if (!found) {
            for (var field : findRuntimeAnnotatedFields(annotationClass)) {
                var annotation = field.getAnnotation(annotationClass);
                if (annotation != null && testRuntimeAnnotation(annotation, annotationPredicate)) {
                    try {
                        if (Modifier.isStatic(field.getModifiers())) {
                            field.setAccessible(true);
                            consumer.accept(field, field.get(null));
                        } else {
                            LDLib2.LOGGER.error("Field is not static for notation: {} in {}", field.getName(), field.getDeclaringClass().getName());
                        }
                    } catch (Throwable throwable) {
                        LDLib2.LOGGER.error("Failed to load static field for notation: {} in {}", field.getName(), field.getDeclaringClass().getName(), throwable);
                    }
                }
            }
        }
        onFinished.run();
    }

    public static <A extends Annotation> void findAnnotationStaticMethod(Class<A> annotationClass,
                                                                         @Nullable Predicate<Map<String, Object>> annotationPredicate,
                                                                         Consumer<Method> consumer,
                                                                         Runnable onFinished) {
        org.objectweb.asm.Type annotationType = org.objectweb.asm.Type.getType(annotationClass);
        var found = false;
        for (ModFileScanData data : ModList.get().getAllScanData()) {
            for (ModFileScanData.AnnotationData annotation : data.getAnnotations()) {
                if (annotationType.equals(annotation.annotationType()) && annotation.targetType() == ElementType.METHOD) {
                    if (annotationPredicate == null || annotationPredicate.test(annotation.annotationData())) {
                        var clazz = annotation.clazz();
                        var methodFullDesc = annotation.memberName();
                        var methodName = methodFullDesc.substring(0, methodFullDesc.indexOf('('));
                        var methodDesc = methodFullDesc.substring(methodFullDesc.indexOf('('));
                        try {
                            for (var method : Class.forName(annotation.clazz().getClassName()).getDeclaredMethods()) {
                                if (method.getName().equals(methodName) &&
                                        methodDesc.equals(org.objectweb.asm.Type.getMethodDescriptor(method))) {
                                    if (Modifier.isStatic(method.getModifiers())) {
                                        consumer.accept(method);
                                        found = true;
                                    } else {
                                        LDLib2.LOGGER.error("Method is not static for notation: {} in {}", methodDesc, clazz);
                                    }
                                }
                            }
                        } catch (Throwable throwable) {
                            LDLib2.LOGGER.error("Failed to load static method for notation: {} in {}", methodDesc, clazz, throwable);
                        }
                    }
                }
            }
        }
        if (!found) {
            for (var method : findRuntimeAnnotatedMethods(annotationClass)) {
                var annotation = method.getAnnotation(annotationClass);
                if (annotation != null && testRuntimeAnnotation(annotation, annotationPredicate)) {
                    if (Modifier.isStatic(method.getModifiers())) {
                        method.setAccessible(true);
                        consumer.accept(method);
                    } else {
                        LDLib2.LOGGER.error("Method is not static for notation: {} in {}", method.getName(), method.getDeclaringClass().getName());
                    }
                }
            }
        }
        onFinished.run();
    }

    private static <A extends Annotation> boolean testRuntimeAnnotation(A annotation,
                                                                        @Nullable Predicate<Map<String, Object>> annotationPredicate) {
        return annotationPredicate == null || annotationPredicate.test(annotationData(annotation));
    }

    private static Map<String, Object> annotationData(Annotation annotation) {
        var data = new java.util.HashMap<String, Object>();
        for (var method : annotation.annotationType().getDeclaredMethods()) {
            try {
                data.put(method.getName(), method.invoke(annotation));
            } catch (Throwable throwable) {
                LDLib2.LOGGER.error("Failed to read annotation property {} from {}", method.getName(), annotation.annotationType().getName(), throwable);
            }
        }
        return data;
    }

    private static List<Class<?>> findRuntimeAnnotatedClasses(Class<? extends Annotation> annotationClass) {
        return ANNOTATED_CLASS_CACHE.computeIfAbsent(annotationClass, annotation -> {
            var classes = new java.util.ArrayList<Class<?>>();
            var classLoader = ReflectionUtils.class.getClassLoader();
            for (var className : scanLDLibAnnotationTargets(annotation).classes()) {
                var clazz = loadClass(className, classLoader);
                if (clazz != null && clazz.isAnnotationPresent(annotation)) {
                    classes.add(clazz);
                }
            }
            return classes;
        });
    }

    private static List<Field> findRuntimeAnnotatedFields(Class<? extends Annotation> annotationClass) {
        return ANNOTATED_FIELD_CACHE.computeIfAbsent(annotationClass, annotation ->
                scanLDLibAnnotationTargets(annotation).fieldOwners().stream()
                        .map(className -> loadClass(className, ReflectionUtils.class.getClassLoader()))
                        .filter(java.util.Objects::nonNull)
                        .flatMap(clazz -> java.util.Arrays.stream(clazz.getDeclaredFields()))
                        .filter(field -> field.isAnnotationPresent(annotation))
                        .toList());
    }

    private static List<Method> findRuntimeAnnotatedMethods(Class<? extends Annotation> annotationClass) {
        return ANNOTATED_METHOD_CACHE.computeIfAbsent(annotationClass, annotation ->
                scanLDLibAnnotationTargets(annotation).methodOwners().stream()
                        .map(className -> loadClass(className, ReflectionUtils.class.getClassLoader()))
                        .filter(java.util.Objects::nonNull)
                        .flatMap(clazz -> java.util.Arrays.stream(clazz.getDeclaredMethods()))
                        .filter(method -> method.isAnnotationPresent(annotation))
                        .toList());
    }

    private static RuntimeAnnotationTargets scanLDLibAnnotationTargets(Class<? extends Annotation> annotationClass) {
        return RUNTIME_ANNOTATION_TARGET_CACHE.computeIfAbsent(org.objectweb.asm.Type.getDescriptor(annotationClass), descriptor -> {
            var targets = new MutableRuntimeAnnotationTargets();
            var packagePath = FALLBACK_SCAN_PACKAGE.replace('.', '/');
            var classLoader = ReflectionUtils.class.getClassLoader();
            try {
                Enumeration<URL> resources = classLoader.getResources(packagePath);
                while (resources.hasMoreElements()) {
                    scanResource(resources.nextElement(), packagePath, descriptor, targets);
                }
            } catch (Throwable throwable) {
                LDLib2.LOGGER.error("Failed to scan {} classes for runtime annotations", FALLBACK_SCAN_PACKAGE, throwable);
            }
            return targets.freeze();
        });
    }

    private static void scanResource(URL resource, String packagePath, String annotationDescriptor, MutableRuntimeAnnotationTargets targets) throws Exception {
        switch (resource.getProtocol()) {
            case "file" -> scanDirectory(Path.of(resource.toURI()).toString(), annotationDescriptor, targets);
            case "jar" -> scanJar(((JarURLConnection) resource.openConnection()).getJarFile(), packagePath, annotationDescriptor, targets);
            default -> {
                var path = resource.toString().replace('\\', '/');
                var separator = path.indexOf("!/");
                if (separator >= 0) {
                    var jarPath = path.substring(0, separator);
                    if (jarPath.startsWith("jar:")) {
                        jarPath = jarPath.substring(4);
                    }
                    if (jarPath.startsWith("union:")) {
                        jarPath = jarPath.substring("union:".length());
                    }
                    if (jarPath.startsWith("file:")) {
                        jarPath = jarPath.substring(5);
                    }
                    var file = Path.of(URI.create("file:" + stripJarIndex(jarPath))).toFile();
                    if (file.isDirectory()) {
                        scanDirectory(new java.io.File(file, packagePath).getPath(), annotationDescriptor, targets);
                    } else {
                        scanJar(new JarFile(file), packagePath, annotationDescriptor, targets);
                    }
                }
            }
        }
    }

    private static String stripJarIndex(String jarPath) {
        var fragment = jarPath.indexOf('#');
        var encodedFragment = jarPath.toLowerCase(java.util.Locale.ROOT).indexOf("%23");
        var end = jarPath.length();
        if (fragment >= 0) {
            end = Math.min(end, fragment);
        }
        if (encodedFragment >= 0) {
            end = Math.min(end, encodedFragment);
        }
        return jarPath.substring(0, end);
    }

    private static void scanDirectory(String directoryPath, String annotationDescriptor, MutableRuntimeAnnotationTargets targets) {
        var directory = new java.io.File(directoryPath);
        var files = directory.listFiles();
        if (files == null) {
            return;
        }
        for (var file : files) {
            if (file.isDirectory()) {
                scanDirectory(file.getPath(), annotationDescriptor, targets);
            } else if (file.getName().endsWith(".class")) {
                try (var input = new FileInputStream(file)) {
                    scanClass(input, annotationDescriptor, targets);
                } catch (Throwable throwable) {
                    LDLib2.LOGGER.debug("Skipping class file {} during runtime annotation scan", file.getPath(), throwable);
                }
            }
        }
    }

    private static void scanJar(JarFile jarFile, String packagePath, String annotationDescriptor, MutableRuntimeAnnotationTargets targets) {
        try (jarFile) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                var entry = entries.nextElement();
                var entryName = entry.getName();
                if (entryName.startsWith(packagePath) && entryName.endsWith(".class")) {
                    try (var input = jarFile.getInputStream(entry)) {
                        scanClass(input, annotationDescriptor, targets);
                    } catch (Throwable throwable) {
                        LDLib2.LOGGER.debug("Skipping class entry {} during runtime annotation scan", entryName, throwable);
                    }
                }
            }
        } catch (Throwable throwable) {
            LDLib2.LOGGER.error("Failed to scan jar classes for runtime annotations", throwable);
        }
    }

    private static void scanClass(InputStream input, String annotationDescriptor, MutableRuntimeAnnotationTargets targets) throws java.io.IOException {
        new org.objectweb.asm.ClassReader(input).accept(new org.objectweb.asm.ClassVisitor(org.objectweb.asm.Opcodes.ASM9) {
            private String className;

            @Override
            public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                className = name.replace('/', '.');
            }

            @Override
            public org.objectweb.asm.AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                if (annotationDescriptor.equals(descriptor)) {
                    targets.classes.add(className);
                }
                return null;
            }

            @Override
            public org.objectweb.asm.FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
                return new org.objectweb.asm.FieldVisitor(org.objectweb.asm.Opcodes.ASM9) {
                    @Override
                    public org.objectweb.asm.AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                        if (annotationDescriptor.equals(descriptor)) {
                            targets.fieldOwners.add(className);
                        }
                        return null;
                    }
                };
            }

            @Override
            public org.objectweb.asm.MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                return new org.objectweb.asm.MethodVisitor(org.objectweb.asm.Opcodes.ASM9) {
                    @Override
                    public org.objectweb.asm.AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                        if (annotationDescriptor.equals(descriptor)) {
                            targets.methodOwners.add(className);
                        }
                        return null;
                    }
                };
            }
        }, org.objectweb.asm.ClassReader.SKIP_CODE | org.objectweb.asm.ClassReader.SKIP_DEBUG | org.objectweb.asm.ClassReader.SKIP_FRAMES);
    }

    private static final class MutableRuntimeAnnotationTargets {
        private final Set<String> classes = new HashSet<>();
        private final Set<String> fieldOwners = new HashSet<>();
        private final Set<String> methodOwners = new HashSet<>();

        private RuntimeAnnotationTargets freeze() {
            return new RuntimeAnnotationTargets(
                    Collections.unmodifiableSet(classes),
                    Collections.unmodifiableSet(fieldOwners),
                    Collections.unmodifiableSet(methodOwners));
        }
    }

    private record RuntimeAnnotationTargets(Set<String> classes, Set<String> fieldOwners, Set<String> methodOwners) {
    }

    @Nullable
    private static Class<?> loadClass(String className, ClassLoader classLoader) {
        try {
            return Class.forName(className, false, classLoader);
        } catch (Throwable throwable) {
            LDLib2.LOGGER.debug("Skipping class {} during runtime annotation scan", className, throwable);
            return null;
        }
    }

}
