package com.messio.demo

import org.slf4j.LoggerFactory
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalTime

val logger = LoggerFactory.getLogger("com.messio.demo.repositories")

@Repository
interface BankRepository: CrudRepository<Bank, Long> {
    fun findByName(name: String): Bank?
}

@Repository
interface CurrencyGroupRepository: CrudRepository<CurrencyGroup, Long>{
    fun findAllByOrderByPriority(): Iterable<CurrencyGroup>
}

@Repository
interface CurrencyRepository: CrudRepository<Currency, Long> {
    fun findByBank(bank: Bank): Iterable<Currency>
}

@Repository
interface InstructionRepository: CrudRepository<Instruction, Long> {
    fun findAllByBank(bank: Bank): Iterable<Instruction>
    fun findAllByBankAndTypeAndBookedIsNull(bank: Bank, type: InstructionType): Iterable<Instruction>
    fun findAllByBankAndTypeAndBookedIsNotNull(bank: Bank, type: InstructionType): Iterable<Instruction>
    @Query("select max(i.bookId) from Instruction i")
    fun findMaxBookId(): Long?
}

@Component
class Facade(
        val bankRepository: BankRepository,
        val currencyGroupRepository: CurrencyGroupRepository,
        val currencyRepository: CurrencyRepository,
        val instructionRepository: InstructionRepository
){
    fun book(instruction: Instruction, time: LocalTime){
        val maxBookId = instructionRepository.findMaxBookId() ?: 0L
        instructionRepository.findById(instruction.id).ifPresent {
            logger.debug("Booking @ $time: $instruction")
            it.bookId = maxBookId + 1
            it.booked = time
            instructionRepository.save(it)
        }
    }
}