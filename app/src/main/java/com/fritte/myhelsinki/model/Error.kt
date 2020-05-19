package com.fritte.myhelsinki.model

class Error(val code: Int, val message: String?) {
    companion object {
        const val ERROR_INTERNAL: Int = 400
        const val ERROR_CONNECTION: Int = 401
    }
}