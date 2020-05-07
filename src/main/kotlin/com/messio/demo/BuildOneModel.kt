package com.messio.demo

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component
import java.time.LocalTime
import java.util.concurrent.atomic.AtomicInteger

@Component
class BuildOneModel(val facade: Facade, val javaMailSender: JavaMailSender) : BankModel() {
    private val logger: Logger = LoggerFactory.getLogger(BuildOneModel::class.java)

    override fun currencyClosing(time: LocalTime, currency: Currency) {
        logger.debug("Closing currency: ${currency.coin}")
        facade.instructionRepository
                .findByBank(currency.bank)
                .filter { !it.moment.isAfter(time) && it.type == InstructionType.PAY_OUT && it.amount.containsKey(currency.coin) && it.booked == null}
                .forEach { facade.book(it, time) }
    }

    override fun settlementCompletionTarget(time: LocalTime, bank: Bank) {
        logger.debug("Booking pay-ins")
        val balance = Balance()
        facade.instructionRepository
                .findByBank(bank)
                .filter { !it.moment.isAfter(time) && it.type == InstructionType.PAY_IN && it.booked == null }
                .forEach {
                    logger.debug("Booking: $it")
                    facade.book(it, time)
                    balance.transfer(it.principal, it.counterparty, it.amount)
                }
        logger.debug("Settling sequentially")
        val settledCount = AtomicInteger()
        do {
            settledCount.set(0)
            // simplest stuff, run once and only allow if sufficient provision on account
            facade.instructionRepository
                    .findByBank(bank)
                    .filter { !it.moment.isAfter(time) && it.type == InstructionType.SETTLEMENT && it.booked == null && balance.isProvisioned(it.principal, it.amount) }
                    .forEach {
                        facade.book(it, time)
                        balance.transfer(it.principal, it.counterparty, it.amount)
                        settledCount.incrementAndGet()
                    }
            logger.debug("Count of instructions settled: $settledCount")
        } while (settledCount.get() > 0)
        logger.debug("Generating pay-outs")
        val mirror = facade.accountRepository.findByBank(bank).first { it.name == MIRROR_NAME }
        mirror.let {
            balance
                    .filter { it.key.name != MIRROR_NAME }
                    .forEach { e ->
                        e.value.xlong()
                                .forEach {
                                    val payout = Instruction(
                                            moment = time,
                                            type = InstructionType.PAY_OUT,
                                            amount = Position(it)
                                    )
                                    payout.principal = mirror
                                    payout.counterparty = e.key
                                    logger.debug("Creating pay-out: $payout")
                                    facade.instructionRepository.save(payout)
                                }
                    }
        }
    }

    override fun doneDay(time: LocalTime) {
        val message = SimpleMailMessage()
        message.setTo("jean-pierre.cuvelliez@skynet.be")
        message.setSubject("Settlement day finished")
        message.setText("It really is finished")
        javaMailSender.send(message)
    }
}