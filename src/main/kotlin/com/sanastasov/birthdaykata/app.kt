package com.sanastasov.birthdaykata

import arrow.fx.IO
import arrow.fx.extensions.fx
import java.time.LocalDate

interface Env : EmployeeRepository,
    BirthdayService,
    EmailService,
    LoggingService

suspend fun main() {
    val env: Env = object : Env,
        EmployeeRepository by FileEmployeeRepository("input.txt"),
        BirthdayService by BirthdayServiceInterpreter(),
        EmailService by SmtpEmailService("localhost", 8080),
        LoggingService by KotlinLoggingLogger() {}

    env.sendGreetingsUseCase(date = LocalDate.now()).suspended()
}

fun Env.sendGreetingsUseCase(date: LocalDate): IO<Unit> = IO.fx {
    val allEmployees = allEmployees()
        .logError { "Error reading employees from repo" }
        .bind()
    val greetings = birthdayMessages(allEmployees, date)
    sendGreetings(greetings)
        .logError { "Error sending emails. Greetings count ${greetings.size} on date: $date" }
        .bind()
    Unit
}