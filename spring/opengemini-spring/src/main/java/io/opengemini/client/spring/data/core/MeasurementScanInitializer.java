package io.opengemini.client.spring.data.core;

import io.opengemini.client.spring.data.annotation.Measurement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;

@Slf4j
public class MeasurementScanInitializer {

    private final Set<Class<?>> measurementClassSet;

    public MeasurementScanInitializer(MeasurementScanConfigurer configurer) {
        this.measurementClassSet = new HashSet<>();

        if (configurer != null) {
            for (String basePackage : configurer.getBasePackages()) {
                scanForMeasurementClass(basePackage);
            }
        }

        log.info("measurementClassSet={}", this.measurementClassSet);

        // TODO 按需创建数据库和RP
    }

    private void scanForMeasurementClass(String basePackage) {
        if (!StringUtils.hasText(basePackage)) {
            return;
        }

        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(Measurement.class));
        for (BeanDefinition candidate : provider.findCandidateComponents(basePackage)) {
            String beanClassName = candidate.getBeanClassName();
            if (beanClassName != null) {
                try {
                    measurementClassSet.add(ClassUtils.forName(beanClassName, this.getClass().getClassLoader()));
                } catch (ClassNotFoundException | LinkageError ignored) {
                }
            }
        }
    }

}
