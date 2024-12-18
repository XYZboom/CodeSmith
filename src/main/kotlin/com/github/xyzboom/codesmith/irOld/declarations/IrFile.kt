package com.github.xyzboom.codesmith.irOld.declarations

import com.github.xyzboom.codesmith.irOld.IrElement
import com.github.xyzboom.codesmith.irOld.types.IrFileType
import com.github.xyzboom.codesmith.irOld.visitor.IrVisitor

interface IrFile: IrElement, IrFunctionContainer, IrDeclarationContainer, IrClassContainer {
    val name: String
    val fileType: IrFileType

    override val declarations: List<IrDeclaration>
        get() = ArrayList<IrDeclaration>(functions.size + classes.size).apply {
            addAll(functions)
            addAll(classes)
        }
    val containingPackage: IrPackage
    override fun <R, D> accept(visitor: IrVisitor<R, D>, data: D): R =
        visitor.visitFile(this, data)

    override fun <D> acceptChildren(visitor: IrVisitor<Unit, D>, data: D) {
        declarations.forEach { it.accept(visitor, data) }
    }
}