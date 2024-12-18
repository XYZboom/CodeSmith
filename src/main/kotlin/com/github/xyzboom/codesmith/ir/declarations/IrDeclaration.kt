package com.github.xyzboom.codesmith.ir.declarations

import com.github.xyzboom.codesmith.Language
import com.github.xyzboom.codesmith.ir.IrElement
import com.github.xyzboom.codesmith.ir.visitor.IrVisitor

abstract class IrDeclaration(
    val name: String,
): IrElement() {
    var language = Language.KOTLIN
    override fun <R, D> accept(visitor: IrVisitor<R, D>, data: D): R {
        return visitor.visitDeclaration(this, data)
    }
}