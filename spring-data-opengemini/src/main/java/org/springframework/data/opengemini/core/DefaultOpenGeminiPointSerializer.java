package org.springframework.data.opengemini.core;

import io.opengemini.client.api.Point;
import io.opengemini.client.api.Precision;
import io.opengemini.client.api.QueryResult;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.opengemini.annotations.Column;
import org.springframework.data.opengemini.annotations.Measurement;
import org.springframework.data.opengemini.annotations.TimeColumn;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class DefaultOpenGeminiPointSerializer<T> implements OpenGeminiPointSerializer<T> {

    private final Class<T> clazz;
    private final MappingInfo mappingInfo;

    public DefaultOpenGeminiPointSerializer(Class<T> clazz) {
        this.clazz = clazz;
        this.mappingInfo = resolveMappingInfo(clazz);
        log.info("mapping info: {}", mappingInfo);
    }

    @Override
    public Point serialize(T t) {
        try {
            Map<String, Object> fields = new HashMap<>();
            Map<String, String> tags = new HashMap<>();

            for (Map.Entry<String, Field> fieldEntry : mappingInfo.fieldMap.entrySet()) {
                fields.put(fieldEntry.getKey(), fieldEntry.getValue().get(t));
            }

            for (Map.Entry<String, Field> fieldEntry : mappingInfo.tagFieldMap.entrySet()) {
                tags.put(fieldEntry.getKey(), Objects.toString(fieldEntry.getValue().get(t), null));
            }

            Point point = new Point();
            point.setMeasurement(mappingInfo.measurementName);
            point.setFields(fields);
            point.setTags(tags);
            point.setTime((Long) mappingInfo.timeField.get(t));
            point.setPrecision(mappingInfo.timePrecision);
            return point;
        } catch (Exception e) {
            log.error("Could not serialize point", e);
        }
        return null;
    }

    @Override
    public T deserialize(Point point) {
        return null;
    }

    @Override
    public List<T> deserialize(QueryResult queryResult) {
        return Collections.emptyList();
    }

    private MappingInfo resolveMappingInfo(Class<T> clazz) {
        MappingInfo mappingInfo = new MappingInfo();

        Measurement msAnnotation = clazz.getAnnotation(Measurement.class);
        if (msAnnotation == null) {
            throw new IllegalArgumentException("Class " + this.clazz.getName() + " has no @Measurement annotation");
        }

        mappingInfo.measurementName = msAnnotation.name();

        Map<String, Field> fieldMap = new HashMap<>();
        Map<String, Field> tagFieldMap = new HashMap<>();
        for (Field field : clazz.getDeclaredFields()) {
            Column colAnnotation = field.getAnnotation(Column.class);
            if (colAnnotation != null) {
                field.setAccessible(true);
                if (colAnnotation.tag()) {
                    if (!String.class.equals(field.getType())) {
                        throw new IllegalArgumentException("Class " + this.clazz.getName());
                    }
                    tagFieldMap.put(getFieldName(field, colAnnotation), field);
                } else {
                    fieldMap.put(getFieldName(field, colAnnotation), field);
                }
                continue;
            }
            TimeColumn timeColAnnotation = field.getAnnotation(TimeColumn.class);
            if (timeColAnnotation != null) {
                field.setAccessible(true);

                mappingInfo.timeField = field;
                mappingInfo.timePrecision = timeColAnnotation.precision();
            }
        }
        mappingInfo.fieldMap = fieldMap;
        mappingInfo.tagFieldMap = tagFieldMap;

        return mappingInfo;
    }

    private String getFieldName(Field field, Column colAnnotation) {
        if (colAnnotation != null && !colAnnotation.name().isEmpty()) {
            return colAnnotation.name();
        }
        return field.getName();
    }


    @ToString
    private static class MappingInfo {
        private String measurementName;
        private Map<String, Field> fieldMap;
        private Map<String, Field> tagFieldMap;
        private Field timeField;
        private Precision timePrecision;
    }
}
