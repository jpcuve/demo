package com.messio.demo

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@TestPropertySource(locations = ["classpath:application-test.yaml"])
class DemoApplicationTests @Autowired constructor(val facade: Facade) {

	@Test
	fun contextLoads() {
	}

	@Test
	fun testBanks(){
		facade.bankRepository.findAll().forEach {
			print(it ?: "coucou")
		}
	}

}
