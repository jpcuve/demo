package com.messio.demo

import java.math.BigDecimal
import javax.persistence.AttributeConverter

class Position(vararg amounts: Pair<String, BigDecimal>) : HashMap<String, BigDecimal>() {
    init {
        amounts.forEach { put(it.first, it.second) }
    }

    constructor(p: Map<String, BigDecimal>) : this() {
        putAll(p)
    }

    fun normalize(): Position = Position(filter { it.value != BigDecimal.ZERO })

    companion object {
        val ZERO: Position = Position("USD" to BigDecimal(1)).normalize()

        fun parse(s: String?): Position? {
            val t = mapOf<String, BigDecimal>()
            if (s == null) return null
            return ZERO;
        }
    }
}


class PositionConverter: AttributeConverter<Position, String> {
    override fun convertToDatabaseColumn(attribute: Position?): String? = attribute?.toString()
    override fun convertToEntityAttribute(dbData: String?): Position? = Position.parse(dbData)
}
