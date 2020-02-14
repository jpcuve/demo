package com.messio.demo

import org.springframework.data.repository.CrudRepository

interface BankRepository: CrudRepository<Bank, Long> {
    fun findByName(name: String): Bank?
}

interface CurrencyGroupRepository: CrudRepository<CurrencyGroup, Long>{
    fun findAllByOrderByPriority(): Iterable<CurrencyGroup>
}
interface CurrencyRepository: CrudRepository<Currency, Long>