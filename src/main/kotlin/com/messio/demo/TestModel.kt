package com.messio.demo

import org.springframework.stereotype.Component
import java.time.LocalTime

@Component
class TestModel: BankModel() {
    override fun initDay() {
        print("Init day")
    }

    override fun bankOpening(time: LocalTime, bank: Bank) {
        print("opening $bank")
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