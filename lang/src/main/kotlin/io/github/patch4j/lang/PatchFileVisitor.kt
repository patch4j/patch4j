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
import io.github.patch4j.model.AccessType
import io.github.patch4j.model.ClassPatch
import io.github.patch4j.model.FieldPatch
import io.github.patch4j.model.FieldRef
import io.github.patch4j.model.Literal
import io.github.patch4j.model.MethodPatch
import io.github.patch4j.model.PatchFile
import org.antlr.v4.kotlinruntime.CharStreams
import org.antlr.v4.kotlinruntime.CommonTokenStream
import java.io.InputStream
import java.util.LinkedList
import kotlin.collections.plusAssign

internal object PatchFileVisitor : Patch4JBaseVisitor<Any>() {
    override fun defaultResult() = Unit

    override fun visitPatchFile(ctx: Patch4JParser.PatchFileContext): PatchFile {
        val target = ctx.targetDecl()?.STRING()?.textWithoutQuotes

        val resolver = QualifiedNameResolver()

        ctx.importDecl().forEach { importDecl ->
            resolver += importDecl.qualifiedName().typeRef
        }

        val declarations = LinkedList<Declaration>()
        val topLevelDeclarationVisitor = TopLevelDeclarationVisitor(resolver)

        ctx.topLevelDecl().mapTo(
            destination = declarations,
            transform = topLevelDeclarationVisitor::visit,
        )

        val patchReducer = PatchReducer()

        while (declarations.isNotEmpty()) {
            when (val declaration = declarations.pop()) {
                is Declaration.PatchClass -> {
                    patchReducer.mergePatchClass(declaration)
                    declarations += declaration.declarations
                }

                is Declaration.Make.Class -> {
                    patchReducer.mergeMakeClass(declaration)
                }

                is Declaration.Make.Method -> {
                    patchReducer.mergeMakeMethod(declaration)
                }

                is Declaration.PatchMethod -> {
                    patchReducer.mergePatchMethod(declaration)
                }

                is Declaration.Make.Field -> {
                    patchReducer.mergeMakeField(declaration)
                }

                is Declaration.PatchField -> {
                    patchReducer.mergePatchField(declaration)
                }

                Declaration.Empty, Declaration.Make.Empty -> {
                    error("Unexpected empty declaration")
                }
            }
        }

        return with(patchReducer) {
            PatchFile(
                target = target ?: PatchFile.DefaultTargets.JAVA,
                classPatches = classPatchesByDescriptor.values.toList(),
                methodPatches = methodPatchesByDescriptor.values.toList(),
                fieldPatches = fieldPatchesByDescriptor.values.toList(),
            )
        }
    }
}

private class PatchReducer {
    val classPatchesByDescriptor = hashMapOf<String, ClassPatch>()
    val fieldPatchesByDescriptor = hashMapOf<String, FieldPatch>()
    val methodPatchesByDescriptor = hashMapOf<String, MethodPatch>()

    private data class ReducedModifications(
        val isFinal: Boolean?,
        val accessType: AccessType?,
        val defaultValue: Literal?,
    )

    private fun Declaration.reduceModifications(): ReducedModifications {
        var isFinal: Boolean? = null
        var accessType: AccessType? = null
        var defaultValue: Literal? = null

        for (modification in modifications) {
            when (modification) {
                is Modification.Access -> accessType = accessType ?: modification.type
                is Modification.SetFinal -> isFinal = isFinal ?: modification.final
                is Modification.DefaultValue -> defaultValue = defaultValue ?: modification.value
            }
        }

        return ReducedModifications(
            isFinal = isFinal,
            accessType = accessType,
            defaultValue = defaultValue,
        )
    }

    fun mergePatchClass(declaration: Declaration.PatchClass) {
        val (isFinal, accessType, defaultValue) = declaration.reduceModifications()

        require(defaultValue == null) {
            "Cannot set a default value for a class"
        }

        val descriptor = declaration.typeRef.descriptor

        classPatchesByDescriptor.computeIfAbsent(descriptor) {
            ClassPatch(
                type = declaration.typeRef,
                accessType = null,
                final = null,
                replacements = emptySet(),
            )
        }

        classPatchesByDescriptor.computeIfPresent(descriptor) { _, v ->
            v.copy(
                accessType = v.accessType ?: accessType,
                final = v.final ?: isFinal,
                replacements = v.replacements + declaration.replacements.toSet(),
            )
        }
    }

