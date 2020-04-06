package com.messio.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DemoApplication(val facade: Facade, val schedulerService: SchedulerService) {
    init {
        facade.bankRepository.findAll().forEach{ bank ->
            schedulerService.enter(BankEvent(this, bank.opening, "${bank.name} opening", bank))
            schedulerService.enter(BankEvent(this, bank.closing, "${bank.name} closing", bank))
            facade.currencyRepository.findByBank(bank).forEach {currency ->
                schedulerService.enter(CurrencyEvent(this, currency.opening, "${currency.coin} opening", currency))
                schedulerService.enter(CurrencyEvent(this, currency.fundingCompletionTarget, "${currency.coin} fct", currency))
                schedulerService.enter(CurrencyEvent(this, currency.closing, "${currency.coin} closing", currency))
                schedulerService.enter(CurrencyEvent(this, currency.close, "${currency.coin} close", currency))
            }
        }
    }
}

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}
