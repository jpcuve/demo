package com.messio.demo

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController @Autowired constructor(
        val bankRepository: BankRepository,
        val currencyGroupRepository: CurrencyGroupRepository,
        val currencyRepository: CurrencyRepository
) {
    @GetMapping("/all-banks")
    fun apiAllBanks() = bankRepository.findAll().asSequence().toList()

    @GetMapping("/all-currency-groups")
    fun apiAllCurrencyGroups() = currencyGroupRepository.findAllByOrderByPriority().asSequence().toList()

    @GetMapping("/all-currencies")
    fun apiAllCurrencies() = currencyRepository.findAll().asSequence().toList()

}