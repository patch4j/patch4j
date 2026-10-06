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
import io.github.patch4j.lang.generated.Patch4JParser.ClassLevelPatchConstructorDeclarationContext
import io.github.patch4j.lang.generated.Patch4JParser.ClassLevelPatchExtendsDeclarationContext
import io.github.patch4j.lang.generated.Patch4JParser.ClassLevelReplaceConstantDeclarationContext

internal class ClassLevelDeclarationVisitor(
    private val nameResolver: QualifiedNameResolver,
    private val makeDeclarationVisitor: MakeDeclarationVisitor = MakeDeclarationVisitor(nameResolver),
    private val commonDeclarationVisitor: CommonDeclarationVisitor = CommonDeclarationVisitor(nameResolver),
) : Patch4JBaseVisitor<Declaration?>() {
    override fun defaultResult() = null

    override fun visitClassLevelMakeDeclaration(ctx: Patch4JParser.ClassLevelMakeDeclarationContext) =
        makeDeclarationVisitor.visit(ctx.makeDecl())

    override fun visitClassLevelPatchFieldDeclaration(ctx: Patch4JParser.ClassLevelPatchFieldDeclarationContext) =
        commonDeclarationVisitor.visit(ctx.patchFieldDecl())

    override fun visitClassLevelPatchMethodDeclaration(ctx: Patch4JParser.ClassLevelPatchMethodDeclarationContext) =
        commonDeclarationVisitor.visit(ctx.patchMethodDecl())

    override fun visitClassLevelPatchClassDeclaration(ctx: Patch4JParser.ClassLevelPatchClassDeclarationContext) =
        commonDeclarationVisitor.visitPatchClassDecl(ctx.patchClassDecl())

    override fun visitClassLevelPatchClinitDeclaration(ctx: Patch4JParser.ClassLevelPatchClinitDeclarationContext) =
        super.visitClassLevelPatchClinitDeclaration(ctx)

    override fun visitClassLevelPatchConstructorDeclaration(ctx: ClassLevelPatchConstructorDeclarationContext) =
        super.visitClassLevelPatchConstructorDeclaration(ctx)

    override fun visitClassLevelPatchExtendsDeclaration(ctx: ClassLevelPatchExtendsDeclarationContext) =
        super.visitClassLevelPatchExtendsDeclaration(ctx)

    override fun visitClassLevelReplaceConstantDeclaration(ctx: ClassLevelReplaceConstantDeclarationContext) = error("Unreachable code")
}
