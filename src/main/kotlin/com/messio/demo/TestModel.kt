package com.messio.demo

import org.springframework.stereotype.Component
import java.time.LocalTime

@Component
class TestModel: BankModel() {
    override fun initDay() {
        print("Init day")
    }

    override fun bankOpening(time: LocalTime, bank: Bank) {
        print("Opening: $bank")
    }

    override fun currencyOpening(time: LocalTime, currency: Currency) {
        print("Opening currency: $currency")
    }

    override fun currencyFundingCompletionTarget(time: LocalTime, currency: Currency) {
        print("FCT currency: $currency")
    }

    override fun currencyClosing(time: LocalTime, currency: Currency) {
        print("Closing currency: $currency")
    }

    override fun currencyClose(time: LocalTime, currency: Currency) {
        print("Close currency: $currency")
    }

    override fun settlementCompletionTarget(time: LocalTime, bank: Bank) {
        print("SCT: $bank")
    }

    override fun bankClosing(time: LocalTime, bank: Bank) {
        print("Closing: $bank")
    }

    override fun doneDay() {
        print("Done day")
    }
}