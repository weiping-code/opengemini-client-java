package io.opengemini.client.spring.data.core;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.util.List;

@Setter
@Getter
public class MeasurementScanConfigurer implements InitializingBean {

    private List<String> basePackages;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(basePackages, "Property 'basePackage' is required");
        Assert.notEmpty(basePackages, "Property 'basePackage' is required");
    }
}
