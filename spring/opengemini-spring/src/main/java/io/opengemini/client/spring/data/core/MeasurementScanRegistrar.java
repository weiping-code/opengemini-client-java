package io.opengemini.client.spring.data.core;

import io.opengemini.client.spring.data.annotation.MeasurementScan;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
public class MeasurementScanRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(@NotNull AnnotationMetadata importingClassMetadata, @NotNull BeanDefinitionRegistry registry) {
        log.info("registerBeanDefinitions importingClassMetadata={} registry={}", importingClassMetadata, registry);
        AnnotationAttributes mapperScanAttrs = AnnotationAttributes
                .fromMap(importingClassMetadata.getAnnotationAttributes(MeasurementScan.class.getName()));
        if (mapperScanAttrs != null) {
            List<String> basePackages = Arrays.stream(mapperScanAttrs.getStringArray("basePackages"))
                    .filter(StringUtils::hasText).collect(Collectors.toList());
            String beanName = generateBaseBeanName(importingClassMetadata);
            log.info("Registering measurement scan annotation, basePackages={} beanName={}", basePackages, beanName);
            registerBeanDefinitions(mapperScanAttrs, registry, beanName);
        }
    }

    void registerBeanDefinitions(AnnotationAttributes annoAttrs,
                                 BeanDefinitionRegistry registry, String beanName) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(MeasurementScanConfigurer.class);

        List<String> basePackages = Arrays.stream(annoAttrs.getStringArray("basePackages"))
                .filter(StringUtils::hasText).collect(Collectors.toList());
        builder.addPropertyValue("basePackages", basePackages);

        registry.registerBeanDefinition(beanName, builder.getBeanDefinition());
    }

    private static String generateBaseBeanName(AnnotationMetadata importingClassMetadata) {
        return importingClassMetadata.getClassName() + "#" + MeasurementScanRegistrar.class.getSimpleName();
    }

}
