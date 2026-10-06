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
package io.github.patch4j.lang

import io.github.patch4j.model.FieldRef
import io.github.patch4j.model.MethodRef
import io.github.patch4j.model.Replacement
import io.github.patch4j.model.TypeRef

internal sealed interface Declaration {
    val modifications: List<Modification>

    data object Empty : Declaration {
        override val modifications: List<Modification> = emptyList()
    }

    data class PatchClass(
        val typeRef: TypeRef.Object,
        val declarations: List<Declaration>,
        override val modifications: List<Modification>,
        val replacements: List<Replacement>,
    ) : Declaration

    data class PatchMethod(
        val methodRef: MethodRef,
        override val modifications: List<Modification>,
        val replacements: List<Replacement>,
    ) : Declaration

    data class PatchField(
        val fieldRef: FieldRef,
        override val modifications: List<Modification>,
    ) : Declaration

    sealed interface Make : Declaration {
        data class Class(
            val typeRef: TypeRef.Object,
            override val modifications: List<Modification>,
        ) : Make

        data class Method(
            val methodRef: MethodRef,
            override val modifications: List<Modification>,
        ) : Make

        data class Field(
            val fieldRef: FieldRef,
            override val modifications: List<Modification>,
        ) : Make

        data object Empty : Make {
            override val modifications: List<Modification> = emptyList()
        }
    }
}
