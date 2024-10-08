package com.github.xyzboom.codesmith.ir.declarations.builtin

import com.github.xyzboom.codesmith.ir.types.IrClassType
import com.github.xyzboom.codesmith.ir.types.IrConcreteType
import com.github.xyzboom.codesmith.ir.types.builtin.IrBuiltinTypes

object NumberClass: AbstractBuiltinClass("Number", IrBuiltinTypes.ANY, IrClassType.OPEN) {
    override val type: IrConcreteType get() = IrBuiltinTypes.NUMBER
}