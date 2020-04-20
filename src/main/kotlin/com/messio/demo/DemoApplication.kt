package com.messio.demo

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.time.LocalTime

@SpringBootApplication
class DemoApplication @Autowired constructor(val facade: Facade, val schedulerService: SchedulerService) {

    init {
        facade.bankRepository.findAll().forEach{ bank ->
            schedulerService.enter(BaseEvent(this, LocalTime.MIN, "opening"))
            schedulerService.enter(BaseEvent(this, LocalTime.MAX, "closing"))
            schedulerService.enter(BankEvent(this, bank.opening, "opening", bank))
            schedulerService.enter(BankEvent(this, bank.settlementCompletionTarget, "settlement completion target", bank))
            schedulerService.enter(BankEvent(this, bank.closing, "closing", bank))
            facade.currencyRepository.findByBank(bank).forEach {currency ->
                schedulerService.enter(CurrencyEvent(this, currency.opening, "opening", currency))
                schedulerService.enter(CurrencyEvent(this, currency.fundingCompletionTarget, "funding completion target", currency))
                schedulerService.enter(CurrencyEvent(this, currency.closing, "closing", currency))
                schedulerService.enter(CurrencyEvent(this, currency.close, "close", currency))
            }
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
        // test
        schedulerService.run()
    }
}

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}
