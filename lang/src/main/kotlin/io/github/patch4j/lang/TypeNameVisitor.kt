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
import io.github.patch4j.model.TypeRef

internal class TypeNameVisitor(
    private val nameResolver: QualifiedNameResolver,
) : Patch4JBaseVisitor<TypeRef?>() {
    override fun defaultResult() = null

    override fun visitTypeName(ctx: Patch4JParser.TypeNameContext): TypeRef {
        val arrayDimension = ctx.LBRACKET().size
        val qualifiedName = ctx.qualifiedName()
        val primitiveType = ctx.primitiveType()

        val typeRef =
            when {
                qualifiedName != null -> visitQualifiedName(qualifiedName)
                primitiveType != null -> visitPrimitiveType(primitiveType)
                else -> error("Unknown type: ${ctx.text}")
            }

        var currentType = typeRef
        repeat(arrayDimension) {
            currentType = TypeRef.Array(currentType)
        }

        return currentType
    }

    override fun visitPrimitiveType(ctx: Patch4JParser.PrimitiveTypeContext): TypeRef =
        when {
            ctx.BOOLEAN_T() != null -> TypeRef.Primitive.Boolean
            ctx.BYTE() != null -> TypeRef.Primitive.Byte
            ctx.CHAR() != null -> TypeRef.Primitive.Char
            ctx.DOUBLE() != null -> TypeRef.Primitive.Double
            ctx.LONG() != null -> TypeRef.Primitive.Long
            ctx.SHORT() != null -> TypeRef.Primitive.Short
            ctx.VOID() != null -> TypeRef.Primitive.Void
            ctx.FLOAT_T() != null -> TypeRef.Primitive.Float
            ctx.INT_T() != null -> TypeRef.Primitive.Integer
            else -> error("Unknown primitive type: ${ctx.text}")
        }

    override fun visitQualifiedName(ctx: Patch4JParser.QualifiedNameContext): TypeRef = nameResolver.resolveTypeRef(ctx.typeRef)
}
