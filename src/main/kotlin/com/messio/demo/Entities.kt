package com.messio.demo

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.LocalTime
import javax.persistence.*

const val MIRROR_NAME = "__MIRROR__"

@Entity
@Table(name = "accounts", uniqueConstraints = [UniqueConstraint(columnNames = ["name", "bank_id"])])
@JsonIgnoreProperties("bank")
class Account(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id") var id: Long = 0L,
        @Column(name = "name", nullable = false) var name: String = "",
        @Column(name = "short_position_limit", nullable = false) var shortPositionLimit: Position = Position.ZERO
) : Comparable<Account>{
    @ManyToOne
    @JoinColumn(name = "bank_id", nullable = false)
    lateinit var bank: Bank
    @Column(name = "bank_id", insertable = false, updatable = false)
    var bankId: Long = 0L

    override fun compareTo(other: Account): Int {
        return (id - other.id).toInt()
    }

    override fun equals(other: Any?): Boolean {
        return other is Account && other.id == id
    }
}

@Entity
@Table(name = "users")
@JsonIgnoreProperties("account")
class User(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id") var id: Long = 0L,
        @Column(name = "firebase_uid", unique = true) var firebaseUid: String?,
        @Column(name = "anonymous", nullable = false) var anonymous: Boolean = false,
        @Column(name = "email", unique = true, nullable = false) var email: String,
        @Column(name = "display_name") var displayName: String? = null,
        @Column(name = "roles", nullable = false) var roles: String = ""
) {
    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    lateinit var account: Account
    @Column(name = "account_id", insertable = false, updatable = false)
    var accountId: Long = 0L

    val securityRoles: List<String>
        get() = roles
                .split(",", ";", "|")
                .filter { !it.isBlank() }
                .map { String.format("ROLE_%s", it.toUpperCase().trim()) }
                .toList()
}


@Entity
@Table(name = "banks", uniqueConstraints = [UniqueConstraint(columnNames = ["name"])])
class Bank(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id") var id: Long = 0L,
        @Column(name = "name", nullable = false, unique = true) var name: String = "",
        @Column(name = "opening", nullable = false) var opening: LocalTime = LocalTime.MIN,
        @Column(name = "closing", nullable = false) var closing: LocalTime = LocalTime.MAX,
        @Column(name = "settlement_completion_target", nullable = false) var settlementCompletionTarget: LocalTime = LocalTime.NOON,
        @Column(name = "minimum_pay_in", nullable = false) var minimumPayIn: Position = Position.ZERO
) {
    override fun toString(): String = name
}

@Entity
@Table(name = "currency_groups", uniqueConstraints = [UniqueConstraint(columnNames = ["name"])])
class CurrencyGroup(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id") var id: Long = 0L,
        @Column(name = "name", nullable = false, unique = true) var name: String = "",
        @Column(name = "priority", nullable = false) var priority: Int = 0
)

@Entity
@Table(name = "currencies", uniqueConstraints = [UniqueConstraint(columnNames = ["bank_id", "coin"])])
@JsonIgnoreProperties("bank", "currencyGroup")
class Currency(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id") var id: Long = 0L,
        @Enumerated(EnumType.STRING) @Column(name = "coin") var coin: Coin = Coin.EUR,
        @Column(name = "opening", nullable = false) var opening: LocalTime = LocalTime.MIN,
        @Column(name = "closing", nullable = false) var closing: LocalTime = LocalTime.MAX,
        @Column(name = "funding_completion_target", nullable = false) var fundingCompletionTarget: LocalTime = LocalTime.NOON,
        @Column(name = "close", nullable = false) var close: LocalTime = LocalTime.MAX
) {
    @ManyToOne
    @JoinColumn(name = "bank_id", nullable = false)
    lateinit var bank: Bank
    @Column(name = "bank_id", insertable = false, updatable = false)
    var bankId: Long = 0L
    @ManyToOne
    @JoinColumn(name = "currency_group_id", nullable = false)
    lateinit var currencyGroup: CurrencyGroup
    @Column(name = "currency_group_id", insertable = false, updatable = false)
    var currencyGroupId: Long = 0L
    override fun toString(): String = coin.toString()
}

enum class InstructionType {
    PAY, PAY_IN, PAY_OUT, SETTLEMENT
}

@Entity
@Table(name = "instructions")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@JsonIgnoreProperties("db", "cr")
class Instruction(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id") var id: Long = 0,
        @Column(name = "moment", nullable = false) var moment: LocalTime = LocalTime.MIN,
        @Column(name = "book_id", nullable = true) var bookId: Long? = null,
        @Column(name = "booked", nullable = true) var booked: LocalTime? = null,
        @Enumerated(EnumType.STRING) @Column(name = "instruction_type", nullable = false) var type: InstructionType = InstructionType.PAY,
        @Column(name = "reference", nullable = false) var reference: String = "",
        @Column(name = "amount", nullable = false) var amount: Position = Position.ZERO
) {
    @ManyToOne
    @JoinColumn(name = "db_id", nullable = false)
    lateinit var db: Account
    @Column(name = "db_id", insertable = false, updatable = false)
    var dbId: Long = 0L
    @ManyToOne
    @JoinColumn(name = "cr_id", nullable = false)
    lateinit var cr: Account
    @Column(name = "cr_id", insertable = false, updatable = false)
    var crId: Long = 0L

    val partyIds: List<Long>
        get() = listOf(dbId, crId)

    override fun toString(): String = "$type ${db.name} ${cr.name} $amount"
}
