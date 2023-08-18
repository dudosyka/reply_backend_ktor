package com.reply.stat.utils.formula


class Operand(
    private val type: OperandType,
    private val value: Any
) {
    enum class OperandType {
        NUMBER
    }
    fun getValue(): Double =
            value.toString().toDouble()
}