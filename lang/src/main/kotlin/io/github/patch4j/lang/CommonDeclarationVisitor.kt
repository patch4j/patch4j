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
import io.github.patch4j.model.Replacement

internal class CommonDeclarationVisitor(
    private val nameResolver: QualifiedNameResolver,
    private val fieldNameVisitor: FieldNameVisitor = FieldNameVisitor(nameResolver),
    private val methodNameVisitor: MethodNameVisitor = MethodNameVisitor(nameResolver),
) : Patch4JBaseVisitor<Declaration>() {
    override fun defaultResult() = Declaration.Empty

    override fun visitPatchClassDecl(ctx: Patch4JParser.PatchClassDeclContext): Declaration {
        val classBlockCtx = ctx.patchClassBlock()
        val typeRef = ctx.qualifiedName().typeRef
        val resolved = nameResolver.resolveClassTypeRef(typeRef)
        val nestedNameResolver = nameResolver.nestedNamespaceOf(resolved)
        val classLevelDeclarationVisitor = ClassLevelDeclarationVisitor(nestedNameResolver)
        val declarations = mutableListOf<Declaration>()
        val replacements = mutableListOf<Replacement>()

        classBlockCtx.patchClassLevelStatement().forEach { statement ->
            if (statement is Patch4JParser.ClassLevelReplaceConstantDeclarationContext) {
                replacements += requireNotNull(ReplacementStatementVisitor.visit(statement))
            } else {
                declarations += requireNotNull(classLevelDeclarationVisitor.visit(statement))
            }
        }

        return Declaration.PatchClass(
            typeRef = resolved,
            declarations = declarations,
            modifications = classBlockCtx.classModifier().mapNotNull(ModifierVisitor::visit),
            replacements = replacements,
        )
    }

    override fun visitPatchMethodDecl(ctx: Patch4JParser.PatchMethodDeclContext): Declaration {
        val blockCtx = ctx.patchMethodBlock()
        val replacements = mutableListOf<Replacement>()

        return Declaration.PatchMethod(
            methodRef = methodNameVisitor.visitMethodName(ctx.methodName()),
            replacements = replacements,
            modifications = blockCtx.methodModifier().mapNotNull(ModifierVisitor::visit),
        )
    }

    override fun visitPatchFieldDecl(ctx: Patch4JParser.PatchFieldDeclContext): Declaration =
        Declaration.PatchField(
            fieldRef = fieldNameVisitor.visitFieldName(ctx.fieldName()),
            modifications = ctx.patchFieldBlock().fieldModifier().mapNotNull(ModifierVisitor::visit),
        )
}
