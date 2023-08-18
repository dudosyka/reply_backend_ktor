package com.reply.stat.utils.formula.operates

import com.reply.stat.utils.formula.Operand

class SubOperate: FormulaOperate {
    override fun invoke(firstOperand: Operand, secondOperand: Operand): Double {
        return firstOperand.getValue() - secondOperand.getValue()
    }
}