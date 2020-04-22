package com.messio.demo

import com.messio.demo.Account.Companion.MIRROR_NAME
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalTime
import java.util.concurrent.atomic.AtomicInteger

@Component
class BuildOneModel(val facade: Facade): BankModel(){
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
    }

    override fun currencyClose(time: LocalTime, currency: Currency) {
    }

    override fun settlementCompletionTarget(time: LocalTime, bank: Bank) {
        logger.debug("Booking pay-ins")
        val balance = Balance()
        facade.instructionRepository.findAllByBankAndTypeAndMomentLessThanEqual(bank, InstructionType.PAY_IN, time).forEach {
            logger.debug("Booking: $it")
            facade.book(it)
            balance.transfer(it.principal, it.counterparty, it.amount)
        }
        logger.debug("Settling sequentially")
        val settledCount = AtomicInteger()
        do {
            settledCount.set(0)
            // simplest stuff, run once and only allow if sufficient provision on account
            facade.instructionRepository.findAllByBankAndTypeAndMomentLessThanEqual(bank, InstructionType.SETTLEMENT, time).filter {
                balance.isProvisioned(it.principal, it.amount)
            }.forEach {
                facade.book(it)
                balance.transfer(it.principal, it.counterparty, it.amount)
                settledCount.incrementAndGet()
            }
            logger.debug("Count of instructions settled: $settledCount")
        } while (settledCount.get() > 0)
        logger.debug("Creating pay-outs")
        logger.debug("$balance")
        balance.entries.filter { it.key != MIRROR_NAME }.forEach {

        }
    }

    override fun bankClosing(time: LocalTime, bank: Bank) {
    }

    override fun doneDay() {
    }
}