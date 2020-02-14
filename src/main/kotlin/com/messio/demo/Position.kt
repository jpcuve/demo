package com.messio.demo

import java.math.BigDecimal
import javax.persistence.AttributeConverter

enum class Coin {
    USD, EUR, JPY
}

class Position : HashMap<Coin, BigDecimal> {
    constructor(map: Map<Coin, BigDecimal>) : super(map)

    constructor(vararg amounts: Pair<Coin, BigDecimal>) {
        amounts.forEach { put(it.first, it.second) }
        normalize()
    }

    fun add(other: Position) = other.mapValuesTo(Position(this), { getOrDefault(it.key, BigDecimal.ZERO).add(it.value) }).normalize()

    fun subtract(other: Position) = other.mapValuesTo(Position(this), { getOrDefault(it.key, BigDecimal.ZERO).subtract(it.value) }).normalize()

    fun negate() = Position(mapValues { it.value.negate() }).normalize()

    fun normalize() = Position(filter { it.value.signum() != 0 })

    fun isZero() = normalize().isEmpty()

    companion object {
        val ZERO = Position()

        fun parse(s: String?): Position? {
            if (s == null) return null
            val st = s.trim()
            if (st.length < 2 || st[0] != '{' || st[s.lastIndex] != '}') return ZERO
            return Position(
                    st
                    .substring(1, s.length - 1)
                    .split(',')
                    .filter { it.contains('=') }
                    .map { Pair(Coin.valueOf(it.substring(0, it.indexOf('=')).trim()), BigDecimal(it.substring(it.indexOf('=') + 1).trim())) }
                    .toMap()
            )
        }
    }
}


class PositionConverter : AttributeConverter<Position, String> {
    override fun convertToDatabaseColumn(attribute: Position?): String? = attribute?.toString()
    override fun convertToEntityAttribute(dbData: String?): Position? = Position.parse(dbData)
}
