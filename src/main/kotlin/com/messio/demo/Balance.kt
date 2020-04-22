package com.messio.demo

import java.util.*

class Balance: TreeMap<String, Position>() {

    fun transfer(principal: String, counterparty: String, amount: Position){
        this[principal] = (this[principal] ?: Position.ZERO).subtract(amount)
        this[counterparty] = (this[counterparty] ?: Position.ZERO).add(amount)
    }

    fun isProvisioned(principal: String, amount: Position): Boolean {
        return (this[principal] ?: Position.ZERO).subtract(amount).isLong()
    }
}