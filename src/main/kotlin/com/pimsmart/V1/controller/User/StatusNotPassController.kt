package com.pimsmart.V1.controller.User

import com.pimsmart.V1.Service.Admin.StudentNotPassedService
import com.pimsmart.V1.config.ApiResponseD
import com.pimsmart.V1.dto.ApprovalRequestDto
import com.pimsmart.V1.repository.Admin.StudentsAdminImgRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/Admin/studentNotPassed")
class StatusNotPassController(

    private val studentNotPassedService: StudentNotPassedService,
    private val studentsAdminImgRepository: StudentsAdminImgRepository,


    ) {

    @PostMapping("/approve/{studentId}")
    fun approveStudent(
        @PathVariable studentId: String,
        @RequestParam applicationId: Int?,
        @RequestParam approve: String,
        @RequestParam approvedBy: String,
        @RequestParam(required = false) appointmentDate: String?
    ): ResponseEntity<ApiResponseD<Unit>> {
        return try {
            // Create the DTO instance and pass it to the service
            val approvalRequestDto = ApprovalRequestDto(
                ApplicationId = applicationId,
                studentId = studentId,
                approve = approve,
                approvedBy = approvedBy,
                appointmentDate = appointmentDate
            )

            // Pass the DTO to the service
            studentNotPassedService.approveStudent(approvalRequestDto)

            // Return a success response
            ResponseEntity.ok(
                ApiResponseD(
                    success = true,
                    message = "Student with ID $studentId approval processed successfully."
                )
            )
        } catch (ex: IllegalArgumentException) {
            // Handle invalid request error
            ResponseEntity.badRequest().body(
                ApiResponseD(
                    success = false,
                    error = ex.message
                )
            )
        } catch (ex: Exception) {
            // Handle unexpected error
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseD(
                    success = false,
                    error = "An unexpected error occurred: ${ex.message}"
                )
            )
        }
    }




    @GetMapping("/students")
    fun getAllStudents(
        @RequestParam("offset") offset: Int,
        @RequestParam("limit") limit: Int,
        @RequestParam("studentId", required = false) studentId: String?
    ): ResponseEntity<Map<String, Any>> {
        val result = studentNotPassedService.getAllStudents(offset, limit, studentId)
        val response = mapOf(
            "success" to true,
            "message" to "Data retrieved successfully",
            "totalCount" to result.totalElements,
            "data" to result.content
        )
        return ResponseEntity.ok(response)
    }

}