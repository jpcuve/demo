package com.messio.demo

import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationListener
import java.time.LocalTime

open class BaseEvent(source: Any, val instant: LocalTime, val name: String) : ApplicationEvent(source)

open class BankEvent(source: Any, instant: LocalTime, name: String, val bank: Bank): BaseEvent(source, instant, name)

class CurrencyEvent(source: Any, instant: LocalTime, name: String, val currency: Currency): BaseEvent(source, instant, name)

class InstructionEvent(source: Any, instant: LocalTime, name: String, val instruction: Instruction): BaseEvent(source, instant, name)

abstract class BankModel: ApplicationListener<BaseEvent> {
    override fun onApplicationEvent(event: BaseEvent) {
        when (event){
            is CurrencyEvent -> when(event.name){
                "opening" -> currencyOpening(event.instant, event.currency)
                "funding completion target" -> currencyFundingCompletionTarget(event.instant, event.currency)
                "closing" -> currencyClosing(event.instant, event.currency)
                "close" -> currencyClose(event.instant, event.currency)
            }
            is BankEvent -> when(event.name){
                "opening" -> bankOpening(event.instant, event.bank)
                "settlement completion target" -> settlementCompletionTarget(event.instant, event.bank)
                "closing" -> bankClosing(event.instant, event.bank)
            }
            else -> when(event.name){
                "opening" -> initDay()
                "closing" -> doneDay()
            }
        }
    }

    abstract fun initDay()
    abstract fun bankOpening(time: LocalTime, bank: Bank)
    abstract fun currencyOpening(time: LocalTime, currency: Currency)
    abstract fun currencyFundingCompletionTarget(time: LocalTime, currency: Currency)
    abstract fun currencyClosing(time: LocalTime, currency: Currency)
    abstract fun currencyClose(time: LocalTime, currency: Currency)
    abstract fun settlementCompletionTarget(time: LocalTime, bank: Bank)
    abstract fun bankClosing(time: LocalTime, bank: Bank)
    abstract fun doneDay()
}