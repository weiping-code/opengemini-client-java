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

import io.opengemini.client.api.grpc.ServerStatus;

/**
 * Manual proto implementation for PingResponse.
 */
public class PingResponse {

    private ServerStatus status = ServerStatus.Up;

    public PingResponse() {
    }

    public ServerStatus getStatus() {
        return status;
    }

    public PingResponse setStatus(ServerStatus status) {
        this.status = status;
        return this;
    }

    public byte[] toByteArray() {
        return new byte[]{(byte) status.getValue()};
    }

    public static PingResponse parseFrom(byte[] data) {
        PingResponse response = new PingResponse();
        if (data != null && data.length > 0) {
            response.setStatus(ServerStatus.forNumber(data[0]));
        }
        return response;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private final PingResponse response = new PingResponse();

        public Builder setStatus(ServerStatus status) {
            response.status = status;
            return this;
        }

        public PingResponse build() {
            return response;
        }
    }
}