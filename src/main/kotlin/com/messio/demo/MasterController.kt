package com.messio.demo

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import javax.annotation.security.RolesAllowed
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/master")
@CrossOrigin(allowCredentials = "true")
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

    @GetMapping("/static")
    fun apiStatic(@Autowired req: HttpServletRequest): Map<String, Any> {
        val principal = req.userPrincipal.name
        logger.debug("Principal: $principal")
        facade.userRepository.findTopByEmail(principal)
                ?.let {
                    val currencyGroups = facade.currencyGroupRepository.findAll()
                    val currencies = facade.currencyRepository.findByBank(it.account.bank)
                    return mapOf(
                            "profile" to ProfileValue(true, "", it.name, it.securityRoles),
                            "account" to it.account,
                            "bank" to it.account.bank,
                            "currencyGroups" to currencyGroups,
                            "currencies" to currencies
                    )
                }
        throw CustomException("User not found: $principal")
    }

    @GetMapping("/all-banks")
    fun apiAllBanks() = facade.bankRepository.findAll().asSequence().toList()

    @GetMapping("/all-currency-groups")
    @RolesAllowed("ROLE_USER")
    fun apiAllCurrencyGroups() = facade.currencyGroupRepository.findAllByOrderByPriority().asSequence().toList()

    @GetMapping("/all-currencies")
    fun apiAllCurrencies() = facade.currencyRepository.findAll().asSequence().toList()

    @GetMapping("/all-instructions")
    fun apiAllInstructions() = facade.instructionRepository.findAll().asSequence().toList()
}