package com.messio.demo

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalTime

@Component
class DefaultModel(val facade: Facade): BankModel() {
    private val logger = LoggerFactory.getLogger(DefaultModel::class.java)

    override fun initDay() {
        logger.debug("Clearing all movements")
        facade.movementRepository.deleteAll()
    }

    override fun bankOpening(time: LocalTime, bank: Bank) {
        logger.debug("Opening: $bank")
    }

    override fun currencyOpening(time: LocalTime, currency: Currency) {
        logger.debug("Opening currency: ${currency.bank} $currency")
    }

    override fun currencyFundingCompletionTarget(time: LocalTime, currency: Currency) {
        logger.debug("FCT currency: ${currency.bank} $currency")
    }

    override fun currencyClosing(time: LocalTime, currency: Currency) {
        logger.debug("Closing currency: ${currency.bank} $currency")
    }

    override fun currencyClose(time: LocalTime, currency: Currency) {
        logger.debug("Close currency: ${currency.bank} $currency")
    }

    override fun settlementCompletionTarget(time: LocalTime, bank: Bank) {
        logger.debug("SCT: $bank")
    }

    override fun bankClosing(time: LocalTime, bank: Bank) {
        logger.debug("Closing: $bank")
    }

    override fun doneDay() {
        logger.debug("Done day")
    }
}