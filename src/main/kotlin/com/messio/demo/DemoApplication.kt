package com.messio.demo

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.time.LocalTime

@SpringBootApplication
class DemoApplication @Autowired constructor(
        val facade: Facade,
        val schedulerService: SchedulerService) {

    init {
        schedulerService.enter(BaseEvent(this, LocalTime.MIN, "opening"))
        schedulerService.enter(BaseEvent(this, LocalTime.MAX, "closing"))
        facade.bankRepository.findAll().forEach{ bank ->
            schedulerService.enter(BankEvent(this, bank.opening, "opening", bank))
            schedulerService.enter(BankEvent(this, bank.settlementCompletionTarget, "settlement completion target", bank))
            schedulerService.enter(BankEvent(this, bank.closing, "closing", bank))
            facade.currencyRepository.findByBank(bank).forEach { currency ->
                schedulerService.enter(CurrencyEvent(this, currency.opening, "opening", currency))
                schedulerService.enter(CurrencyEvent(this, currency.fundingCompletionTarget, "funding completion target", currency))
                schedulerService.enter(CurrencyEvent(this, currency.closing, "closing", currency))
                schedulerService.enter(CurrencyEvent(this, currency.close, "close", currency))
            }
        }
        // test
        schedulerService.run()
    }
}

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}
