package org.springframework.data.opengemini.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.opengemini.core.DefaultOpenGeminiConnectionFactory;
import org.springframework.data.opengemini.core.DefaultOpenGeminiTemplate;
import org.springframework.data.opengemini.core.OpenGeminiConnectionFactory;
import org.springframework.data.opengemini.core.OpenGeminiInitializer;
import org.springframework.data.opengemini.core.OpenGeminiTemplate;

@AutoConfiguration
@EnableConfigurationProperties({OpenGeminiProperties.class})
public class OpenGeminiAutoConfiguration {

    private final OpenGeminiProperties properties;

    public OpenGeminiAutoConfiguration(OpenGeminiProperties properties) {
        this.properties = properties;
    }

    @Bean
    @ConditionalOnMissingBean(OpenGeminiConnectionFactory.class)
    public OpenGeminiConnectionFactory openGeminiConnectionFactory() {
        return new DefaultOpenGeminiConnectionFactory(properties);
    }

    @Bean
    @ConditionalOnMissingBean(OpenGeminiTemplate.class)
    public DefaultOpenGeminiTemplate openGeminiTemplate(OpenGeminiConnectionFactory connectionFactory) {
        return new DefaultOpenGeminiTemplate(connectionFactory);
    }

    @Bean
    public OpenGeminiInitializer openGeminiInitializer() {
        return new OpenGeminiInitializer(properties.getBasePackage());
    }

}
