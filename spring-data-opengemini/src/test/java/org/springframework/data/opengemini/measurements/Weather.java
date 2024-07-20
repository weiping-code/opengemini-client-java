package org.springframework.data.opengemini.measurements;


import io.opengemini.client.api.Precision;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.opengemini.annotations.Column;
import org.springframework.data.opengemini.annotations.Database;
import org.springframework.data.opengemini.annotations.Measurement;
import org.springframework.data.opengemini.annotations.RetentionPolicy;
import org.springframework.data.opengemini.annotations.TimeColumn;

@Database(name = "testdb")
@RetentionPolicy(name = "testrp", duration = "365d", shardGroupDuration = "30d")
@Measurement(name = "testms")
@Getter
@Setter
public class Weather {

    @Column(name = "Location", tag = true)
    private String location;

    @Column(name = "Temperature")
    private Double temperature;

    @TimeColumn(precision = Precision.PRECISIONMILLISECOND)
    private Long time;

}
