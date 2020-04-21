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
        TODO("Not yet implemented")
    }

    override fun currencyOpening(time: LocalTime, currency: Currency) {
        TODO("Not yet implemented")
    }

    override fun currencyFundingCompletionTarget(time: LocalTime, currency: Currency) {
        TODO("Not yet implemented")
    }

    override fun currencyClosing(time: LocalTime, currency: Currency) {
        TODO("Not yet implemented")
    }

    override fun currencyClose(time: LocalTime, currency: Currency) {
        TODO("Not yet implemented")
    }

    override fun settlementCompletionTarget(time: LocalTime, bank: Bank) {
        TODO("Not yet implemented")
    }

    override fun bankClosing(time: LocalTime, bank: Bank) {
        TODO("Not yet implemented")
    }

    override fun doneDay() {
        TODO("Not yet implemented")
    }
}