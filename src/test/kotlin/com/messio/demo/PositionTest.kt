package com.messio.demo

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.math.BigDecimal

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PositionTest {
    private val p1 = Position(Coin.USD to BigDecimal.ONE, Coin.EUR to BigDecimal.TEN)
    private val p2 = Position(Coin.USD to BigDecimal("-3"), Coin.JPY to BigDecimal.ONE)

    @Test
    fun codec(){
        assertEquals(p1, Position.parse("{USD=1, EUR=10}"))
        print(p1)
    }

    @Test
    fun basic(){
        assertEquals(true, Position().isZero())
        assertEquals(true, Position(Coin.JPY to BigDecimal.ZERO).isZero())
        assertEquals(Position(Coin.USD to BigDecimal.ONE.negate(), Coin.EUR to BigDecimal.TEN.negate()), p1.negate())
        assertEquals(Position.parse("{USD=-2,EUR=10,JPY=1}"), p1.add(p2))
        assertEquals(Position.parse("{USD=4,EUR=10,JPY=-1}"), p1.subtract(p2))
    }

}