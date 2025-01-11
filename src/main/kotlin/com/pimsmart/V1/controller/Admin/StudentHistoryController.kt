package com.pimsmart.V1.controller.Admin

import com.pimsmart.V1.Service.Admin.StudentHistoryService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/Admin/StudentHistory")
class StudentHistoryController (
    private val studentHistoryService: StudentHistoryService,
){




    @GetMapping("/students")
    fun getAllStudents(
        @RequestParam("offset") offset: Int,
        @RequestParam("limit") limit: Int,
        @RequestParam("studentId", required = false) studentId: String?
    ): ResponseEntity<Map<String, Any>> {
        val result = studentHistoryService.getAllStudents(offset, limit,studentId)
        val response = mapOf(
            "success" to true,
            "message" to "Data retrieved successfully",
            "totalCount" to result.totalElements,
            "data" to result.content
        )
        return ResponseEntity.ok(response)
    }


}