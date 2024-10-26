package com.github.xyzboom.codesmith.mutator.impl

import com.github.xyzboom.codesmith.checkers.IAccessChecker
import com.github.xyzboom.codesmith.checkers.impl.AccessCheckerImpl
import com.github.xyzboom.codesmith.ir.IrAccessModifier.*
import com.github.xyzboom.codesmith.ir.declarations.IrConstructor
import com.github.xyzboom.codesmith.ir.declarations.IrProgram
import com.github.xyzboom.codesmith.ir.declarations.builtin.AbstractBuiltinClass
import com.github.xyzboom.codesmith.ir.expressions.IrConstructorCallExpression
import com.github.xyzboom.codesmith.ir.expressions.IrSpecialConstructorCallExpression
import com.github.xyzboom.codesmith.ir.types.IrFileType.JAVA
import com.github.xyzboom.codesmith.ir.types.IrFileType.KOTLIN
import com.github.xyzboom.codesmith.mutator.*
import kotlin.random.Random
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.memberProperties

@Suppress("Unused")
class IrMutatorImpl(
    private val config: MutatorConfig = MutatorConfig.default,
    private val random: Random = Random.Default,
): IrMutator(), IAccessChecker by AccessCheckerImpl() {

    init {
        if (!config.anyEnabled()) {
            throw IllegalArgumentException("config must enable at least one mutator")
        }
    }

    @ConfigBy("ktExposeKtInternal")
    override fun mutateKtExposeKtInternal(program: IrProgram): Pair<IrProgram, Boolean> {
        var success = false
        program.randomTraverseClasses(random) {
            if (it.accessModifier == PUBLIC &&
                it.superType?.declaration?.accessModifier == PUBLIC &&
                it.superType?.declaration?.containingFile?.fileType == KOTLIN &&
                it.superType?.declaration?.isInSamePackage(it) != true &&
                it.containingFile.fileType == KOTLIN
            ) {
                it.superType?.declaration?.accessModifier = INTERNAL
                success = true
                return@randomTraverseClasses true
            }
            false
        }
        return program to success
    }

    @ConfigBy("javaExposeKtInternal")
    override fun mutateJavaExposeKtInternal(program: IrProgram): Pair<IrProgram, Boolean> {
        var success = false
        program.randomTraverseClasses(random) {
            if (it is AbstractBuiltinClass || it.superType?.declaration is AbstractBuiltinClass) {
                return@randomTraverseClasses false
            }
            if (it.accessModifier == PUBLIC &&
                it.superType?.declaration?.accessModifier == PUBLIC &&
                it.superType?.declaration?.containingFile?.fileType == KOTLIN &&
                it.superType?.declaration?.isInSamePackage(it) != true &&
                it.containingFile.fileType == JAVA
            ) {
                it.superType?.declaration?.accessModifier = INTERNAL
                success = true
                return@randomTraverseClasses true
            }
            false
        }
        return program to success
    }

    @ConfigBy("constructorSuperCallPrivate")
    override fun mutateConstructorSuperCallPrivate(program: IrProgram): Pair<IrProgram, Boolean> {
        var success = false
        program.randomTraverseConstructors(random) {
            if (it.containingDeclaration is AbstractBuiltinClass
                || it.superCall.callTarget.containingDeclaration is AbstractBuiltinClass
            ) {
                return@randomTraverseConstructors false
            }
            it.superCall.callTarget.accessModifier = PRIVATE
            success = true
            true
        }
        return program to success
    }

    @ConfigBy("constructorSuperCallInternal")
    override fun mutateConstructorSuperCallInternal(program: IrProgram): Pair<IrProgram, Boolean> {
        var success = false
        program.randomTraverseConstructors(random) {
            if (it.containingDeclaration is AbstractBuiltinClass
                || it.superCall.callTarget.containingDeclaration is AbstractBuiltinClass
            ) {
                return@randomTraverseConstructors false
            }
            val thisClass = it.containingDeclaration
            val callTarget = it.superCall.callTarget
            val superClass = callTarget.containingDeclaration
            val superConstructorAccessModifier = callTarget.accessModifier
            val superIsJavaAndNotInSamePackage =
                superClass.containingFile.fileType == JAVA && !thisClass.isInSamePackage(superClass)
            val superIsKotlinAndNotInSameModule =
                thisClass.containingFile.fileType == KOTLIN &&
                        superClass.containingFile.fileType == KOTLIN && !thisClass.isInSameModule(superClass)
            if (superConstructorAccessModifier == PUBLIC &&
                (superIsJavaAndNotInSamePackage || superIsKotlinAndNotInSameModule)
            ) {
                val superCallTarget = it.superCall.callTarget
                superCallTarget.accessModifier = INTERNAL
                // in case there are no available overload
                superCallTarget.containingDeclaration.declarations.filterIsInstance<IrConstructor>()
                    .filter { it1 -> it1.valueParameters.size == superCallTarget.valueParameters.size }
                    .forEach { it1 -> it1.accessModifier = INTERNAL }
                success = true
                return@randomTraverseConstructors true
            }
            false
        }
        return program to success
    }

    @ConfigBy("constructorNormalCallPrivate")
    override fun mutateConstructorNormalCallPrivate(program: IrProgram): Pair<IrProgram, Boolean> {
        var success = false
        program.randomTraverseConstructors(random) { constructor ->
            constructor.superCall.valueArguments.shuffled(random).forEach { expr ->
                if (expr is IrConstructorCallExpression) {
                    val callTarget = expr.callTarget
                    if (callTarget.containingClass is AbstractBuiltinClass) return@forEach
                    if (callTarget.accessModifier < PRIVATE
                        && constructor.containingClass !== callTarget.containingClass
                    ) {
                        if (expr is IrSpecialConstructorCallExpression) {
                            callTarget.containingClass.declarations.filterIsInstance<IrConstructor>()
                                .filter { it.valueParameters.isEmpty() }.forEach { it.accessModifier = PRIVATE }
                        }
                        callTarget.accessModifier = PRIVATE
                        success = true
                        return@randomTraverseConstructors true
                    }
                }
            }
            false
        }
        return program to success
    }

    @ConfigBy("constructorNormalCallInternal")
    override fun mutateConstructorNormalCallInternal(program: IrProgram): Pair<IrProgram, Boolean> {
        var success = false
        program.randomTraverseConstructors(random) { constructor ->
            constructor.superCall.valueArguments.shuffled(random).forEach { expr ->
                if (expr is IrConstructorCallExpression) {
                    val callTarget = expr.callTarget
                    if (callTarget.containingClass is AbstractBuiltinClass) return@forEach
                    val callingJavaAndNotInSamePackage = callTarget.containingFile.fileType == JAVA &&
                            !constructor.containingClass.isInSamePackage(callTarget.containingClass)
                    val callingKotlinAndNotInSameModule = constructor.containingFile.fileType == KOTLIN &&
                            callTarget.containingFile.fileType == KOTLIN &&
                            !constructor.containingClass.isInSameModule(callTarget.containingClass)
                    if (callTarget.accessModifier < INTERNAL
                        && (callingJavaAndNotInSamePackage || callingKotlinAndNotInSameModule)
                    ) {
                        if (expr is IrSpecialConstructorCallExpression) {
                            callTarget.containingClass.declarations.filterIsInstance<IrConstructor>()
                                .filter { it.valueParameters.isEmpty() }.forEach { it.accessModifier = INTERNAL }
                        }
                        callTarget.accessModifier = INTERNAL
                        success = true
                        return@randomTraverseConstructors true
                    }
                }
            }
            false
        }
        return program to success
    }

    /**
     * do a mutated on specified [program].
     * Return the result program and the mutated position in [MutatorConfig].
     */
    override fun mutate(program: IrProgram): Pair<IrProgram, MutatorConfig> {
        val configByMethods = IrMutatorImpl::class.declaredMemberFunctions.filter {
            it.annotations.any { anno ->
                anno.annotationClass == ConfigBy::class
            }
        }
        val configParams = ::MutatorConfig.parameters
        val flow = configByMethods.map { method ->
            val anno = method.annotations.single { it.annotationClass == ConfigBy::class } as ConfigBy
            val configProperty = MutatorConfig::class.memberProperties.single { it.name == anno.name }
            val param = configParams.single { it.name == anno.name }
            @Suppress("UNCHECKED_CAST")
            Triple(method as KFunction<Pair<IrProgram, Boolean>>, configProperty, param)
        }
        val mutateResult = HashMap<KParameter, Boolean>()
        var myProg = program
        for ((method, property, param) in flow) {
            if (property.call(config) != true) {
                mutateResult[param] = false
                continue
            }
            val step = method.call(this, myProg)
            myProg = step.first
            mutateResult[param] = step.second
        }
        return myProg to ::MutatorConfig.callBy(mutateResult)
    }
}