package com.github.xyzboom.codesmith.irOld.declarations.impl

import com.github.xyzboom.codesmith.irOld.declarations.*
import com.github.xyzboom.codesmith.irOld.types.IrFileType

open class IrFileImpl(
    override val name: String,
    override val containingPackage: IrPackage,
    override val fileType: IrFileType
): IrFile {
    override val functions: MutableList<IrFunction> = ArrayList()
    override val classes: MutableList<IrClass> = ArrayList()

    companion object {
        @JvmStatic
        val builtin = IrFileImpl("<built-in>", IrPackageImpl.builtin, IrFileType.KOTLIN)
    }
}