package com.sanastasov.birthdaykata

import arrow.core.computations.either
import java.time.LocalDate

interface Env : EmployeeRepository,
    BirthdayService,
    EmailService

suspend fun main() {
    val env: Env = object : Env,
        EmployeeRepository by FileEmployeeRepository("input.txt"),
        BirthdayService by BirthdayServiceInterpreter(),
        EmailService by SmtpEmailService("localhost", 8080) {}

    env.sendGreetingsUseCase(date = LocalDate.now())

    // TODO UseCase for concurrent processing
    //  unit-of-work: get employee -> create message -> send
}

suspend fun Env.sendGreetingsUseCase(date: LocalDate): Unit {
    val result = either<Throwable, Int> {
        val allEmployees = allEmployees()()
        val greetings = birthdayMessages(allEmployees, date)
        sendGreetings(greetings) // TODO report number of successful emails
        42
    }
    result.fold({ println(it.message) }) { println("sent $it emails successfully") }
}
