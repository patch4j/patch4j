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

internal object ModifierShortcutVisitor : Patch4JBaseVisitor<Modification?>() {
    override fun defaultResult(): Modification? = null

    override fun visitAccessModifierShortcut(ctx: Patch4JParser.AccessModifierShortcutContext): Modification =
        Modification.Access(type = ctx.modifier().accessType)

    override fun visitFinalToggleModifierShortcut(ctx: Patch4JParser.FinalToggleModifierShortcutContext): Modification =
        Modification.SetFinal(
            final = ctx.finalToggle().BANG() == null,
        )
}
