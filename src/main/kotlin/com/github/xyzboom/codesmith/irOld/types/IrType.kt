package com.github.xyzboom.codesmith.irOld.types

import com.github.xyzboom.codesmith.irOld.declarations.IrClass
import com.github.xyzboom.codesmith.irOld.declarations.IrCompanionObject
import com.github.xyzboom.codesmith.irOld.declarations.IrFile
import com.github.xyzboom.codesmith.irOld.declarations.builtin.AbstractBuiltinClass

sealed interface IrType: IrTypeArgument {
    val name: String
    val nullability: Nullability
    val classType: IrClassType
    val declaration: IrClass

    fun copy(nullability: Nullability): IrType

    val fullName: String
        get() = (if (declaration is AbstractBuiltinClass) {
            name
        } else when (val typeContainer = declaration.containingDeclaration) {
            is IrCompanionObject -> TODO()
            is IrClass -> TODO()
            is IrFile ->
                "${typeContainer.containingPackage.fullName}.$name"
        })

    fun equalsIgnoreNullability(other: IrType): Boolean
}