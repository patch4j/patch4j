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
import io.github.patch4j.model.BooleanLiteral
import io.github.patch4j.model.IntLiteral
import io.github.patch4j.model.Literal
import io.github.patch4j.model.StringLiteral

internal object LiteralVisitor : Patch4JBaseVisitor<Literal?>() {
    override fun defaultResult() = null

    override fun visitBooleanLiteral(ctx: Patch4JParser.BooleanLiteralContext): Literal = BooleanLiteral(ctx.BOOLEAN().text.toBoolean())

    override fun visitIntLiteral(ctx: Patch4JParser.IntLiteralContext): Literal = IntLiteral(ctx.INT().text.toInt())

    override fun visitStringLiteral(ctx: Patch4JParser.StringLiteralContext): Literal = StringLiteral(ctx.STRING().textWithoutQuotes)
}
