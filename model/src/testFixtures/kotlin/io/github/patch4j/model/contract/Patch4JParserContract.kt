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
package io.github.patch4j.model.contract

import io.github.patch4j.model.AccessType
import io.github.patch4j.model.MethodPatch
import io.github.patch4j.model.PatchFile
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

abstract class Patch4JParserContract {
    abstract fun parse(text: String): PatchFile

    @Test
    fun `parse minimal file with target`() {
        val result =
            parse(
                """
                target "java"
                """.trimIndent(),
            )

        assertEquals("java", result.target)
        assertTrue(result.classPatches.isEmpty())
    }

    @Test
    fun `parse file with class patch and import`() {
        val result =
            parse(
                """
                target "java"
                
                import com.example.app.TargetClass
                
                patch class TargetClass {
                }
                """.trimIndent(),
            )

        val patch = result.classPatches.first()

        assertEquals("com/example/app/TargetClass", patch.type.qualifiedName)
        assertEquals("TargetClass", patch.type.name)
    }

    @Test
    fun `parse file with nested class patch and import`() {
        val result =
            parse(
                """
                target "java"
                
                import com.example.app.TargetClass
                
                patch class TargetClass {
                    patch class Nested {
                        access public
                        final false
                    }
                }
                """.trimIndent(),
            )

        val patch = result.classPatches.first { it.type.qualifiedName == $$"com/example/app/TargetClass$Nested" }

        assertTrue(patch.final != null && !patch.final)
        assertEquals(AccessType.Public, patch.accessType)
        assertEquals("Nested", patch.type.name)
    }

    @Test
    fun `parse file with method patch in a nested class`() {
        val result =
            parse(
                """
                target "java"
                
                import com.example.app.TargetClass
                
                patch class TargetClass {
                    patch class Nested {
                        access public
                        final false
                        
                        patch method exampleMethod(): void {
                            access public
                            final false
                        }
                    }
                }
                """.trimIndent(),
            )

        val patch = result.methodPatches.first() as MethodPatch.Method

        assertTrue(patch.final != null && !patch.final)
        assertEquals(AccessType.Public, patch.accessType)
        assertEquals($$"com/example/app/TargetClass$Nested.exampleMethod:()V", patch.methodRef.descriptor)
    }

    @Test
    fun `parse file with class patch`() {
        val result =
            parse(
                """
                target "java"
                
                patch class com.example.app.TargetClass {
                    access public
                    final false
                }
                """.trimIndent(),
            )

        val patch = result.classPatches.first()

        assertTrue(patch.final != null && !patch.final)
        assertEquals(AccessType.Public, patch.accessType)
        assertEquals("com/example/app/TargetClass", patch.type.qualifiedName)
        assertEquals("TargetClass", patch.type.name)
    }

    @Test
    fun `parse file with class patch shortcut`() {
        val result =
            parse(
                """
                target "java"
                
                make class com.example.app.TargetClass public
                """.trimIndent(),
            )

        val patch = result.classPatches.first()

        assertEquals(AccessType.Public, patch.accessType)
        assertEquals("com/example/app/TargetClass", patch.type.qualifiedName)
        assertEquals("TargetClass", patch.type.name)
    }

    @Test
    fun `parse file with method patch shortcut`() {
        val result =
            parse(
                """
                target "java"
                
                make method com.example.app.TargetClass.exampleMethod(String, int): int public
                """.trimIndent(),
            )

        val patch = result.methodPatches.first() as MethodPatch.Method

        assertEquals(AccessType.Public, patch.accessType)
        assertEquals("com/example/app/TargetClass", patch.owner.qualifiedName)
        assertEquals("exampleMethod", patch.methodRef.name)
        assertEquals("com/example/app/TargetClass.exampleMethod:(LString;I)I", patch.methodRef.descriptor)
    }

    @Test
    fun `parse file with nested method patch shortcut`() {
        val result =
            parse(
                """
                target "java"
                
                import com.example.app.TargetClass
                
                patch class TargetClass {
                    make method exampleMethod(String, int): int public
                }
                
                """.trimIndent(),
            )

        val patch = result.methodPatches.first() as MethodPatch.Method

        assertEquals(AccessType.Public, patch.accessType)
        assertEquals("com/example/app/TargetClass", patch.owner.qualifiedName)
        assertEquals("exampleMethod", patch.methodRef.name)
        assertEquals("com/example/app/TargetClass.exampleMethod:(LString;I)I", patch.methodRef.descriptor)
    }

    @Test
    fun `parse file with nested method patch`() {
        val result =
            parse(
                """
                target "java"
                
                import com.example.app.TargetClass
                
                patch class TargetClass {
                    patch method exampleMethod(String, int): int {
                        access public
                        final false
                    }
                }
                
                """.trimIndent(),
            )

        val patch = result.methodPatches.first() as MethodPatch.Method

        assertTrue(patch.final != null && !patch.final)
        assertEquals(AccessType.Public, patch.accessType)
        assertEquals("com/example/app/TargetClass", patch.owner.qualifiedName)
        assertEquals("exampleMethod", patch.methodRef.name)
        assertEquals("com/example/app/TargetClass.exampleMethod:(LString;I)I", patch.methodRef.descriptor)
    }

    @Test
    fun `parse file with method patch shortcut and import`() {
        val result =
            parse(
                """
                target "java"
                
                import com.example.app.TargetClass
                
                make method TargetClass.exampleMethod(String, int): int public
                """.trimIndent(),
            )

        val patch = result.methodPatches.first() as MethodPatch.Method

        assertEquals(AccessType.Public, patch.accessType)
        assertEquals("com/example/app/TargetClass", patch.owner.qualifiedName)
        assertEquals("exampleMethod", patch.methodRef.name)
        assertEquals("com/example/app/TargetClass.exampleMethod:(LString;I)I", patch.methodRef.descriptor)
    }
}
