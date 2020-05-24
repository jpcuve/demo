package com.messio.demo.controller

import com.messio.demo.CustomException
import com.messio.demo.Facade
import com.messio.demo.Instruction
import com.messio.demo.ProfileValue
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/master")
@CrossOrigin(allowCredentials = "true")
class MasterController @Autowired constructor(val facade: Facade) {
    private val logger: Logger = LoggerFactory.getLogger(MasterController::class.java)

    @GetMapping("/statement")
    fun apiStatement(@Autowired req: HttpServletRequest): List<Instruction> {
        val userPrincipalName = req.userPrincipal.name
        facade.userRepository.findTopByEmail(userPrincipalName)?.let {
            return facade.instructionRepository.findByBank(it.account.bank)
                    .filter { i -> i.booked != null && (i.partyIds.contains(it.account.id)) }
                    .sortedBy { i -> i.bookId ?: 0L }
                    .toList()
        }
        throw CustomException("User not found: ${userPrincipalName}")
    }

    @GetMapping("/perpetual")
    fun apiPerpetual(@Autowired req: HttpServletRequest): Map<String, Any> {
        facade.userRepository.findTopByEmail(req.userPrincipal.name)?.let {
            val currencyGroups = facade.currencyGroupRepository.findAll()
            val currencies = facade.currencyRepository.findByBank(it.account.bank)
            return mapOf(
                    "profile" to ProfileValue(true, "", it.displayName, it.securityRoles),
                    "account" to it.account,
                    "bank" to it.account.bank,
                    "currencyGroups" to currencyGroups,
                    "currencies" to currencies
            )
        }
        throw CustomException("User not found")
    }
}