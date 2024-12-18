package com.github.xyzboom.codesmith.generator

import com.github.xyzboom.codesmith.irOld.types.builtin.IrBuiltinTypes.builtinTypes

object KeyWords {

    val java = listOf(
        "abstract",
        "assert",
        "boolean",
        "break",
        "byte",
        "case",
        "catch",
        "char",
        "class",
        "const",
        "continue",
        "default",
        "do",
        "double",
        "else",
        "enum",
        "extends",
        "final",
        "finally",
        "float",
        "for",
        "goto",
        "if",
        "implements",
        "import",
        "instanceof",
        "int",
        "interface",
        "long",
        "native",
        "new",
        "package",
        "private",
        "protected",
        "public",
        "return",
        "short",
        "static",
        "strictfp",
        "super",
        "switch",
        "synchronized",
        "this",
        "throw",
        "throws",
        "transient",
        "try",
        "void",
        "volatile",
        "while",
        "Object"
    )
    val kotlin = listOf(
        "as", "break", "fun", "in", "is", "object", "typealias", "typeof", "val", "var", "when",
        "Unit", "Any"
    )
    val builtins = listOf("Object") + builtinTypes.map { it.name }
    val windows = listOf("nul")
}