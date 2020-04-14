package com.messio.demo

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.LocalTime
import javax.persistence.*

@Entity
@Table(name = "accounts", uniqueConstraints = [UniqueConstraint(columnNames = ["name", "bank_id"])])
class Account(
        @Id @Column(name = "id") var id: Long = 0L,
        @Column(name = "name", nullable = false) var name: String = "",
        @Column(name = "short_position_limit", nullable = false) var shortPositionLimit: Position = Position.ZERO
){
    @ManyToOne @JoinColumn(name = "bank_id", nullable = false) lateinit var bank: Bank
    @Column(name = "bank_id", insertable = false, updatable = false) var bankId: Long = 0L
}

@Entity
@Table(name = "banks", uniqueConstraints = [UniqueConstraint(columnNames = ["name"])])
class Bank(
        @Id @Column(name = "id") var id: Long = 0L,
        @Column(name = "name", nullable = false, unique = true) var name: String = "",
        @Column(name = "opening", nullable = false) var opening: LocalTime = LocalTime.MIN,
        @Column(name = "closing", nullable = false) var closing: LocalTime = LocalTime.MAX,
        @Column(name = "settlement_completion_target", nullable = false) var settlementCompletionTarget: LocalTime = LocalTime.NOON,
        @Column(name = "minimum_pay_in", nullable = false) var minimumPayIn: Position = Position.ZERO
)

@Entity
@Table(name = "currency_groups", uniqueConstraints = [UniqueConstraint(columnNames = ["name"])])
class CurrencyGroup(
        @Id @Column(name = "id") var id: Long = 0L,
        @Column(name = "name", nullable = false, unique = true) var name: String = "",
        @Column(name = "priority", nullable = false) var priority: Int = 0
)

@Entity
@Table(name = "currencies", uniqueConstraints = [UniqueConstraint(columnNames = ["coin"])])
@JsonIgnoreProperties("bank", "currencyGroup")
class Currency(
        @Id @Column(name = "id") var id: Long = 0L,
        @Enumerated(EnumType.STRING) @Column(name = "coin") var coin: Coin = Coin.EUR,
        @Column(name = "opening", nullable = false) var opening: LocalTime = LocalTime.MIN,
        @Column(name = "closing", nullable = false) var closing: LocalTime = LocalTime.MAX,
        @Column(name = "funding_completion_target", nullable = false) var fundingCompletionTarget: LocalTime = LocalTime.NOON,
        @Column(name = "close", nullable = false) var close: LocalTime = LocalTime.MAX
){
    @ManyToOne @JoinColumn(name = "bank_id", nullable = false) lateinit var bank: Bank
    @Column(name = "bank_id", insertable = false, updatable = false) var bankId: Long = 0L
    @ManyToOne @JoinColumn(name = "currency_group_id", nullable = false) lateinit var currencyGroup: CurrencyGroup
    @Column(name = "currency_group_id", insertable = false, updatable = false) var currencyGroupId: Long = 0L
}

@Entity
@Table(name = "instructions")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "disc")
@JsonIgnoreProperties("bank")
open class Instruction(
        @Id @Column(name = "id") var id: Long = 0,
        @Column(name = "moment", nullable = true) open var moment: LocalTime? = null,
        @Column(name = "principal", nullable = false) open var principal: String = "",
        @Column(name = "reference", nullable = false) open var reference: String = "",
        @Column(name = "amount", nullable = false) open var amount: Position = Position.ZERO
){
    @ManyToOne @JoinColumn(name = "bank_id", nullable = false) open lateinit var bank: Bank
    @Column(name = "bank_id", insertable = false, updatable = false) open var bankId: Long = 0L
}

@Entity
@DiscriminatorValue("T")
class Transfer(
        override var moment: LocalTime? = null,
        override var principal: String = "",
        override var reference: String = "",
        override var amount: Position = Position.ZERO,
        @Column(name = "counterparty", nullable = false) var counterparty: String = ""
): Instruction(moment = moment, principal = principal, reference = reference, amount = amount)
