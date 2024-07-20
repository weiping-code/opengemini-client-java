package org.springframework.data.opengemini.core;

import io.opengemini.client.api.Point;
import io.opengemini.client.api.QueryResult;

import java.util.List;

public interface OpenGeminiPointSerializer<T> {

    Point serialize(T t);

    T deserialize(Point point);

    List<T> deserialize(QueryResult queryResult);
}
