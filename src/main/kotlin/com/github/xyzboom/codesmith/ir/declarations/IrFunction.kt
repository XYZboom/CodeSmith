package com.github.xyzboom.codesmith.ir.declarations

import com.github.xyzboom.codesmith.ir.expressions.IrExpression
import com.github.xyzboom.codesmith.ir.types.IrType
import com.github.xyzboom.codesmith.ir.visitor.IrVisitor

interface IrFunction: IrDeclaration, IrFunctionContainer, IrAccessModifierContainer {
    val name: String
    val containingDeclaration: IrFunctionContainer
    val returnType: IrType
    val valueParameters: MutableList<IrValueParameter>
    val expressions: MutableList<IrExpression>
    override fun <R, D> accept(visitor: IrVisitor<R, D>, data: D): R =
        when (this) {
            is IrConstructor -> visitor.visitConstructor(this, data)
            else -> visitor.visitFunction(this, data)
        }

    override fun <D> acceptChildren(visitor: IrVisitor<Unit, D>, data: D) {
        valueParameters.forEach { it.accept(visitor, data) }
    }
}