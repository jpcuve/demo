package com.messio.demo

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController @Autowired constructor(
        val bankRepository: BankRepository
) {
    @GetMapping("/all-banks")
    fun apiAllBanks(): List<Bank> {
        return bankRepository.findAll().iterator().asSequence().toList()
    }

}