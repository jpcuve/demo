package com.messio.demo

import org.springframework.data.repository.CrudRepository

interface BankRepository: CrudRepository<Bank, Long> {
    fun findByName(name: String): Bank?
}