package com.messio.demo

import org.springframework.context.ApplicationEvent
import java.time.LocalTime

open class BaseEvent(source: Any, val instant: LocalTime, val name: String) : ApplicationEvent(source)

open class BankEvent(source: Any, instant: LocalTime, name: String, val bank: Bank): BaseEvent(source, instant, name)

class CurrencyEvent(source: Any, instant: LocalTime, name: String, val currency: Currency): BaseEvent(source, instant, name)

class InstructionEvent(source: Any, instant: LocalTime, name: String, val instruction: Instruction): BaseEvent(source, instant, name)