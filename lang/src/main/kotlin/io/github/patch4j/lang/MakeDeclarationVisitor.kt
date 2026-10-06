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

internal class MakeDeclarationVisitor(
    private val nameResolver: QualifiedNameResolver,
    private val methodNameVisitor: MethodNameVisitor = MethodNameVisitor(nameResolver),
    private val fieldNameVisitor: FieldNameVisitor = FieldNameVisitor(nameResolver),
) : Patch4JBaseVisitor<Declaration.Make>() {
    override fun defaultResult() = Declaration.Make.Empty

    override fun visitMakeClassDeclaration(ctx: Patch4JParser.MakeClassDeclarationContext): Declaration.Make =
        Declaration.Make.Class(
            typeRef = nameResolver.resolveTypeRef(ctx.qualifiedName().typeRef),
            modifications = ctx.modifierShortcut().mapNotNull(ModifierShortcutVisitor::visit),
        )

    override fun visitMakeFieldDeclaration(ctx: Patch4JParser.MakeFieldDeclarationContext): Declaration.Make =
        Declaration.Make.Field(
            fieldRef = fieldNameVisitor.visitFieldName(ctx.fieldName()),
            modifications = ctx.modifierShortcut().mapNotNull(ModifierShortcutVisitor::visit),
        )

    override fun visitMakeMethodDeclaration(ctx: Patch4JParser.MakeMethodDeclarationContext): Declaration.Make =
        Declaration.Make.Method(
            methodRef = methodNameVisitor.visitMethodName(ctx.methodName()),
            modifications = ctx.modifierShortcut().mapNotNull(ModifierShortcutVisitor::visit),
        )
}
