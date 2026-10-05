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

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TypeRefDescriptorTest {
    @Test
    fun `primitive Integer descriptor is I`() {
        assertEquals("I", TypeRef.Primitive.Integer.descriptor)
    }

    @Test
    fun `primitive Long descriptor is J`() {
        assertEquals("J", TypeRef.Primitive.Long.descriptor)
    }

    @Test
    fun `primitive Float descriptor is F`() {
        assertEquals("F", TypeRef.Primitive.Float.descriptor)
    }

    @Test
    fun `primitive Double descriptor is D`() {
        assertEquals("D", TypeRef.Primitive.Double.descriptor)
    }

    @Test
    fun `primitive Boolean descriptor is Z`() {
        assertEquals("Z", TypeRef.Primitive.Boolean.descriptor)
    }

    @Test
    fun `primitive Short descriptor is S`() {
        assertEquals("S", TypeRef.Primitive.Short.descriptor)
    }

    @Test
    fun `primitive Byte descriptor is B`() {
        assertEquals("B", TypeRef.Primitive.Byte.descriptor)
    }

    @Test
    fun `primitive Char descriptor is C`() {
        assertEquals("C", TypeRef.Primitive.Char.descriptor)
    }

    @Test
    fun `primitive Void descriptor is V`() {
        assertEquals("V", TypeRef.Primitive.Void.descriptor)
    }

    @Test
    fun `object descriptor wraps qualified name with L and semicolon`() {
        val type = TypeRef.Object("String", "java/lang/String")
        assertEquals("Ljava/lang/String;", type.descriptor)
    }

    @Test
    fun `inner class object descriptor uses dollar sign`() {
        val type = TypeRef.Object("Inner", $$"com/example/App$Inner")
        assertEquals($$"Lcom/example/App$Inner;", type.descriptor)
    }

    @Test
    fun `single-dimensional int array descriptor is bracket-I`() {
        val type = TypeRef.Array(TypeRef.Primitive.Integer)
        assertEquals("[I", type.descriptor)
    }

    @Test
    fun `two-dimensional int array descriptor is double-bracket-I`() {
        val type = TypeRef.Array(TypeRef.Array(TypeRef.Primitive.Integer))
        assertEquals("[[I", type.descriptor)
    }

    @Test
    fun `three-dimensional double array descriptor`() {
        val type = TypeRef.Array(TypeRef.Array(TypeRef.Array(TypeRef.Primitive.Double)))
        assertEquals("[[[D", type.descriptor)
    }

    @Test
    fun `object array descriptor has L-prefix inside brackets`() {
        val type = TypeRef.Array(TypeRef.Object("String", "java/lang/String"))
        assertEquals("[Ljava/lang/String;", type.descriptor)
    }

    @Test
    fun `two-dimensional object array descriptor`() {
        val type = TypeRef.Array(TypeRef.Array(TypeRef.Object("List", "java/util/List")))
        assertEquals("[[Ljava/util/List;", type.descriptor)
    }
}
