package com.pimsmart.V1.Service.User

interface RankinService {

    fun getRankinActivityRanking(studentId: String?): List<Map<String, Any>>
}
