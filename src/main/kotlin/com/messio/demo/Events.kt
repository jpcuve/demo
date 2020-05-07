package com.messio.demo

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationListener
import java.time.LocalTime

open class BaseEvent(source: Any, val instant: LocalTime, val name: String) : ApplicationEvent(source)

open class BankEvent(source: Any, instant: LocalTime, name: String, val bank: Bank): BaseEvent(source, instant, name)

class CurrencyEvent(source: Any, instant: LocalTime, name: String, val currency: Currency): BaseEvent(source, instant, name)

class InstructionEvent(source: Any, instant: LocalTime, name: String, val instruction: Instruction): BaseEvent(source, instant, name)

open class BankModel: ApplicationListener<BaseEvent> {
    companion object{
        val logger: Logger = LoggerFactory.getLogger(BankModel::class.java)
    }

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
                "opening" -> initDay(event.instant)
                "closing" -> doneDay(event.instant)
            }
        }
    }

    open fun initDay(time: LocalTime) = logger.debug("$time, start of day")
    open fun bankOpening(time: LocalTime, bank: Bank) = logger.debug("$time, opening of bank '$bank'")
    open fun currencyOpening(time: LocalTime, currency: Currency) = logger.debug("$time, opening of curreny '$currency' for bank '${currency.bank}'")
    open fun currencyFundingCompletionTarget(time: LocalTime, currency: Currency) = logger.debug("$time, funding completion target of curreny '$currency' for bank '${currency.bank}'")
    open fun currencyClosing(time: LocalTime, currency: Currency) = logger.debug("$time, closing of curreny '$currency' for bank '${currency.bank}'")
    open fun currencyClose(time: LocalTime, currency: Currency) = logger.debug("$time, close of curreny '$currency' for bank '${currency.bank}'")
    open fun settlementCompletionTarget(time: LocalTime, bank: Bank) = logger.debug("$time, settlement completion target of bank '$bank'")
    open fun bankClosing(time: LocalTime, bank: Bank) = logger.debug("$time, closing of bank '$bank'")
    open fun doneDay(time: LocalTime) = logger.debug("$time, end of day")
}