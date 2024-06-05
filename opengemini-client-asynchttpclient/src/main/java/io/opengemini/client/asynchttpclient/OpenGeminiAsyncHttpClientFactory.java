package io.opengemini.client.asynchttpclient;

import io.opengemini.client.api.AuthConfig;
import io.opengemini.client.api.AuthType;
import io.opengemini.client.api.BatchConfig;
import io.opengemini.client.api.OpenGeminiException;
import io.opengemini.client.api.OpengeminiConst;
import io.opengemini.client.api.TlsConfig;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

public class OpenGeminiAsyncHttpClientFactory {

    public static OpenGeminiAsyncHttpClient create(Configuration configuration) throws OpenGeminiException {
        if (configuration.getAddresses() == null || configuration.getAddresses().isEmpty()) {
            throw new OpenGeminiException("at least one address is required");
        }

        if (configuration.getTimeout() == null || configuration.getTimeout().isNegative()) {
            configuration.setTimeout(OpengeminiConst.DEFAULT_TIMEOUT);
        }

        if (configuration.getConnectTimeout() == null || configuration.getConnectTimeout().isNegative()) {
            configuration.setConnectTimeout(OpengeminiConst.DEFAULT_CONNECT_TIMEOUT);
        }

        AuthConfig authConfig = configuration.getAuthConfig();
        if (authConfig != null) {
            validateAuthConfig(authConfig);
        }

        BatchConfig batchConfig = configuration.getBatchConfig();
        if (batchConfig != null) {
            validateBatchConfig(batchConfig);
        }

        TlsConfig tlsConfig = configuration.getTlsConfig();
        if (tlsConfig != null) {
            validateTlsConfig(tlsConfig);
        }
        return new OpenGeminiAsyncHttpClient(configuration);
    }

    private static void validateAuthConfig(AuthConfig authConfig) throws OpenGeminiException {
        if (authConfig.getAuthType() == AuthType.TOKEN && StringUtils.isEmpty(authConfig.getToken())) {
            throw new OpenGeminiException("invalid auth config due to empty token");
        }

        if (authConfig.getAuthType() == AuthType.PASSWORD) {
            if (StringUtils.isEmpty(authConfig.getUsername())) {
                throw new OpenGeminiException("invalid auth config due to empty username");
            }
            if (StringUtils.isEmpty(authConfig.getPassword())) {
                throw new OpenGeminiException("invalid auth config due to empty password");
            }
        }
    }

    private static void validateBatchConfig(BatchConfig batchConfig) throws OpenGeminiException {
        if (batchConfig.getBatchInterval() <= 0) {
            throw new OpenGeminiException("batch enabled, batch interval must be great than 0");
        }
        if (batchConfig.getBatchSize() <= 0) {
            throw new OpenGeminiException("batch enabled, batch size must be great than 0");
        }
    }

    private static void validateTlsConfig(TlsConfig tlsConfig) throws OpenGeminiException {
        boolean enableTls = !tlsConfig.tlsVerifyDisabled;
        if (enableTls && ObjectUtils.anyNull(tlsConfig.trustStorePath, tlsConfig.trustStorePassword)) {
            throw new OpenGeminiException("tls verification enabled, trust store path and password must not be null");
        }
    }
}
