package org.springframework.data.opengemini.core;

import io.opengemini.client.api.RpConfig;
import org.springframework.beans.factory.InitializingBean;

public class DefaultOpenGeminiTemplate implements OpenGeminiTemplate, InitializingBean {

    private final OpenGeminiConnectionFactory connectionFactory;

    public DefaultOpenGeminiTemplate(OpenGeminiConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public boolean isDatabaseExist(String database) {
        return false;
    }

    @Override
    public void createDatabase(String database) {

    }

    @Override
    public boolean isRetentionPolicyExist(String retentionPolicy) {
        return false;
    }

    @Override
    public void createRetentionPolicy(RpConfig rpConfig) {

    }

    @Override
    public <T> MeasurementOperations<T> opsForMeasurement(Class<T> clazz) {
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
