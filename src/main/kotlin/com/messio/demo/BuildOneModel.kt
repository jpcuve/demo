package com.messio.demo

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalTime

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
    }

    override fun bankClosing(time: LocalTime, bank: Bank) {
    }

    override fun doneDay() {
    }
}