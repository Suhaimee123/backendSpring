package com.pimsmart.V1.controller.User

import com.pimsmart.V1.Service.User.RankinService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rankin")
class RankinController(private val rankinService: RankinService) {

    @GetMapping("/activity")
    fun getRankinActivityRanking(@RequestParam(required = false) studentId: String?): ResponseEntity<List<Map<String, Any>>> {
        val ranking = rankinService.getRankinActivityRanking(studentId)
        return if (ranking.isNotEmpty()) {
            ResponseEntity(ranking, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NO_CONTENT)
        }
    }
}
