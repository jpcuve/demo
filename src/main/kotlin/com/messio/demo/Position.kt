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

    fun add(other: Position) = other.mapValuesTo(Position(this), { other.getOrDefault(it.key, BigDecimal.ZERO).add(it.value) }).normalize()

    fun subtract(other: Position) = other.mapValuesTo(Position(this), { getOrDefault(it.key, BigDecimal.ZERO).subtract(it.value) }).normalize()

    fun negate() = Position(mapValues { it.value.negate() })

    fun normalize() = Position(filter { it.value.signum() != 0 })

    companion object {
        val ZERO = Position("USD" to BigDecimal.ONE, "JPY" to BigDecimal.TEN).subtract(Position("USD" to BigDecimal.ONE, "EUR" to BigDecimal.TEN))

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
