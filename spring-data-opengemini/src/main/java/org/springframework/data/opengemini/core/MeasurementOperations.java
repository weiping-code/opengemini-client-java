package org.springframework.data.opengemini.core;

import io.opengemini.client.api.Query;

import java.util.List;

public interface MeasurementOperations<T> {

    void write(T t);

    void batchWrite(List<T> list);

    List<T> query(Query query);

    void write(String database, String retentionPolicy, String measurement, T t);

    void batchWrite(String database, String retentionPolicy, String measurement, List<T> list);

    List<T> query(String database, String retentionPolicy, String measurement, Query query);

}
