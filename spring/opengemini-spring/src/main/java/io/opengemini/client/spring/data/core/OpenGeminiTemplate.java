package io.opengemini.client.spring.data.core;

import io.opengemini.client.api.OpenGeminiAsyncClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Helper class that simplifies OpenGemini data access code.
 */
public class OpenGeminiTemplate implements OpenGeminiOperations {

    private final OpenGeminiAsyncClient asyncClient;
    private final OpenGeminiSerializerFactory serializerFactory;
    private final Map<Class<?>, MeasurementOperations<?>> measurementOperationMap = new ConcurrentHashMap<>();

    public OpenGeminiTemplate(OpenGeminiAsyncClient asyncClient, OpenGeminiSerializerFactory serializerFactory) {
        this.asyncClient = asyncClient;
        this.serializerFactory = serializerFactory;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> MeasurementOperations<T> opsForMeasurement(Class<T> clazz) {
        Function<Class<?>, MeasurementOperations<?>> operationsFunction = aClass -> new DefaultMeasurementOperations<>(
                asyncClient, serializerFactory.getSerializer(aClass));
        return (MeasurementOperations<T>) measurementOperationMap.computeIfAbsent(clazz, operationsFunction);
    }
}
