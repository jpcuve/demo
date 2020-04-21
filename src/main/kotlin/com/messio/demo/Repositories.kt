package com.messio.demo

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository

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
    fun findByBank(bank: Bank): Iterable<Instruction>
}

@Component
class Facade(
        val bankRepository: BankRepository,
        val currencyGroupRepository: CurrencyGroupRepository,
        val currencyRepository: CurrencyRepository,
        val movementRepository: MovementRepository,
        val instructionRepository: InstructionRepository
)