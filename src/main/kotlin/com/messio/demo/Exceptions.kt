package com.messio.demo

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*


class CustomException(message: String = "Unspecified") : RuntimeException(message) { // constructors
}

@RestControllerAdvice
class ErrorHandler {
    @ExceptionHandler(CustomException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleCustomException(ce: CustomException): Map<String, String> {
        return mapOf("error" to (ce.message ?: "Unspecified"))
    }
}

@RestController
class ErrorController {

    @GetMapping("/test-error")
    @Throws(CustomException::class)
    fun error() {
        throw CustomException("Some error has happened")
    }
}