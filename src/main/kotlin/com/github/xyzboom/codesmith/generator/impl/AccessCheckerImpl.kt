package com.github.xyzboom.codesmith.generator.impl

import com.github.xyzboom.codesmith.generator.IAccessChecker
import com.github.xyzboom.codesmith.ir.IrAccessModifier.INTERNAL
import com.github.xyzboom.codesmith.ir.IrAccessModifier.PUBLIC
import com.github.xyzboom.codesmith.ir.declarations.*
import com.github.xyzboom.codesmith.ir.declarations.builtin.BuiltinClasses

class AccessCheckerImpl: IAccessChecker {
    //<editor-fold desc="same container">
    @Suppress("RecursivePropertyAccessor")
    override val IrDeclaration.containingPackage: IrPackage
        get() = when (this) {
            is IrClass -> when (val container = containingDeclaration) {
                is IrClass -> container.containingPackage
                is IrFile -> container.containingPackage // property is in IrFile, not extension here
            }

            is IrFunction -> when (val container = containingDeclaration) {
                is IrClass -> container.containingPackage
                is IrFile -> container.containingPackage // property is in IrFile, not extension here
                is IrFunction -> container.containingPackage
            }
        }

    override fun IrDeclaration.isInSamePackage(declaration: IrDeclaration): Boolean {
        return this.containingPackage === declaration.containingPackage
    }

    //</editor-fold>
    //<editor-fold desc="accessible">
    override val IrFile.accessibleClasses: Set<IrClass>
        get() = mutableSetOf<IrClass>().apply {
            addAll(BuiltinClasses.builtins)
            addAll(declarations.filterIsInstance<IrClass>())
            addAll(containingPackage.accessibleClasses)
            addAll(containingPackage.containingModule.accessibleClasses)
            addAll(containingPackage.containingModule.program.modules.flatMap { it.accessibleClasses })
        }

    override val IrPackage.accessibleClasses: Set<IrClass>
        get() = mutableSetOf<IrClass>().apply {
            addAll(BuiltinClasses.builtins)
            addAll(files.flatMap {
                it.declarations.filterIsInstance<IrClass>().filter { it1 -> it1.accessModifier == PUBLIC }
            })
        }

    override val IrModule.accessibleClasses: Set<IrClass>
        get() = mutableSetOf<IrClass>().apply {
            addAll(BuiltinClasses.builtins)
            addAll(packages.flatMap { p ->
                p.files.flatMap {
                    it.declarations.filterIsInstance<IrClass>().filter { it1 -> it1.accessModifier == PUBLIC }
                }
            })
        }

    override val IrProgram.accessibleClasses: Set<IrClass>
        get() = mutableSetOf<IrClass>().apply {
            addAll(BuiltinClasses.builtins)
            addAll(modules.flatMap { m ->
                m.packages.flatMap { p ->
                    p.files.flatMap {
                        it.declarations.filterIsInstance<IrClass>().filter { it1 -> it1.accessModifier == PUBLIC }
                    }
                }
            })
        }

    override fun IrFile.isAccessible(function: IrFunction): Boolean {
        TODO("Not yet implemented")
    }

    override fun IrFile.isAccessible(clazz: IrClass): Boolean {
        val clazzContainingDecl = clazz.containingDeclaration
        if (clazzContainingDecl === this) return true
        return when (clazz.accessModifier) {
            PUBLIC -> when (clazzContainingDecl) {
                is IrClass -> isAccessible(clazzContainingDecl)
                is IrFile -> containingPackage.containingModule
                    .isDependOn(clazzContainingDecl.containingPackage.containingModule)
            }

            INTERNAL -> when (clazzContainingDecl) {
                is IrClass -> isAccessible(clazzContainingDecl)
                is IrFile -> containingPackage === clazzContainingDecl.containingPackage
            }

            else -> TODO()
        }
    }

    override fun IrClass.isAccessible(clazz: IrClass): Boolean {
        val myContainer = containingDeclaration
        val otherContainer = clazz.containingDeclaration
        if (this === otherContainer) return true
        if (this.superType?.declaration === clazz) return true
        when (myContainer) {
            is IrClass -> TODO()
            is IrFile -> when (otherContainer) {
                is IrClass -> TODO()
                is IrFile -> {
                    if (myContainer.isAccessible(clazz)) return true
                    return false
                }
            }
        }
    }

    override fun IrClass.isAccessible(function: IrFunction): Boolean {
        TODO("Not yet implemented")
    }

    //</editor-fold>
}