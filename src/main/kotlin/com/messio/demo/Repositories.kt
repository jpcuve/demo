package com.messio.demo

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import java.time.LocalTime

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
interface MovementRepository: CrudRepository<Movement, Long> {
    fun findByBank(bank: Bank): Iterable<Movement>
}

@Repository
interface InstructionRepository: CrudRepository<Instruction, Long> {
    fun findAllByBank(bank: Bank): Iterable<Instruction>
    fun findAllByBankAndTypeAndMomentLessThanEqual(bank: Bank, type: InstructionType, moment: LocalTime): Iterable<Instruction>
    @Query("select max(i.id) from Instruction i")
    fun findMaxId(): Long?
}

@Component
class Facade(
        val bankRepository: BankRepository,
        val currencyGroupRepository: CurrencyGroupRepository,
        val currencyRepository: CurrencyRepository,
        val movementRepository: MovementRepository,
        val instructionRepository: InstructionRepository
){
    fun book(instruction: Instruction){
        val maxId = instructionRepository.findMaxId() ?: 1L
        instructionRepository.findById(instruction.id).ifPresent {
            it.bookId = maxId + 1
            instructionRepository.save(instruction)
        }
    }
}