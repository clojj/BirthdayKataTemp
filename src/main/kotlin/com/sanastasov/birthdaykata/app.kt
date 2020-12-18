package com.sanastasov.birthdaykata

import arrow.core.computations.either
import arrow.fx.coroutines.parTraverseEither
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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

    delay(4000)
}

suspend fun Env.sendGreetingsUseCase(date: LocalDate): Unit {

    val result = either<Throwable, Int> {
        val allEmployees: List<Employee> = allEmployees()()

        // TODO return Either
        val greetings: List<EmailMessage> = birthdayMessages(allEmployees, date)()

        // TODO parMapN for sending
        // TODO show each success message
        val results: List<String> = greetings.parTraverseEither(Dispatchers.IO) {
            sendGreeting(it)
        }()

        results.map { println(it); 1 }.sum()
    }
    result.fold({ println(it.message) }) { println("sent $it emails successfully") }
}

// remove
fun test() {
    // test syntax
    val result = something {
        println(abc)
        println("test")
        42
    }

    println(result)
}

object something {
    val abc = "ABC"

    inline fun eager(crossinline c: () -> Int): Int {
        println("eager")
        return c() + 1
    }

    inline operator fun invoke(crossinline c: something.() -> Int): Int {
        println("object")
        val i = this.c() * 100
        return i
    }
}
