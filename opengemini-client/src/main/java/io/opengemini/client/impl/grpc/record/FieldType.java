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

/**
 * FieldType represents the type of a field in a record.
 */
public class FieldType {

    public static final int UNKNOWN = 0;
    public static final int INT = 1;
    public static final int UINT = 2;
    public static final int FLOAT = 3;
    public static final int STRING = 4;
    public static final int BOOLEAN = 5;
    public static final int TAG = 6;
    public static final int LAST = 7;

    public static final String[] FIELD_TYPE_NAME = {
            "Unknown",
            "Integer",
            "Unsigned",
            "Float",
            "String",
            "Boolean",
            "Tag",
            "Unknown"
    };

    public static String getTypeName(int type) {
        if (type >= 0 && type < FIELD_TYPE_NAME.length) {
            return FIELD_TYPE_NAME[type];
        }
        return FIELD_TYPE_NAME[UNKNOWN];
    }

    public static int getTypeSize(int type) {
        switch (type) {
            case INT:
            case UINT:
                return 8;
            case FLOAT:
                return 8;
            case BOOLEAN:
                return 1;
            case STRING:
            case TAG:
                return -1;
            default:
                return 0;
        }
    }

    public static boolean isStringType(int type) {
        return type == STRING || type == TAG;
    }

    public static boolean isIntegerType(int type) {
        return type == INT || type == UINT;
    }
}