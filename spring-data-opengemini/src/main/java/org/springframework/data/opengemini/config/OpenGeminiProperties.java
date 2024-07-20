package org.springframework.data.opengemini.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("spring.opengemini")
public class OpenGeminiProperties {

    private boolean enabled;

    private String basePackage;

}
