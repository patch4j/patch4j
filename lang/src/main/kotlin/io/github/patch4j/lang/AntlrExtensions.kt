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

import io.github.patch4j.lang.generated.Patch4JParser
import io.github.patch4j.lang.generated.Patch4JParser.QualifiedNameContext
import io.github.patch4j.model.AccessType
import io.github.patch4j.model.TypeRef
import org.antlr.v4.kotlinruntime.tree.TerminalNode

internal val TerminalNode.textWithoutQuotes: String
    get() = text.removeSurrounding("\"")

private fun typeRefOf(ids: List<TerminalNode>): TypeRef.Object =
    TypeRef.Object(
        name = ids.last().text,
        qualifiedName = ids.joinToString("/") { it.text },
    )

internal val QualifiedNameContext.typeRef: TypeRef.Object
    get() = typeRefOf(ID())

internal val QualifiedNameContext.ownerToNameRef: Pair<TypeRef.Object?, String>
    get() =
        ID().let { ids ->
            val ownerPart = ids.dropLast(1).takeIf { it.isNotEmpty() }?.let { typeRefOf(it) }
            val namePart = ids.last()

            ownerPart to namePart.text
        }

internal val Patch4JParser.ModifierContext.accessType: AccessType
    get() =
        when {
            PRIVATE() != null -> AccessType.Private
            PUBLIC() != null -> AccessType.Public
            PROTECTED() != null -> AccessType.Protected
            else -> error("Unreachable condition")
        }