    fun mergeMakeClass(declaration: Declaration.Make.Class) {
        val (isFinal, accessType, defaultValue) = declaration.reduceModifications()

        require(defaultValue == null) {
            "Cannot set a default value for a class"
        }

        val descriptor = declaration.typeRef.descriptor

        classPatchesByDescriptor.computeIfAbsent(descriptor) {
            ClassPatch(
                type = declaration.typeRef,
                accessType = null,
                final = null,
                replacements = emptySet(),
            )
        }

        classPatchesByDescriptor.computeIfPresent(descriptor) { _, v ->
            v.copy(
                accessType = v.accessType ?: accessType,
                final = v.final ?: isFinal,
            )
        }
    }

    fun mergeMakeField(declaration: Declaration.Make.Field) {
        mergeFieldPatchByFieldRef(declaration.reduceModifications(), declaration.fieldRef)
    }

    fun mergePatchField(declaration: Declaration.PatchField) {
        mergeFieldPatchByFieldRef(declaration.reduceModifications(), declaration.fieldRef)
    }

    private fun mergeFieldPatchByFieldRef(
        modifications: ReducedModifications,
        fieldRef: FieldRef,
    ) {
        val (isFinal, accessType, defaultValue) = modifications
        val descriptor = fieldRef.descriptor

        fieldPatchesByDescriptor.computeIfAbsent(descriptor) {
            FieldPatch(
                fieldRef = fieldRef,
                accessType = null,
                final = null,
                value = null,
            )
        }

        fieldPatchesByDescriptor.computeIfPresent(descriptor) { _, v ->
            v.copy(
                accessType = v.accessType ?: accessType,
                final = v.final ?: isFinal,
                value = v.value ?: defaultValue,
            )
        }
    }

    fun mergeMakeMethod(declaration: Declaration.Make.Method) {
        val (isFinal, accessType, defaultValue) = declaration.reduceModifications()

        require(defaultValue == null) {
            "Cannot set a default value for a method"
        }

        val methodRef = declaration.methodRef
        val descriptor = methodRef.descriptor

        methodPatchesByDescriptor.computeIfAbsent(descriptor) {
            MethodPatch.Method(
                replacements = emptySet(),
                methodRef = methodRef,
                accessType = null,
                final = null,
            )
        }

        methodPatchesByDescriptor.computeIfPresent(descriptor) { _, v ->
            when (v) {
                is MethodPatch.ClassInit -> {
                    error("Cannot make modifications to the <clinit> block")
                }

                is MethodPatch.Constructor -> {
                    if (isFinal != null) {
                        error("Cannot make constructor final=$isFinal")
                    }
                    v.copy(
                        accessType = v.accessType ?: accessType,
                    )
                }

                is MethodPatch.Method -> {
                    v.copy(
                        accessType = v.accessType ?: accessType,
                        final = v.final ?: isFinal,
                    )
                }
            }
        }
    }

    fun mergePatchMethod(declaration: Declaration.PatchMethod) {
        val (isFinal, accessType, defaultValue) = declaration.reduceModifications()

        require(defaultValue == null) {
            "Cannot set a default value for a method"
        }

        val methodRef = declaration.methodRef
        val descriptor = methodRef.descriptor

        methodPatchesByDescriptor.computeIfAbsent(descriptor) {
            MethodPatch.Method(
                replacements = emptySet(),
                methodRef = methodRef,
                accessType = null,
                final = null,
            )
        }

        methodPatchesByDescriptor.computeIfPresent(descriptor) { _, v ->
            val replacements = v.replacements + declaration.replacements.toSet()
            when (v) {
                is MethodPatch.ClassInit -> {
                    require(isFinal == null && accessType == null) {
                        "Cannot make modifications to the <clinit> block"
                    }
                    v.copy(replacements = replacements)
                }

                is MethodPatch.Constructor -> {
                    if (isFinal != null) {
                        error("Cannot make constructor final: $isFinal")
                    }
                    v.copy(
                        accessType = v.accessType ?: accessType,
                        replacements = replacements,
                    )
                }

                is MethodPatch.Method -> {
                    v.copy(
                        accessType = v.accessType ?: accessType,
                        final = v.final ?: isFinal,
                        replacements = replacements,
                    )
                }
            }
        }
    }
}

fun patchFileOf(inputStream: InputStream): PatchFile {
    val lexer = Patch4JLexer(CharStreams.fromStream(inputStream))
    val tokens = CommonTokenStream(lexer)
    val parser = Patch4JParser(tokens)
    return PatchFileVisitor.visitPatchFile(parser.patchFile())
}
