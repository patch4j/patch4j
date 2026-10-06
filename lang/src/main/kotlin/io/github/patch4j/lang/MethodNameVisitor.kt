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

import io.github.patch4j.lang.generated.Patch4JBaseVisitor
import io.github.patch4j.lang.generated.Patch4JParser
import io.github.patch4j.model.FieldRef
import io.github.patch4j.model.MethodRef

internal class MethodNameVisitor(
    private val nameResolver: QualifiedNameResolver,
    private val typeNameVisitor: TypeNameVisitor = TypeNameVisitor(nameResolver),
) : Patch4JBaseVisitor<MethodRef?>() {
    override fun defaultResult() = null

    override fun visitMethodName(ctx: Patch4JParser.MethodNameContext): MethodRef {
        val (owner, methodName) = ctx.qualifiedName().ownerToNameRef
        return MethodRef(
            owner = nameResolver.resolveOwner(owner),
            name = methodName,
            parameters =
                ctx
                    .parameterList()
                    ?.parameter()
                    ?.let { params ->
                        params.mapNotNull { param ->
                            typeNameVisitor.visit(param.typeName())
                        }
                    }.orEmpty(),
            returnTypeRef = ctx.returnType?.let { typeNameVisitor.visit(it) },
            isStatic = ctx.STATIC_METHOD() != null && ctx.METHOD() == null,
        )
    }
}
