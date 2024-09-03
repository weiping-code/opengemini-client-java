package io.opengemini.client.jdk;

import io.opengemini.client.api.AuthConfig;
import io.opengemini.client.api.AuthType;
import io.opengemini.client.api.TlsConfig;
import io.opengemini.client.common.BaseAsyncClient;
import org.jetbrains.annotations.NotNull;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;

public abstract class BaseOpenGeminiJdkClient extends BaseAsyncClient {

    protected final Configuration conf;

    protected final HttpClient client;

    public BaseOpenGeminiJdkClient(@NotNull Configuration conf) {
        super(conf);
        this.conf = conf;
        HttpClient.Builder builder = HttpClient.newBuilder()
                .connectTimeout(conf.getConnectTimeout())
                .version(HttpClient.Version.HTTP_1_1);
        if (conf.isTlsEnabled()) {
            TlsConfig tlsConfig = conf.getTlsConfig();
            builder = builder.sslContext(SslContextUtil.buildSSLContextFromJks(
                    tlsConfig.keyStorePath,
                    tlsConfig.keyStorePassword,
                    tlsConfig.trustStorePath,
                    tlsConfig.trustStorePassword,
                    tlsConfig.verifyDisabled));
        }

        AuthConfig authConfig = conf.getAuthConfig();
        if (authConfig != null) {
            AuthType authType = authConfig.getAuthType();
            if (AuthType.PASSWORD.equals(authType)) {
                builder.authenticator(
                        getAuthenticator(authConfig.getUsername(), String.valueOf(authConfig.getPassword())));
            }
        }
        this.client = builder.build();
    }

    private Authenticator getAuthenticator(String username, String password) {
        return new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password.toCharArray());
            }
        };
    }

    protected URI buildUri(String url) {
        return URI.create(nextUrlPrefix() + url);
    }

    @Override
    protected String encode(String str) {
        // jdk17 has a better way than jdk8
        return URLEncoder.encode(str, StandardCharsets.UTF_8);
    }

    @Override
    public void close() {
        // no need to close
    }
}
