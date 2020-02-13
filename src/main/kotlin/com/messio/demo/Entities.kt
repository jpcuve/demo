package com.messio.demo

import java.time.LocalTime
import javax.persistence.*

@Entity
@Table(name = "accounts", uniqueConstraints = [UniqueConstraint(columnNames = ["name", "bank_id"])])
class Account(
        @Id @Column(name = "id") var id: Long,
        @Basic @Column(name = "name", nullable = false) var name: String,
        @ManyToOne @JoinColumn(name = "bank_id", nullable = false) var bank: Bank
)

@Entity
@Table(name = "banks", uniqueConstraints = [UniqueConstraint(columnNames = ["name"])])
class Bank(
        @Id @Column(name = "id") var id: Long,
        @Basic @Column(name = "name", nullable = false, unique = true) var name: String,
        @Basic @Column(name = "opening", nullable = false) var opening: LocalTime = LocalTime.of(8, 0),
        @Basic @Column(name = "closing", nullable = false) var closing: LocalTime = LocalTime.of(17, 0),
        @Convert(converter = PositionConverter::class) @Column(name = "minimum_pay_in", nullable = false) var minimumPayIn: Position = Position.ZERO
)