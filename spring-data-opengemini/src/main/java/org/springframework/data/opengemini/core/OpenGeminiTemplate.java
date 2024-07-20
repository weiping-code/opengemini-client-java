package org.springframework.data.opengemini.core;

import io.opengemini.client.api.RpConfig;

public interface OpenGeminiTemplate {

    boolean isDatabaseExist(String database);

    void createDatabase(String database);

    boolean isRetentionPolicyExist(String retentionPolicy);

    void createRetentionPolicy(RpConfig rpConfig);

    <T> MeasurementOperations<T> opsForMeasurement(Class<T> clazz);

}
