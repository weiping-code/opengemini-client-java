/*
 * Copyright 2024 openGemini Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
        ClassLoader classLoader = this.getClass().getClassLoader();
        for (BeanDefinition candidate : provider.findCandidateComponents(basePackage)) {
            String beanClassName = candidate.getBeanClassName();
            if (beanClassName != null) {
                try {
                    measurementClassSet.add(ClassUtils.forName(beanClassName, classLoader));
                } catch (ClassNotFoundException | LinkageError ignored) {
                }
            }
        }
    }

}
