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

import java.nio.charset.StandardCharsets;

/**
 * Manual proto implementation for Record.
 */
public class Record {

    private String measurement;
    private long minTime;
    private long maxTime;
    private CompressMethod compressMethod;
    private byte[] block;

    public Record() {
    }

    public String getMeasurement() {
        return measurement;
    }

    public Record setMeasurement(String measurement) {
        this.measurement = measurement;
        return this;
    }

    public long getMinTime() {
        return minTime;
    }

    public Record setMinTime(long minTime) {
        this.minTime = minTime;
        return this;
    }

    public long getMaxTime() {
        return maxTime;
    }

    public Record setMaxTime(long maxTime) {
        this.maxTime = maxTime;
        return this;
    }

    public CompressMethod getCompressMethod() {
        return compressMethod;
    }

    public Record setCompressMethod(CompressMethod compressMethod) {
        this.compressMethod = compressMethod;
        return this;
    }

    public byte[] getBlock() {
        return block;
    }

    public Record setBlock(byte[] block) {
        this.block = block;
        return this;
    }

    public byte[] toByteArray(byte[] buf) {
        buf = writeString(buf, measurement);
        buf = writeInt64(buf, minTime);
        buf = writeInt64(buf, maxTime);
        buf = writeUint32(buf, compressMethod != null ? compressMethod.getValue() : 0);
        buf = writeBytes(buf, block);
        return buf;
    }

    private byte[] writeString(byte[] b, String s) {
        if (s == null) {
            return writeUint16(b, 0);
        }
        byte[] strBytes = s.getBytes(StandardCharsets.UTF_8);
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

    private byte[] writeUint32(byte[] b, int value) {
        byte[] result = new byte[b.length + 4];
        System.arraycopy(b, 0, result, 0, b.length);
        result[b.length] = (byte) (value >> 24);
        result[b.length + 1] = (byte) (value >> 16);
        result[b.length + 2] = (byte) (value >> 8);
        result[b.length + 3] = (byte) value;
        return result;
    }

    private byte[] writeInt64(byte[] b, long value) {
        byte[] result = new byte[b.length + 8];
        System.arraycopy(b, 0, result, 0, b.length);
        result[b.length] = (byte) (value >> 56);
        result[b.length + 1] = (byte) (value >> 48);
        result[b.length + 2] = (byte) (value >> 40);
        result[b.length + 3] = (byte) (value >> 32);
        result[b.length + 4] = (byte) (value >> 24);
        result[b.length + 5] = (byte) (value >> 16);
        result[b.length + 6] = (byte) (value >> 8);
        result[b.length + 7] = (byte) value;
        return result;
    }

    private byte[] writeBytes(byte[] b, byte[] data) {
        if (data == null || data.length == 0) {
            return writeUint32(b, 0);
        }
        b = writeUint32(b, data.length);
        byte[] result = new byte[b.length + data.length];
        System.arraycopy(b, 0, result, 0, b.length);
        System.arraycopy(data, 0, result, b.length, data.length);
        return result;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private final Record record = new Record();

        public Builder setMeasurement(String measurement) {
            record.measurement = measurement;
            return this;
        }

        public Builder setMinTime(long minTime) {
            record.minTime = minTime;
            return this;
        }

        public Builder setMaxTime(long maxTime) {
            record.maxTime = maxTime;
            return this;
        }

        public Builder setCompressMethod(CompressMethod compressMethod) {
            record.compressMethod = compressMethod;
            return this;
        }

        public Builder setBlock(byte[] block) {
            record.block = block;
            return this;
        }

        public Record build() {
            return record;
        }
    }
}
