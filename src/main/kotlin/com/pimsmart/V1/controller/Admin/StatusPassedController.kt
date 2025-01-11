package com.pimsmart.V1.controller.Admin

import com.pimsmart.V1.Service.Admin.StudentPassedService
import com.pimsmart.V1.repository.Admin.StudentsAdminImgRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/Admin/studentPassed")
class StatusPassedController (
    private val studentPassedService: StudentPassedService,
    private val studentsAdminImgRepository: StudentsAdminImgRepository,
){


    @GetMapping("/students")
    fun getAllStudents(
        @RequestParam("offset") offset: Int,
        @RequestParam("limit") limit: Int,
        @RequestParam("studentId", required = false) studentId: String?
    ): ResponseEntity<Map<String, Any>> {
        val result = studentPassedService.getAllStudents(offset, limit, studentId)
        val response = mapOf(
            "success" to true,
            "message" to "Data retrieved successfully",
            "totalCount" to result.totalElements,
            "data" to result.content
        )
        return ResponseEntity.ok(response)
    }



}