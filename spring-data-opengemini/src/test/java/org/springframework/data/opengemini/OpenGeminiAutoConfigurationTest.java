package org.springframework.data.opengemini;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.opengemini.core.OpenGeminiTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringJUnitConfig
@SpringBootTest(classes = OpenGeminiApplication.class,
        properties = {"spring.opengemini.base-package=org.springframework.data.opengemini"})
public class OpenGeminiAutoConfigurationTest {

    @Autowired
    private OpenGeminiTemplate openGeminiTemplate;

    @Test
    public void testApp() {
        assertNotNull(openGeminiTemplate);
    }

}
