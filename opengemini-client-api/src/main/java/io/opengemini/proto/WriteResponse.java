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

package io.opengemini.proto;

import io.opengemini.client.api.grpc.ResponseCode;

/**
 * Manual proto implementation for WriteResponse.
 */
public class WriteResponse {

    private ResponseCode code = ResponseCode.Success;

    public WriteResponse() {
    }

    public ResponseCode getCode() {
        return code;
    }

    public WriteResponse setCode(ResponseCode code) {
        this.code = code;
        return this;
    }

    public byte[] toByteArray() {
        return new byte[]{(byte) code.getValue()};
    }

    public static WriteResponse parseFrom(byte[] data) {
        WriteResponse response = new WriteResponse();
        if (data != null && data.length > 0) {
            response.setCode(ResponseCode.forNumber(data[0]));
        }
        return response;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private final WriteResponse response = new WriteResponse();

        public Builder setCode(ResponseCode code) {
            response.code = code;
            return this;
        }

        public WriteResponse build() {
            return response;
        }
    }
}