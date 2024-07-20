package org.springframework.data.opengemini.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.data.opengemini.annotations.Measurement;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class OpenGeminiInitializer {

    private final Set<Class<?>> measurementClassSet;

    public OpenGeminiInitializer(String basePackage) {
        this.measurementClassSet = scanForMeasurementClass(basePackage);
        log.info("measurementClassSet={}", this.measurementClassSet);
    }

    private Set<Class<?>> scanForMeasurementClass(String basePackage) {
        if (!StringUtils.hasText(basePackage)) {
            return Collections.emptySet();
        }

        Set<Class<?>> measurementClassSet = new HashSet<>();
        ClassPathScanningCandidateComponentProvider componentProvider = new ClassPathScanningCandidateComponentProvider(
                false);
        componentProvider.addIncludeFilter(new AnnotationTypeFilter(Measurement.class));
        for (BeanDefinition candidate : componentProvider.findCandidateComponents(basePackage)) {
            String beanClassName = candidate.getBeanClassName();
            if (beanClassName != null) {
                try {
                    measurementClassSet.add(ClassUtils.forName(beanClassName, this.getClass().getClassLoader()));
                } catch (ClassNotFoundException | LinkageError ignored) {
                }
            }
        }
        return measurementClassSet;
    }

}
