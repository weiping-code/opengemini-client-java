package org.springframework.data.opengemini.core;

import io.opengemini.client.api.OpenGeminiAsyncClient;

public interface OpenGeminiConnectionFactory {

    OpenGeminiAsyncClient getConnection();

}
