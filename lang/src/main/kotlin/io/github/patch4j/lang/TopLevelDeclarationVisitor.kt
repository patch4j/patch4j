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
import io.github.patch4j.lang.generated.Patch4JParser.TopLevelPatchClassDeclarationContext
import io.github.patch4j.lang.generated.Patch4JParser.TopLevelPatchFieldDeclarationContext
import io.github.patch4j.lang.generated.Patch4JParser.TopLevelPatchMethodDeclarationContext

internal class TopLevelDeclarationVisitor(
    private val nameResolver: QualifiedNameResolver,
    private val commonDeclarationVisitor: CommonDeclarationVisitor = CommonDeclarationVisitor(nameResolver),
) : Patch4JBaseVisitor<Declaration>() {
    override fun defaultResult() = Declaration.Empty

    override fun visitTopLevelMakeDeclaration(ctx: Patch4JParser.TopLevelMakeDeclarationContext): Declaration =
        MakeDeclarationVisitor(nameResolver).visitTopLevelMakeDeclaration(ctx)

    override fun visitTopLevelPatchClassDeclaration(ctx: TopLevelPatchClassDeclarationContext): Declaration =
        commonDeclarationVisitor.visit(ctx.patchClassDecl())

    override fun visitTopLevelPatchFieldDeclaration(ctx: TopLevelPatchFieldDeclarationContext): Declaration =
        commonDeclarationVisitor.visit(ctx.patchFieldDecl())

    override fun visitTopLevelPatchMethodDeclaration(ctx: TopLevelPatchMethodDeclarationContext): Declaration =
        commonDeclarationVisitor.visit(ctx.patchMethodDecl())
}
