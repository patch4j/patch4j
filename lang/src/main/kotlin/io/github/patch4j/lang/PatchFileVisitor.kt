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
import io.github.patch4j.lang.generated.Patch4JLexer
import io.github.patch4j.lang.generated.Patch4JParser
import io.github.patch4j.model.PatchFile
import org.antlr.v4.kotlinruntime.CharStreams
import org.antlr.v4.kotlinruntime.CommonTokenStream
import org.antlr.v4.kotlinruntime.tree.TerminalNode
import java.io.InputStream

internal class PatchFileVisitor : Patch4JBaseVisitor<Any>() {
    override fun defaultResult() = Unit

    private val TerminalNode.textWithoutQuotes: String
        get() = text.removeSurrounding("\"")

    override fun visitPatchFile(ctx: Patch4JParser.PatchFileContext): PatchFile {
        val target = ctx.targetDecl()?.STRING()?.textWithoutQuotes

        ctx.importDecl().forEach { importDecl ->
        }

        ctx.EOF()

        return PatchFile(
            target = target ?: PatchFile.DefaultTargets.JAVA,
            classPatches = listOf(),
            methodPatches = listOf(),
            fieldPatches = listOf(),
        )
    }
}

fun patchFileOf(inputStream: InputStream): PatchFile {
    val lexer = Patch4JLexer(CharStreams.fromStream(inputStream))
    val tokens = CommonTokenStream(lexer)
    val parser = Patch4JParser(tokens)
    val visitor = PatchFileVisitor()
    return visitor.visitPatchFile(parser.patchFile())
}
