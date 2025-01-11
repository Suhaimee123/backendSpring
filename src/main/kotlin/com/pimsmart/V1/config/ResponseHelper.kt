package com.pimsmart.V1.config

object ResponseHelper {
    fun createResponse(status: Int, message: String, data: String = ""): Res {
        return Res(status, message, data)
    }
}
