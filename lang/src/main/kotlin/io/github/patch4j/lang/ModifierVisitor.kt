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

internal object ModifierVisitor : Patch4JBaseVisitor<Modification?>() {
    override fun defaultResult() = null

    override fun visitClassAccessModifier(ctx: Patch4JParser.ClassAccessModifierContext): Modification =
        Modification.Access(
            type = ctx.accessModifier().modifier().accessType,
        )

    override fun visitClassFinalModifier(ctx: Patch4JParser.ClassFinalModifierContext): Modification =
        Modification.SetFinal(
            final =
                ctx
                    .finalModifier()
                    .BOOLEAN()
                    .text
                    .toBoolean(),
        )

    override fun visitFieldAccessModifier(ctx: Patch4JParser.FieldAccessModifierContext): Modification =
        Modification.Access(
            type = ctx.accessModifier().modifier().accessType,
        )

    override fun visitFieldFinalModifier(ctx: Patch4JParser.FieldFinalModifierContext): Modification =
        Modification.SetFinal(
            final =
                ctx
                    .finalModifier()
                    .BOOLEAN()
                    .text
                    .toBoolean(),
        )

    override fun visitFieldValueModifier(ctx: Patch4JParser.FieldValueModifierContext): Modification {
        val literal = LiteralVisitor.visit(ctx.valueModifier().literal())
        return Modification.DefaultValue(
            value = requireNotNull(literal),
        )
    }

    override fun visitMethodAccessModifier(ctx: Patch4JParser.MethodAccessModifierContext): Modification =
        Modification.Access(
            type = ctx.accessModifier().modifier().accessType,
        )

    override fun visitMethodFinalModifier(ctx: Patch4JParser.MethodFinalModifierContext): Modification =
        Modification.SetFinal(
            final =
                ctx
                    .finalModifier()
                    .BOOLEAN()
                    .text
                    .toBoolean(),
        )
}
