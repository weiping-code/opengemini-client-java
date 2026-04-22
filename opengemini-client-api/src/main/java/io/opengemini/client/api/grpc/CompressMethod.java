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

package io.opengemini.client.api.grpc;

public enum CompressMethod {
    UNCOMPRESSED(0),
    LZ4_FAST(1),
    ZSTD_FAST(2),
    SNAPPY(3);

    private final int value;

    CompressMethod(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static CompressMethod forNumber(int value) {
        for (CompressMethod method : values()) {
            if (method.value == value) {
                return method;
            }
        }
        return UNCOMPRESSED;
    }
}