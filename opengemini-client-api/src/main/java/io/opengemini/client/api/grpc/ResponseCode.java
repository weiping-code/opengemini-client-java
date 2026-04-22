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

/**
 * ResponseCode represents the response code from gRPC write service.
 */
public enum ResponseCode {
    Success(0),
    Partial(1),
    Failed(2);

    private final int value;

    ResponseCode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ResponseCode forNumber(int value) {
        for (ResponseCode code : values()) {
            if (code.value == value) {
                return code;
            }
        }
        return Success;
    }
}