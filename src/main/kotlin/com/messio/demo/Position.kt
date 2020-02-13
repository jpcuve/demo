package com.messio.demo

import java.math.BigDecimal
import javax.persistence.AttributeConverter

class Position constructor(vararg amounts: Pair<String, BigDecimal>) : HashMap<String, BigDecimal>() {
    init {
        amounts.forEach { put(it.first, it.second) }
    }

    constructor(p: Map<String, BigDecimal>) : this() {
        putAll(p)
    }

    fun add(that: Position): Position = Position()

    fun negate(): Position = Position(mapValues { it.value.negate() })

    fun normalize(): Position = Position(filter { it.value.signum() != 0 })

    companion object {
        val ZERO: Position = Position("USD" to BigDecimal(1)).negate()

        fun parse(s: String?): Position? {
            if (s == null) return null
            return ZERO;
        }
    }
}


class PositionConverter: AttributeConverter<Position, String> {
    override fun convertToDatabaseColumn(attribute: Position?): String? = attribute?.toString()
    override fun convertToEntityAttribute(dbData: String?): Position? = Position.parse(dbData)
}
