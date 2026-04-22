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

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * BytesBuilder is a utility class for building byte arrays with various encoding methods.
 * This is a Java port of the Go utils.go functions.
 */
public class BytesBuilder {

    private static final int SIZE_OF_INT = 8;
    private static final int SIZE_OF_UINT16 = 2;
    private static final int SIZE_OF_UINT32 = 4;

    private byte[] buffer;
    private int position;

    public BytesBuilder() {
        this.buffer = new byte[64];
        this.position = 0;
    }

    public BytesBuilder(byte[] initial) {
        this.buffer = initial;
        this.position = initial.length;
    }

    public byte[] toBytes() {
        return Arrays.copyOf(buffer, position);
    }

    public int length() {
        return position;
    }

    public BytesBuilder append(byte b) {
        ensureCapacity(1);
        buffer[position++] = b;
        return this;
    }

    public BytesBuilder append(byte[] b) {
        if (b == null) {
            return this;
        }
        ensureCapacity(b.length);
        System.arraycopy(b, 0, buffer, position, b.length);
        position += b.length;
        return this;
    }

    private void ensureCapacity(int needed) {
        if (position + needed > buffer.length) {
            int newSize = Math.max(buffer.length * 2, position + needed);
            buffer = Arrays.copyOf(buffer, newSize);
        }
    }

    public static byte[] appendUint16(byte[] b, int value) {
        byte[] result = new byte[b.length + SIZE_OF_UINT16];
        System.arraycopy(b, 0, result, 0, b.length);
        result[b.length] = (byte) (value >> 8);
        result[b.length + 1] = (byte) value;
        return result;
    }

    public BytesBuilder appendUint16(int value) {
        ensureCapacity(SIZE_OF_UINT16);
        buffer[position++] = (byte) (value >> 8);
        buffer[position++] = (byte) value;
        return this;
    }

    public static byte[] appendUint32(byte[] b, long value) {
        byte[] result = new byte[b.length + SIZE_OF_UINT32];
        System.arraycopy(b, 0, result, 0, b.length);
        result[b.length] = (byte) (value >> 24);
        result[b.length + 1] = (byte) (value >> 16);
        result[b.length + 2] = (byte) (value >> 8);
        result[b.length + 3] = (byte) value;
        return result;
    }

    public BytesBuilder appendUint32(long value) {
        ensureCapacity(SIZE_OF_UINT32);
        buffer[position++] = (byte) (value >> 24);
        buffer[position++] = (byte) (value >> 16);
        buffer[position++] = (byte) (value >> 8);
        buffer[position++] = (byte) value;
        return this;
    }

    public static byte[] appendInt64(byte[] b, long value) {
        long u = (value << 1) ^ (value >> 63);
        return appendUint64(b, u);
    }

    public static byte[] appendDouble(byte[] b, double value) {
        return appendInt64(b, Double.doubleToLongBits(value));
    }

    public static byte[] appendBoolean(byte[] b, boolean value) {
        byte[] result = new byte[b.length + 1];
        System.arraycopy(b, 0, result, 0, b.length);
        result[b.length] = value ? (byte) 1 : (byte) 0;
        return result;
    }

    public BytesBuilder appendInt64(long value) {
        long u = (value << 1) ^ (value >> 63);
        return appendUint64(u);
    }

    private static byte[] appendUint64(byte[] b, long value) {
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

    private BytesBuilder appendUint64(long value) {
        ensureCapacity(8);
        buffer[position++] = (byte) (value >> 56);
        buffer[position++] = (byte) (value >> 48);
        buffer[position++] = (byte) (value >> 40);
        buffer[position++] = (byte) (value >> 32);
        buffer[position++] = (byte) (value >> 24);
        buffer[position++] = (byte) (value >> 16);
        buffer[position++] = (byte) (value >> 8);
        buffer[position++] = (byte) value;
        return this;
    }

    public static byte[] appendInt(byte[] b, int value) {
        return appendInt64(b, value);
    }

    public BytesBuilder appendInt(int value) {
        return appendInt64(value);
    }

    public static byte[] appendString(byte[] b, String s) {
        if (s == null) {
            return appendUint16(b, 0);
        }
        byte[] strBytes = s.getBytes(StandardCharsets.UTF_8);
        b = appendUint16(b, strBytes.length);
        byte[] result = new byte[b.length + strBytes.length];
        System.arraycopy(b, 0, result, 0, b.length);
        System.arraycopy(strBytes, 0, result, b.length, strBytes.length);
        return result;
    }

    public BytesBuilder appendString(String s) {
        if (s == null) {
            appendUint16(0);
            return this;
        }
        byte[] strBytes = s.getBytes(StandardCharsets.UTF_8);
        appendUint16(strBytes.length);
        ensureCapacity(strBytes.length);
        System.arraycopy(strBytes, 0, buffer, position, strBytes.length);
        position += strBytes.length;
        return this;
    }

    public static byte[] appendBytes(byte[] b, byte[] buf) {
        if (buf == null || buf.length == 0) {
            return appendUint32(b, 0);
        }
        b = appendUint32(b, buf.length);
        byte[] result = new byte[b.length + buf.length];
        System.arraycopy(b, 0, result, 0, b.length);
        System.arraycopy(buf, 0, result, b.length, buf.length);
        return result;
    }

    public BytesBuilder appendBytes(byte[] buf) {
        if (buf == null || buf.length == 0) {
            appendUint32(0);
            return this;
        }
        appendUint32(buf.length);
        ensureCapacity(buf.length);
        System.arraycopy(buf, 0, buffer, position, buf.length);
        position += buf.length;
        return this;
    }

    public BytesBuilder appendDouble(double value) {
        return appendInt64(Double.doubleToLongBits(value));
    }

    public BytesBuilder appendBoolean(boolean value) {
        ensureCapacity(1);
        buffer[position++] = value ? (byte) 1 : (byte) 0;
        return this;
    }

    public static int sizeOfString(String s) {
        if (s == null) {
            return SIZE_OF_UINT16;
        }
        return SIZE_OF_UINT16 + s.getBytes(StandardCharsets.UTF_8).length;
    }

    public static int sizeOfInt() {
        return SIZE_OF_INT;
    }

    public static int sizeOfUint32() {
        return SIZE_OF_UINT32;
    }

    public static int sizeOfByteSlice(byte[] s) {
        if (s == null) {
            return SIZE_OF_UINT32;
        }
        return SIZE_OF_UINT32 + s.length;
    }
}
