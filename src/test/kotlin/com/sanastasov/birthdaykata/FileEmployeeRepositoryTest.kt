package com.sanastasov.birthdaykata

import arrow.core.Either
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

/**
 * Integration tests, touches the file system
 */
class FileEmployeeRepositoryTest : StringSpec({

    "all employees are read from a valid CSV file" {
        val sut: EmployeeRepository = FileEmployeeRepository("input.txt")

        val allEmployees: Either<Throwable, List<Employee>> = sut.allEmployees()
        val expectedEmails = listOf("john.doe@foobar.com", "mary.ann@foobar.com").map(::EmailAddress)
        allEmployees.isRight() shouldBe true
        allEmployees.map {
            it.size shouldBe 2
            it.map { it.emailAddress } shouldBe expectedEmails
        }
    }

    "EmployeeRepositoryException when reading an invalid CSV file" {
        val sut: EmployeeRepository = FileEmployeeRepository("invalid_csv_input.txt")

        val exception =
            runCatching { sut.allEmployees() }.exceptionOrNull() as FileEmployeeRepository.EmployeeRepositoryException

        exception.errors.size shouldBe 4
    }
})
