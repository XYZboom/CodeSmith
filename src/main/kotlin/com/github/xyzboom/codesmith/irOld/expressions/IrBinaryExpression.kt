package com.github.xyzboom.codesmith.irOld.expressions

import com.github.xyzboom.codesmith.irOld.IrOperator
import com.github.xyzboom.codesmith.irOld.visitor.IrVisitor

interface IrBinaryExpression: IrExpression {
    val left: IrExpression
    val right: IrExpression
    val operator: IrOperator

    override fun <R, D> accept(visitor: IrVisitor<R, D>, data: D): R {
        return visitor.visitBinaryExpression(this, data)
    }

    override fun <D> acceptChildren(visitor: IrVisitor<Unit, D>, data: D) {
        left.accept(visitor, data)
        operator.accept(visitor, data)
        right.accept(visitor, data)
    }
}