package org.springframework.data.opengemini.core;

import io.opengemini.client.api.Query;

import java.util.Collections;
import java.util.List;

public class DefaultMeasurementOperations<T> implements MeasurementOperations<T> {

    @Override
    public void write(T t) {

    }

    @Override
    public void batchWrite(List<T> list) {

    }

    @Override
    public List<T> query(Query query) {
        return Collections.emptyList();
    }

    @Override
    public void write(String database, String retentionPolicy, String measurement, T t) {

    }

    @Override
    public void batchWrite(String database, String retentionPolicy, String measurement, List<T> list) {

    }

    @Override
    public List<T> query(String database, String retentionPolicy, String measurement, Query query) {
        return Collections.emptyList();
    }
}
