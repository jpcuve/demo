package com.messio.demo

import java.util.*

class Balance: TreeMap<Account, Position>() {

    fun transfer(principal: Account, counterparty: Account, amount: Position){
        this[principal] = (this[principal] ?: Position.ZERO).subtract(amount)
        this[counterparty] = (this[counterparty] ?: Position.ZERO).add(amount)
    }

    fun isProvisioned(principal: Account, amount: Position): Boolean {
        return (this[principal] ?: Position.ZERO).subtract(amount).isLong()
    }
}