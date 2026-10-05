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

sealed interface MethodPatch {
    val owner: TypeRef.Object
    val replacements: Set<Replacement>
    val parameters: List<TypeRef>

    data class Method(
        override val owner: TypeRef.Object,
        override val replacements: Set<Replacement>,
        override val parameters: List<TypeRef>,
        val isStatic: Boolean,
        val returnValue: TypeRef,
        val modifier: Modifier?,
        val final: Boolean?,
    ) : MethodPatch

    data class Constructor(
        override val owner: TypeRef.Object,
        override val replacements: Set<Replacement>,
        override val parameters: List<TypeRef>,
        val modifier: Modifier?,
    ) : MethodPatch

    data class ClassInit(
        override val owner: TypeRef.Object,
        override val replacements: Set<Replacement>,
    ) : MethodPatch {
        override val parameters: List<TypeRef> = emptyList()
    }
}
