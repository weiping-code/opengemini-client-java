package org.springframework.data.opengemini.core;

import io.opengemini.client.api.OpenGeminiAsyncClient;
import org.springframework.data.opengemini.config.OpenGeminiProperties;

public class DefaultOpenGeminiConnectionFactory implements OpenGeminiConnectionFactory {

    private final OpenGeminiProperties properties;

    public DefaultOpenGeminiConnectionFactory(OpenGeminiProperties properties) {
        this.properties = properties;
    }

    @Override
    public OpenGeminiAsyncClient getConnection() {
        return null;
    }
}
