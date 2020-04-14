package com.messio.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DemoApplication(val facade: Facade, val schedulerService: SchedulerService) {
    init {
        facade.bankRepository.findAll().forEach{ bank ->
            schedulerService.enter(BankEvent(this, bank.opening, "${bank.name} opening", bank))
            facade.currencyRepository.findByBank(bank).forEach {currency ->
                schedulerService.enter(CurrencyEvent(this, currency.opening, "${currency.coin} opening", currency))
                schedulerService.enter(CurrencyEvent(this, currency.fundingCompletionTarget, "${currency.coin} funding completion target", currency))
                schedulerService.enter(CurrencyEvent(this, currency.closing, "${currency.coin} closing", currency))
                schedulerService.enter(CurrencyEvent(this, currency.close, "${currency.coin} close", currency))
            }
            schedulerService.enter(BankEvent(this, bank.closing, "${bank.name} closing", bank))
        }
        // testing db operations
        facade.bankRepository.findByName("TEST-01")?.let {
            val instruction: Instruction = Transfer(
                    counterparty = "C",
                    amount = Position.parse("{JPY=100}") ?: Position.ZERO,
                    reference = "TEST2"
            )
            instruction.bank = it
            facade.instructionRepository.save(instruction)
        }
    }
}

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}
