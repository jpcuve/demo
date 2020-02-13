package com.messio.demo

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DemoApplication @Autowired constructor(bankRepository: BankRepository) {
	init {
		for (bank in listOf(
				Bank(id = 0, name = "coucou"),
				Bank(id = 1, name = "two")
		)){
			bankRepository.save(bank)
		}
	}
}

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}
