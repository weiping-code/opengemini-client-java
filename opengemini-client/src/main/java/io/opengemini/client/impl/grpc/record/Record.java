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
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Record represents a collection of fields and their values.
 */
public class Record {

    public static final String TIME_FIELD = "time";

    private List<Field> schema = new ArrayList<>();
    private List<ColVal> colVals = new ArrayList<>();

    public Record() {
    }

    public void reset() {
        schema.clear();
        colVals.clear();
    }

    public void reserveColVal(int size) {
        int colLen = colVals.size();
        int colCap = colVals.size();
        int remain = colCap - colLen;
        if (size - remain > 0) {
            for (int i = 0; i < size - remain; i++) {
                colVals.add(new ColVal());
            }
        }
        for (int i = colLen; i < colLen + size; i++) {
            if (i < colVals.size()) {
                colVals.get(i).init();
            }
        }
    }

    public void initColVal(int start, int end) {
        for (int i = start; i < end; i++) {
            if (i < colVals.size()) {
                colVals.get(i).init();
            }
        }
    }

    public void addColumn(Field field, ColVal colVal) {
        schema.add(field);
        colVals.add(colVal);
    }

    public Field getColumn(int index) {
        if (index >= 0 && index < schema.size()) {
            return schema.get(index);
        }
        return null;
    }

    public ColVal getColVal(int index) {
        if (index >= 0 && index < colVals.size()) {
            return colVals.get(index);
        }
        return null;
    }

    public int getRowNums() {
        if (colVals.isEmpty()) {
            return 0;
        }
        return colVals.get(colVals.size() - 1).len();
    }

    public long[] times() {
        if (colVals.isEmpty()) {
            return null;
        }
        ColVal timeCol = colVals.get(colVals.size() - 1);
        List<Long> times = timeCol.integerValues();
        long[] result = new long[times.size()];
        for (int i = 0; i < times.size(); i++) {
            result[i] = times.get(i);
        }
        return result;
    }

    public void appendTime(long time) {
        if (!colVals.isEmpty()) {
            colVals.get(colVals.size() - 1).appendInteger(time);
        }
    }

    public int len() {
        return schema.size();
    }

    public List<Field> getSchema() {
        return schema;
    }

    public List<ColVal> getColVals() {
        return colVals;
    }

    public void sort() {
        for (int i = 0; i < schema.size() - 1; i++) {
            for (int j = i + 1; j < schema.size(); j++) {
                Field fi = schema.get(i);
                Field fj = schema.get(j);

                if (TIME_FIELD.equals(fi.getName())) {
                    continue;
                }
                if (TIME_FIELD.equals(fj.getName())) {
                    swap(i, j);
                    continue;
                }
                if (fi.getName().compareTo(fj.getName()) > 0) {
                    swap(i, j);
                }
            }
        }
    }

    private void swap(int i, int j) {
        Field tmpField = schema.get(i);
        schema.set(i, schema.get(j));
        schema.set(j, tmpField);

        ColVal tmpColVal = colVals.get(i);
        colVals.set(i, colVals.get(j));
        colVals.set(j, tmpColVal);
    }

    public byte[] marshal(byte[] buf) {
        buf = BytesBuilder.appendUint32(buf, schema.size());
        for (Field field : schema) {
            buf = field.marshal(buf);
        }

        buf = BytesBuilder.appendUint32(buf, colVals.size());
        for (ColVal colVal : colVals) {
            buf = colVal.marshal(buf);
        }

        return buf;
    }

    public static Record checkRecord(Record rec) throws Exception {
        int colN = rec.schema.size();
        if (colN <= 1 || !TIME_FIELD.equals(rec.schema.get(colN - 1).getName())) {
            throw new Exception("invalid schema: " + rec.schema);
        }

        ColVal lastCol = rec.colVals.get(colN - 1);
        if (lastCol.getNilCount() != 0) {
            throw new Exception("invalid colvals");
        }

        for (int i = 1; i < colN; i++) {
            if (rec.schema.get(i).getName().equals(rec.schema.get(i - 1).getName())) {
                throw new Exception("same schema: " + rec.schema.get(i).getName());
            }
        }

        boolean isOrderSchema = true;
        for (int i = 0; i < colN - 1; i++) {
            Field f = rec.schema.get(i);
            ColVal col1 = rec.colVals.get(i);
            ColVal col2 = rec.colVals.get(i + 1);

            if (col1.len() != col2.len()) {
                throw new Exception("invalid colvals length");
            }

            if (isOrderSchema && i > 0 && rec.schema.get(i - 1).getName().compareTo(rec.schema.get(i).getName()) >= 0) {
                isOrderSchema = false;
            }

            if (!FieldType.isStringType(f.getType())) {
                int typeSize = FieldType.getTypeSize(f.getType());
                int expLen = typeSize * (col1.len() - col1.getNilCount());
                if (expLen != getValLength(col1, f.getType())) {
                    throw new Exception("invalid colvals val length");
                }
            }
        }

        if (!isOrderSchema) {
            rec.sort();
        }

        return rec;
    }

    private static int getValLength(ColVal colVal, int type) {
        switch (type) {
            case FieldType.INT:
            case FieldType.UINT:
                return colVal.integerValues().size() * 8;
            case FieldType.FLOAT:
                return colVal.floatValues().size() * 8;
            case FieldType.BOOLEAN:
                return colVal.booleanValues().size() * 1;
            default:
                return 0;
        }
    }
}