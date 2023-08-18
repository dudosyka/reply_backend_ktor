package com.reply.stat.utils.formula

import com.reply.libs.dto.internal.exceptions.InternalServerError
import com.reply.stat.utils.formula.operates.DivOperate
import com.reply.stat.utils.formula.operates.MultiplyOperate
import com.reply.stat.utils.formula.operates.SubOperate
import com.reply.stat.utils.formula.operates.SumOperate

class Formula (
    private var formula: String,
    private var valueByQuestion: Map<Int, Int>
) {
    //Regex for capturing divider and multiplier
    private val firstGradeOperatorsRegex = Regex(pattern = "([&$]\\d+)([*\\\\])([&$]\\d+)")
    //Regex for capturing plus and minus
    private val secondGradeOperatorsRegex = Regex(pattern = "([&$]\\d+)([+-])([&$]\\d+)")

    private val redundantScopeReplacer = Regex(pattern = "\\(([&\$]\\d+)\\)")

    private var curResult: Int = 0
    private var calculated: Boolean = false

    private val operators = mapOf(
        "+" to SumOperate(),
        "-" to SubOperate(),
        "/" to DivOperate(),
        "*" to MultiplyOperate()
    )
    private fun parseOperand(operand: String, valueByQuestion: Map<Int, Int>): Operand {
        return if (operand.contains(Regex("&")))
            Operand(Operand.OperandType.NUMBER, operand.replace("&", ""))
        else if (operand.contains(Regex("\$"))) {
            val index = operand.replace("\$", "").toInt()
            val value = valueByQuestion[index] ?: throw InternalServerError("Failed to parse formula")
            Operand(Operand.OperandType.NUMBER, value.toString())
        }
        else
            throw InternalServerError("Failed to parse formula")
    }

    private fun checkIsCalculationEnded(regex: Regex) {
        val applied = regex.findAll(formula).asIterable()
        if (applied.count() <= 0)
            return

        val firstMatch = applied.first().value.replace("(", "").replace(")", "")

        calculated = (firstMatch == formula.replace(")", "").replace("(", ""))
    }

    private fun applyRegex(regex: Regex) {
        println(formula)
        val applied = regex.findAll(formula).asIterable()

        checkIsCalculationEnded(regex)

        applied.forEach {
            val firstOperand = it.groupValues[1]
            val secondOperand = it.groupValues.last()

            println(it.groupValues)

            val operator =
                operators[it.groupValues[2]] ?: throw InternalServerError("Failed to parse formula") //second position

            curResult = operator.invoke(
                parseOperand(firstOperand, valueByQuestion),
                parseOperand(secondOperand, valueByQuestion)
            ).toInt()
            formula = formula.replace(it.groupValues.first(), "&$curResult")
        }
    }

    private fun clearScopes() {
        val matches = redundantScopeReplacer.findAll(formula).asIterable()

        matches.forEach {
            formula = formula.replace(it.groupValues.first(), it.groupValues.last())
        }
    }

    fun calc(): Int {
        var iterates = 0
        while (!calculated) {
            applyRegex(firstGradeOperatorsRegex)
            if (calculated)
                break
            applyRegex(secondGradeOperatorsRegex)

            iterates++
            if (iterates > 10)
                break
            clearScopes()
        }

        if (iterates > 10)
            throw InternalServerError("failed to parse formula")

        return curResult
    }
}