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

import io.github.patch4j.model.TypeRef

internal class QualifiedNameResolver(
    private val namespace: TypeRef.Object? = null,
    private val typeRefByName: MutableMap<String, TypeRef.Object> = hashMapOf(),
) {
    operator fun plusAssign(typeRef: TypeRef.Object) = add(typeRef)

    fun add(typeRef: TypeRef.Object) {
        typeRefByName[typeRef.name] = typeRef
    }

    fun resolveTypeRef(typeRef: TypeRef.Object): TypeRef.Object {
        if (typeRef.name != typeRef.qualifiedName) {
            return typeRef
        }

        return typeRefByName[typeRef.name] ?: typeRef
    }

    fun resolveClassTypeRef(typeRef: TypeRef.Object): TypeRef.Object =
        when {
            typeRef.name != typeRef.qualifiedName -> {
                typeRef
            }

            namespace == null -> {
                resolveTypeRef(typeRef)
            }

            else -> {
                typeRef.copy(
                    qualifiedName =
                        buildString {
                            append(namespace.qualifiedName)
                            append('$')
                            append(typeRef.name)
                        },
                )
            }
        }

    fun resolveOwner(owner: TypeRef.Object?): TypeRef.Object {
        if (owner != null) {
            return resolveTypeRef(owner)
        }
        return requireNotNull(namespace)
    }

    fun nestedNamespaceOf(owner: TypeRef.Object): QualifiedNameResolver =
        QualifiedNameResolver(
            namespace = owner,
            typeRefByName = typeRefByName,
        )
}
