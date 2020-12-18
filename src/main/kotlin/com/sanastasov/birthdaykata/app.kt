package com.sanastasov.birthdaykata

import arrow.core.computations.either
import arrow.fx.coroutines.parTraverseEither
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import java.time.LocalDate

interface Env : EmployeeRepository,
    BirthdayService,
    EmailService

// TODO UseCase for concurrent processing
//  unit-of-work: get employee -> create message -> send

suspend fun main() {
    val env: Env = object : Env,
        EmployeeRepository by FileEmployeeRepository("input.txt"),
        BirthdayService by BirthdayServiceInterpreter(),
        EmailService by SmtpEmailService("localhost", 8080) {}
    env.sendGreetingsUseCase(date = LocalDate.now())
}

suspend fun Env.sendGreetingsUseCase(date: LocalDate): Unit {

    val result = either<Throwable, Int> {
        val allEmployees: List<Employee> = allEmployees()()
        val greetings: List<EmailMessage> = birthdayMessages(allEmployees, date)()
        val results: List<String> = greetings.parTraverseEither(Dispatchers.IO) { sendGreeting(it) }()
        results.map { println(it); 1 }.sum()
    }
    result.fold({ println(it.message) }) { println("sent $it emails successfully") }
}

// TODO remove... like either { }
fun testInlineOnObject() {
    val result = something {
        println(abc)
        println("test")
        42
    }
    println(result)
}

object something {
    val abc = "abc"

    inline fun eager(crossinline c: () -> Int): Int {
        println("eager")
        return c() + 1
    }

    inline operator fun invoke(crossinline c: something.() -> Int): Int {
        println("object")
        val i = this.c() + 1
        return i
    }
}
