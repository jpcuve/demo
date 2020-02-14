package com.messio.demo

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.LocalTime
import javax.persistence.*

@Entity
@Table(name = "accounts", uniqueConstraints = [UniqueConstraint(columnNames = ["name", "bank_id"])])
class Account(
        @Id @Column(name = "id") var id: Long,
        @ManyToOne @JoinColumn(name = "bank_id", nullable = false) var bank: Bank,
        @Basic @Column(name = "name", nullable = false) var name: String,
        @Convert(converter = PositionConverter::class) @Column(name = "short_position_limit", nullable = false) var shortPositionLimit: Position
)

@Entity
@Table(name = "banks", uniqueConstraints = [UniqueConstraint(columnNames = ["name"])])
class Bank(
        @Id @Column(name = "id") var id: Long,
        @Basic @Column(name = "name", nullable = false, unique = true) var name: String,
        @Basic @Column(name = "opening", nullable = false) var opening: LocalTime,
        @Basic @Column(name = "closing", nullable = false) var closing: LocalTime,
        @Basic @Column(name = "settlement_completion_target", nullable = false) var settlementCompletionTarget: LocalTime,
        @Convert(converter = PositionConverter::class) @Column(name = "minimum_pay_in", nullable = false) var minimumPayIn: Position
)

@Entity
@Table(name = "currency_groups", uniqueConstraints = [UniqueConstraint(columnNames = ["name"])])
class CurrencyGroup(
        @Id @Column(name = "id") var id: Long,
        @Basic @Column(name = "name", nullable = false, unique = true) var name: String,
        @Basic @Column(name = "priority", nullable = false) var priority: Int
)

@Entity
@Table(name = "currencies", uniqueConstraints = [UniqueConstraint(columnNames = ["coin"])])
@JsonIgnoreProperties("bank", "currencyGroup")
class Currency(
        @Id @Column(name = "id") var id: Long,
        @ManyToOne @JoinColumn(name = "bank_id", nullable = false) var bank: Bank,
        @Basic @Column(name = "bank_id", insertable = false, updatable = false) var bankId: Long,
        @ManyToOne @JoinColumn(name = "currency_group_id", nullable = false) var currencyGroup: CurrencyGroup,
        @Basic @Column(name = "currency_group_id", insertable = false, updatable = false) var currencyGroupId: Long,
        @Enumerated(EnumType.STRING) @Column(name = "coin") var coin: Coin,
        @Basic @Column(name = "opening", nullable = false) var opening: LocalTime,
        @Basic @Column(name = "closing", nullable = false) var closing: LocalTime,
        @Basic @Column(name = "funding_completion_target", nullable = false) var fundingCompletionTarget: LocalTime,
        @Basic @Column(name = "close", nullable = false) var close: LocalTime
)