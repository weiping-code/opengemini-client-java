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

package io.opengemini.client.impl.grpc.record;

import java.util.ArrayList;
import java.util.List;

/**
 * ColVal represents column values in a record.
 */
public class ColVal {

    private List<String> stringVals = new ArrayList<>();
    private List<Long> integerVals = new ArrayList<>();
    private List<Double> floatVals = new ArrayList<>();
    private List<Boolean> booleanVals = new ArrayList<>();
    private int nilCount = 0;
    private int len = 0;

    public ColVal() {
    }

    public void init() {
        stringVals.clear();
        integerVals.clear();
        floatVals.clear();
        booleanVals.clear();
        nilCount = 0;
        len = 0;
    }

    public void appendString(String val) {
        stringVals.add(val);
        len++;
    }

    public void appendStringNull() {
        stringVals.add(null);
        nilCount++;
        len++;
    }

    public void appendStringNulls(int count) {
        for (int i = 0; i < count; i++) {
            stringVals.add(null);
        }
        nilCount += count;
        len += count;
    }

    public void appendInteger(long val) {
        integerVals.add(val);
        len++;
    }

    public void appendIntegerNull() {
        integerVals.add(null);
        nilCount++;
        len++;
    }

    public void appendIntegerNulls(int count) {
        for (int i = 0; i < count; i++) {
            integerVals.add(null);
        }
        nilCount += count;
        len += count;
    }

    public void appendFloat(double val) {
        floatVals.add(val);
        len++;
    }

    public void appendFloatNull() {
        floatVals.add(null);
        nilCount++;
        len++;
    }

    public void appendFloatNulls(int count) {
        for (int i = 0; i < count; i++) {
            floatVals.add(null);
        }
        nilCount += count;
        len += count;
    }

    public void appendBoolean(boolean val) {
        booleanVals.add(val);
        len++;
    }

    public void appendBooleanNull() {
        booleanVals.add(null);
        nilCount++;
        len++;
    }

    public void appendBooleanNulls(int count) {
        for (int i = 0; i < count; i++) {
            booleanVals.add(null);
        }
        nilCount += count;
        len += count;
    }

    public List<String> stringValues() {
        return stringVals;
    }

    public List<Long> integerValues() {
        return integerVals;
    }

    public List<Double> floatValues() {
        return floatVals;
    }

    public List<Boolean> booleanValues() {
        return booleanVals;
    }

    public int getNilCount() {
        return nilCount;
    }

    public int len() {
        return len;
    }

    public int size() {
        int size = 0;
        for (String val : stringVals) {
            if (val != null) {
                size += val.length() + 2;
            }
        }
        size += integerVals.size() * 8;
        size += floatVals.size() * 8;
        size += booleanVals.size() * 1;
        return size;
    }

    public byte[] marshal(byte[] buf) {
        int sz = size();
        buf = BytesBuilder.appendUint32(buf, sz);

        for (String val : stringVals) {
            buf = BytesBuilder.appendString(buf, val);
        }
        for (Long val : integerVals) {
            buf = BytesBuilder.appendInt64(buf, val);
        }
        for (Double val : floatVals) {
            buf = BytesBuilder.appendDouble(buf, val);
        }
        for (Boolean val : booleanVals) {
            buf = BytesBuilder.appendBoolean(buf, val);
        }

        return buf;
    }
}
