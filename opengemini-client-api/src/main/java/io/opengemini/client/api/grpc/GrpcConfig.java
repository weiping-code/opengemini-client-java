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

import io.opengemini.client.api.Address;
import io.opengemini.client.api.AuthConfig;
import io.opengemini.client.api.TlsConfig;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * GrpcConfig represents the configuration information for write service by gRPC.
 */
@Getter
@Setter
public class GrpcConfig {

    /**
     * Addresses Configure the service endpoints for the openGemini grpc write service.
     * This parameter is required.
     */
    private List<Address> addresses = new ArrayList<>();

    /**
     * AuthConfig configuration information for authentication.
     */
    private AuthConfig authConfig;

    /**
     * TlsConfig configuration information for tls.
     */
    private TlsConfig tlsConfig;

    /**
     * CompressMethod determines the compress method used for data transmission.
     */
    private CompressMethod compressMethod = CompressMethod.UNCOMPRESSED;

    /**
     * Timeout default 30s
     */
    private Duration timeout = Duration.ofSeconds(30);
}
