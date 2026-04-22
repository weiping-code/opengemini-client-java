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

import java.nio.charset.StandardCharsets;

/**
 * Manual proto implementation for PingRequest.
 */
public class PingRequest {

    private String clientId;

    public PingRequest() {
    }

    public String getClientId() {
        return clientId;
    }

    public PingRequest setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public byte[] toByteArray() {
        if (clientId == null || clientId.isEmpty()) {
            return new byte[0];
        }
        return clientId.getBytes(StandardCharsets.UTF_8);
    }

    public static PingRequest parseFrom(byte[] data) {
        PingRequest request = new PingRequest();
        if (data != null && data.length > 0) {
            request.setClientId(new String(data, StandardCharsets.UTF_8));
        }
        return request;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private final PingRequest request = new PingRequest();

        public Builder setClientId(String clientId) {
            request.clientId = clientId;
            return this;
        }

        public PingRequest build() {
            return request;
        }
    }
}
