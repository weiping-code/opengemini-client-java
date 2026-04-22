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

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Field represents a field schema in a record.
 */
@Getter
@Setter
@NoArgsConstructor
public class Field {

    private int type;
    private String name;

    public Field(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public int getSize() {
        int size = 0;
        size += BytesBuilder.sizeOfString(name);
        size += BytesBuilder.sizeOfInt();
        return size;
    }

    @Override
    public String toString() {
        return name + FieldType.getTypeName(type);
    }

    public byte[] marshal(byte[] buf) {
        buf = BytesBuilder.appendString(buf, name);
        buf = BytesBuilder.appendInt(buf, type);
        return buf;
    }
}
