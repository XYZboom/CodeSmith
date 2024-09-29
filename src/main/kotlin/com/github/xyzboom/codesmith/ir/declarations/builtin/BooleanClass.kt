package com.github.xyzboom.codesmith.ir.declarations.builtin

import com.github.xyzboom.codesmith.ir.types.IrClassType
import com.github.xyzboom.codesmith.ir.types.IrConcreteType
import com.github.xyzboom.codesmith.ir.types.builtin.IrBuiltinTypes

object BooleanClass: AbstractBuiltinClass("Boolean", IrBuiltinTypes.ANY, IrClassType.FINAL) {
    override val type: IrConcreteType get() = IrBuiltinTypes.BOOLEAN
}