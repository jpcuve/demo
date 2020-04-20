package com.messio.demo

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController @Autowired constructor(val facade: Facade) {
    @GetMapping("/all-banks")
    fun apiAllBanks() = facade.bankRepository.findAll().asSequence().toList()

    @GetMapping("/all-currency-groups")
    fun apiAllCurrencyGroups() = facade.currencyGroupRepository.findAllByOrderByPriority().asSequence().toList()

    @GetMapping("/all-currencies")
    fun apiAllCurrencies() = facade.currencyRepository.findAll().asSequence().toList()

    @GetMapping("/all-instructions")
    fun apiAllInstructions() = facade.instructionRepository.findAll().asSequence().toList()
}