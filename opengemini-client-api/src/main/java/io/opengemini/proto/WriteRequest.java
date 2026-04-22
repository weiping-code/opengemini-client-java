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

import io.opengemini.client.api.grpc.CompressMethod;
import io.opengemini.client.api.grpc.ResponseCode;
import io.opengemini.client.api.grpc.ServerStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Manual proto implementation for WriteRequest.
 */
public class WriteRequest {

    private int version;
    private String database;
    private String retentionPolicy;
    private String username;
    private String password;
    private List<Record> records = new ArrayList<>();

    public WriteRequest() {
    }

    public int getVersion() {
        return version;
    }

    public WriteRequest setVersion(int version) {
        this.version = version;
        return this;
    }

    public String getDatabase() {
        return database;
    }

    public WriteRequest setDatabase(String database) {
        this.database = database;
        return this;
    }

    public String getRetentionPolicy() {
        return retentionPolicy;
    }

    public WriteRequest setRetentionPolicy(String retentionPolicy) {
        this.retentionPolicy = retentionPolicy;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public WriteRequest setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public WriteRequest setPassword(String password) {
        this.password = password;
        return this;
    }

    public List<Record> getRecordsList() {
        return records;
    }

    public WriteRequest addRecords(Record record) {
        this.records.add(record);
        return this;
    }

    public byte[] toByteArray() {
        return toByteArrayInternal(new byte[0]);
    }

    public byte[] toByteArrayInternal(byte[] buf) {
        buf = writeUint32(buf, version);
        buf = writeString(buf, database);
        buf = writeString(buf, retentionPolicy);
        buf = writeString(buf, username);
        buf = writeString(buf, password);
        buf = writeUint32(buf, records.size());
        for (Record record : records) {
            buf = record.toByteArray(buf);
        }
        return buf;
    }

    private byte[] writeUint32(byte[] b, int value) {
        byte[] result = new byte[b.length + 4];
        System.arraycopy(b, 0, result, 0, b.length);
        result[b.length] = (byte) (value >> 24);
        result[b.length + 1] = (byte) (value >> 16);
        result[b.length + 2] = (byte) (value >> 8);
        result[b.length + 3] = (byte) value;
        return result;
    }

    private byte[] writeString(byte[] b, String s) {
        if (s == null) {
            return writeUint16(b, 0);
        }
        byte[] strBytes = s.getBytes();
        b = writeUint16(b, strBytes.length);
        byte[] result = new byte[b.length + strBytes.length];
        System.arraycopy(b, 0, result, 0, b.length);
        System.arraycopy(strBytes, 0, result, b.length, strBytes.length);
        return result;
    }

    private byte[] writeUint16(byte[] b, int value) {
        byte[] result = new byte[b.length + 2];
        System.arraycopy(b, 0, result, 0, b.length);
        result[b.length] = (byte) (value >> 8);
        result[b.length + 1] = (byte) value;
        return result;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private final WriteRequest request = new WriteRequest();

        public Builder setVersion(int version) {
            request.version = version;
            return this;
        }

        public Builder setDatabase(String database) {
            request.database = database;
            return this;
        }

        public Builder setRetentionPolicy(String retentionPolicy) {
            request.retentionPolicy = retentionPolicy;
            return this;
        }

        public Builder setUsername(String username) {
            request.username = username;
            return this;
        }

        public Builder setPassword(String password) {
            request.password = password;
            return this;
        }

        public Builder addRecords(Record record) {
            request.records.add(record);
            return this;
        }

        public WriteRequest build() {
            return request;
        }
    }
}