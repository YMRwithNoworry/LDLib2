package net.neoforged.neoforgespi.language;

import org.objectweb.asm.Type;

import java.lang.annotation.ElementType;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ModFileScanData {
    public List<AnnotationData> getAnnotations() {
        return Collections.emptyList();
    }

    public record AnnotationData(Type annotationType, ElementType targetType, Type clazz, String memberName, Map<String, Object> annotationData) {
    }
}
