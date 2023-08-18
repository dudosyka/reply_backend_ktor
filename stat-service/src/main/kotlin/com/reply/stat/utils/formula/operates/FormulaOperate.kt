package com.reply.stat.utils.formula.operates

import com.reply.stat.utils.formula.Operand

fun interface FormulaOperate {
    fun invoke(firstOperand: Operand, secondOperand: Operand): Double
}