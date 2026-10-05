/*
 * Copyright 2026 The patch4j Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.patch4j.model

sealed interface TypeRef {
    enum class Primitive : TypeRef {
        Integer,
        Long,
        Float,
        Double,
        Boolean,
        Short,
        Byte,
        Char,
        Void,
    }

    data class Array(
        val elementType: TypeRef,
    ) : TypeRef

    data class Object(
        val name: String,
        val qualifiedName: String,
    ) : TypeRef
}

val TypeRef.descriptor: String
    get() {
        val stringBuilder = StringBuilder()
        var current = this

        while (current is TypeRef.Array) {
            stringBuilder.append('[')
            current = current.elementType
        }

        val descriptor =
            when (current) {
                TypeRef.Primitive.Integer -> "I"
                TypeRef.Primitive.Long -> "J"
                TypeRef.Primitive.Float -> "F"
                TypeRef.Primitive.Double -> "D"
                TypeRef.Primitive.Boolean -> "Z"
                TypeRef.Primitive.Short -> "S"
                TypeRef.Primitive.Byte -> "B"
                TypeRef.Primitive.Char -> "C"
                TypeRef.Primitive.Void -> "V"
                is TypeRef.Object -> "L${current.qualifiedName};"
            }

        stringBuilder.append(descriptor)

        return stringBuilder.toString()
    }
