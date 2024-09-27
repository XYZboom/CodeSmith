package com.github.xyzboom.codesmith.ir.generator

import com.github.xyzboom.codesmith.ir.declarations.IrModule
import com.github.xyzboom.codesmith.ir.declarations.IrProgram
import com.github.xyzboom.codesmith.ir.declarations.impl.IrModuleImpl
import com.github.xyzboom.codesmith.ir.declarations.impl.IrProgramImpl
import kotlin.math.roundToInt
import kotlin.random.Random

class IrGeneratorImpl(
    seed: Long = System.currentTimeMillis(),
    private val config: GeneratorConfig = GeneratorConfig.default
) : IrGenerator {
    private val random = Random(seed)
    override fun randomName(startsWithUpper: Boolean): String {
        val length = config.nameLengthRange.random(random)
        val sb = StringBuilder(
            if (startsWithUpper) {
                "${upperLetters.random(random)}"
            } else {
                "${lowerLetters.random(random)}"
            }
        )
        for (i in 1 until length) {
            sb.append(lettersAndNumbers.random(random))
        }
        return sb.toString()
    }

    private fun IrProgram.generateModuleDependencies() {
        if (modules.size <= 1) return
        if (modules.size == 2) {
            if (random.nextBoolean()) {
                modules[0].dependsOn(modules[1])
            } else {
                modules[1].dependsOn(modules[0])
            }
        }
        val allModules = ArrayList(modules)
        val needMax = (allModules.size * config.moduleDependencySelectionRate).toInt().coerceAtLeast(2)
        val waitBeDepended = ArrayDeque<IrModule>()
        val iterator = allModules.iterator()
        while (iterator.hasNext()) {
            for (i in 0 until needMax) {
                val next = iterator.next()
                if (waitBeDepended.isNotEmpty()) {
                    waitBeDepended.shuffle(random)
                    val minTake = (waitBeDepended.size * config.moduleDependencyMinRate)
                        .roundToInt().coerceIn(0..<waitBeDepended.size)
                    val maxTake = (waitBeDepended.size * config.moduleDependencyMaxRate)
                        .roundToInt().coerceIn(minTake + 1..waitBeDepended.size)
                    next.dependsOn(waitBeDepended.take(random.nextInt(minTake, maxTake)))
                }
                waitBeDepended.add(next)
                if (!iterator.hasNext()) {
                    break
                }
            }
            val sizeToMove = (waitBeDepended.size * config.moduleDependencyEliminateRate).toInt()
            for (i in 0 until sizeToMove) {
                waitBeDepended.removeFirstOrNull()
            }
        }
    }

    override fun generate(): IrProgram {
        return IrProgramImpl().apply {
            for (i in 0 until config.moduleNumRange.random(random)) {
                modules.add(IrModuleImpl(randomName(false)))
            }
            generateModuleDependencies()
        }
    }
}