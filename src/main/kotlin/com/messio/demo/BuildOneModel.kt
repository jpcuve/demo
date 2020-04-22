package com.messio.demo

import com.messio.demo.Account.Companion.MIRROR_NAME
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalTime
import java.util.concurrent.atomic.AtomicInteger

@Component
class BuildOneModel(val facade: Facade) : BankModel() {
    private val logger: Logger = LoggerFactory.getLogger(BuildOneModel::class.java)

    override fun initDay() {
    }

    override fun bankOpening(time: LocalTime, bank: Bank) {
    }

    override fun currencyOpening(time: LocalTime, currency: Currency) {
    }

    override fun currencyFundingCompletionTarget(time: LocalTime, currency: Currency) {
    }

    override fun currencyClosing(time: LocalTime, currency: Currency) {
        facade.instructionRepository
                .findAllByBankAndTypeAndBookedIsNull(currency.bank, InstructionType.PAY_OUT)
                .filter {
                    !it.moment.isAfter(time) && it.amount.containsKey(currency.coin)
                }
                .forEach {
                    facade.book(it, time)
                }
    }

    override fun currencyClose(time: LocalTime, currency: Currency) {
    }

    override fun settlementCompletionTarget(time: LocalTime, bank: Bank) {
        logger.debug("Booking pay-ins")
        val balance = Balance()
        facade.instructionRepository
                .findAllByBankAndTypeAndBookedIsNull(bank, InstructionType.PAY_IN)
                .filter {
                    !it.moment.isAfter(time)
                }
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
                    .findAllByBankAndTypeAndBookedIsNull(bank, InstructionType.SETTLEMENT)
                    .filter {
                        !it.moment.isAfter(time) && balance.isProvisioned(it.principal, it.amount)
                    }
                    .forEach {
                        facade.book(it, time)
                        balance.transfer(it.principal, it.counterparty, it.amount)
                        settledCount.incrementAndGet()
                    }
            logger.debug("Count of instructions settled: $settledCount")
        } while (settledCount.get() > 0)
        logger.debug("Generating pay-outs")
        balance
                .filter { it.key != MIRROR_NAME }
                .forEach { e ->
                    e.value.xlong()
                            .forEach {
                                val payout = Instruction(
                                        moment = time,
                                        type = InstructionType.PAY_OUT,
                                        principal = MIRROR_NAME,
                                        counterparty = e.key,
                                        amount = Position(it)
                                )
                                payout.bank = bank
                                logger.debug("Creating pay-out: $payout")
                                facade.instructionRepository.save(payout)
                            }
                }
    }

    override fun bankClosing(time: LocalTime, bank: Bank) {
    }

    override fun doneDay() {
    }
}