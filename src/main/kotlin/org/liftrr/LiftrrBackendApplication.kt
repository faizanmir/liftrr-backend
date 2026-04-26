package org.liftrr

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LiftrrBackendApplication

fun main(args: Array<String>) {
	runApplication<LiftrrBackendApplication>(*args)
}
