package com.github.xyzboom.codesmith.ir.expressions

import com.github.xyzboom.codesmith.ir.IrElement
import com.github.xyzboom.codesmith.ir.visitor.IrVisitor

class IrBlock: IrElement(), IrExpressionContainer {
    override val expressions: MutableList<IrExpression> = mutableListOf()
    override fun <R, D> accept(visitor: IrVisitor<R, D>, data: D): R {
        return visitor.visitBlock(this, data)
    }

    override fun <D> acceptChildren(visitor: IrVisitor<Unit, D>, data: D) {
        expressions.forEach {
            it.accept(visitor, data)
        }
    }
}