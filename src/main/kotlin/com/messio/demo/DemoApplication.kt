package com.messio.demo

import com.messio.demo.service.NotificationService
import com.messio.demo.service.SchedulerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import java.time.LocalTime

@SpringBootApplication
@EnableAsync
@EnableScheduling
class DemoApplication @Autowired constructor(
        val facade: Facade,
        val schedulerService: SchedulerService,
        val notificationService: NotificationService
) {

    init {
        schedulerService.enter(BaseEvent(this, LocalTime.MIN, "opening"))
        schedulerService.enter(BaseEvent(this, LocalTime.MAX, "closing"))
        facade.bankRepository.findAll().forEach { bank ->
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

    @Scheduled(fixedRate = 20000)
    fun scheduled() {
        notificationService.sendNotification("jpcuvelliez@gmail.com")
    }
}

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}
