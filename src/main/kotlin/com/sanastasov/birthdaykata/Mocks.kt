package com.sanastasov.birthdaykata

import arrow.core.Either
import java.io.IOException

class FailingFileEmployeeRepository : EmployeeRepository {
    override suspend fun allEmployees(): Either<Throwable, List<Employee>> {
        return Either.catch {
            // some actual IO here...
            throw IOException("outage !")
        }
    }
}
