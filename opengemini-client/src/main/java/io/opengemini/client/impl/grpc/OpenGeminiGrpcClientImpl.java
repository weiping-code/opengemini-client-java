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

package io.opengemini.client.impl.grpc;

import io.opengemini.client.api.Address;
import io.opengemini.client.api.OpenGeminiException;
import io.opengemini.client.api.grpc.CompressMethod;
import io.opengemini.client.api.grpc.GrpcConfig;
import io.opengemini.client.api.grpc.ResponseCode;
import io.opengemini.client.api.grpc.ServerStatus;
import io.opengemini.proto.PingRequest;
import io.opengemini.proto.PingResponse;
import io.opengemini.proto.WriteRequest;
import io.opengemini.proto.WriteResponse;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of OpenGeminiGrpcClient.
 */
public class OpenGeminiGrpcClientImpl implements OpenGeminiGrpcClient {

    private final GrpcConfig config;
    private final List<Address> addresses;
    private final Random random = new Random();

    public OpenGeminiGrpcClientImpl(GrpcConfig config) {
        this.config = config;
        this.addresses = config.getAddresses();
    }

    @Override
    public WriteResponse write(WriteRequest request) throws Exception {
        Address address = getAddress();
        String urlStr = buildUrl(address);

        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setConnectTimeout((int) config.getTimeout().toMillis());
            connection.setReadTimeout((int) config.getTimeout().toMillis());
            connection.setRequestProperty("Content-Type", "application/protobuf");

            byte[] data = request.toByteArray();
            connection.setRequestProperty("Content-Length", String.valueOf(data.length));

            connection.getOutputStream().write(data);
            connection.getOutputStream().flush();
            connection.getOutputStream().close();

            int responseCode = connection.getResponseCode();
            if (responseCode == 200 || responseCode == 201) {
                InputStream is = connection.getInputStream();
                byte[] responseData = readAllBytes(is);
                is.close();

                if (responseData.length > 0) {
                    WriteResponse response = WriteResponse.parseFrom(responseData);
                    if (response.getCode() != ResponseCode.Success) {
                        throw new OpenGeminiException("Write failed: " + response.getCode());
                    }
                    return response;
                }
                return WriteResponse.newBuilder().setCode(ResponseCode.Success).build();
            } else {
                throw new OpenGeminiException("HTTP error: " + responseCode);
            }
        } catch (OpenGeminiException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PingResponse ping(PingRequest request) {
        Address address = getAddress();
        String urlStr = buildPingUrl(address);

        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout((int) config.getTimeout().toMillis());
            connection.setReadTimeout((int) config.getTimeout().toMillis());
            connection.setRequestProperty("Content-Type", "application/protobuf");

            int responseCode = connection.getResponseCode();
            if (responseCode == 200 || responseCode == 201) {
                return PingResponse.newBuilder().setStatus(ServerStatus.Up).build();
            } else {
                return PingResponse.newBuilder().setStatus(ServerStatus.Down).build();
            }
        } catch (Exception e) {
            return PingResponse.newBuilder().setStatus(ServerStatus.Down).build();
        }
    }

    @Override
    public void close() {
    }

    private Address getAddress() {
        if (addresses == null || addresses.isEmpty()) {
            throw new IllegalStateException("No addresses configured");
        }
        int index = random.nextInt(addresses.size());
        return addresses.get(index);
    }

    private String buildUrl(Address address) {
        return "http://" + address.getHost() + ":" + address.getPort() + "/api/v1/write";
    }

    private String buildPingUrl(Address address) {
        return "http://" + address.getHost() + ":" + address.getPort() + "/api/v1/ping";
    }

    private byte[] readAllBytes(InputStream is) throws Exception {
        java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int len;
        while ((len = is.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        return bos.toByteArray();
    }

    private byte[] toByteArray(WriteRequest request) {
        io.opengemini.proto.WriteRequest.Builder builder = io.opengemini.proto.WriteRequest.newBuilder();
        builder.setVersion(request.getVersion());
        if (request.getDatabase() != null) {
            builder.setDatabase(request.getDatabase());
        }
        if (request.getRetentionPolicy() != null) {
            builder.setRetentionPolicy(request.getRetentionPolicy());
        }
        if (request.getUsername() != null) {
            builder.setUsername(request.getUsername());
        }
        if (request.getPassword() != null) {
            builder.setPassword(request.getPassword());
        }
        for (io.opengemini.proto.Record record : request.getRecordsList()) {
            builder.addRecords(record);
        }
        return builder.build().toByteArray();
    }
}