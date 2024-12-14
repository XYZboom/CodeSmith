package com.github.xyzboom.codesmith.ir.declarations

import com.github.xyzboom.codesmith.ir.visitor.IrVisitor

class IrParameterDeclaration(name: String): IrDeclaration(name) {
    override fun <D> acceptChildren(visitor: IrVisitor<Unit, D>, data: D) {

    }
}