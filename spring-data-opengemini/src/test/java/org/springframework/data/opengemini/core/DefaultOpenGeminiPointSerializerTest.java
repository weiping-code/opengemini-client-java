package org.springframework.data.opengemini.core;

import io.opengemini.client.api.Point;
import org.junit.jupiter.api.Test;
import org.springframework.data.opengemini.measurements.Weather;

import static org.junit.jupiter.api.Assertions.*;

class DefaultOpenGeminiPointSerializerTest {

    @Test
    void serialize() {
        DefaultOpenGeminiPointSerializer<Weather> serializer = new DefaultOpenGeminiPointSerializer<>(Weather.class);

        Weather weather = new Weather();
        weather.setLocation("test");
        weather.setTemperature(10.0D);
        weather.setTime(System.currentTimeMillis());

        Point point = serializer.serialize(weather);
        assertNotNull(point);
    }
}
