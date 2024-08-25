package io.opengemini.client.spring.data.sample;

import io.opengemini.client.spring.data.annotation.MeasurementScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MeasurementScan("io.opengemini.client.spring.data.sample.measurement")
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

}
