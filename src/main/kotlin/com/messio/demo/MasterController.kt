package com.messio.demo

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/master")
class MasterController @Autowired constructor(
        val appProperties: AppProperties,
        val facade: Facade
) {
    private val logger: Logger = LoggerFactory.getLogger(MasterController::class.java)

    init {
        logger.debug("App prop: ${appProperties.defaultBank}")
    }

    @GetMapping("/statement")
    fun apiStatement(
            @RequestParam("bank-name") bankName: String,
            @RequestParam("account-name") accountName: String
    ): List<Instruction> {
        return facade.bankRepository.findByName(bankName)
                ?.let { bank ->
                    facade.instructionRepository.findAllByBank(bank)
                            .filter { it.booked != null && (it.partyNames.contains(accountName)) }
                            .sortedBy { it.bookId ?: 0L }
                            .toList()
                }
                ?: emptyList()
    }

    @GetMapping("/all-banks")
    fun apiAllBanks() = facade.bankRepository.findAll().asSequence().toList()

    @GetMapping("/all-currency-groups")
    fun apiAllCurrencyGroups() = facade.currencyGroupRepository.findAllByOrderByPriority().asSequence().toList()

    @GetMapping("/all-currencies")
    fun apiAllCurrencies() = facade.currencyRepository.findAll().asSequence().toList()

    @GetMapping("/all-instructions")
    fun apiAllInstructions() = facade.instructionRepository.findAll().asSequence().toList()
}