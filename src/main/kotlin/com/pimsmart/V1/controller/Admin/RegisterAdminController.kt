package com.pimsmart.V1.controller.Admin

import com.pimsmart.V1.Service.Admin.RegisterAdminService
import com.pimsmart.V1.config.ApiResponse
import com.pimsmart.V1.config.ApiResponseD
import com.pimsmart.V1.dto.ImageDataResponseDto
import com.pimsmart.V1.dto.StudentResponseDto
import com.pimsmart.V1.entities.User.StudentEntities
import com.pimsmart.V1.repository.Admin.StudentsAdminImgRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*



@RestController
@RequestMapping("/Admin/student")
class RegisterAdminController(
    private val registerAdminService: RegisterAdminService,
    private val studentsAdminImgRepository: StudentsAdminImgRepository,

    ) {


    @DeleteMapping("/ID/{Id}")
    fun deleteStudent(@PathVariable("Id") ApplicationId: Int): ResponseEntity<ApiResponseD<Void>> {
        return if (registerAdminService.deleteStudent(ApplicationId)) {
            ResponseEntity.ok(
                ApiResponseD(
                    success = true,
                    message = "Student deleted successfully"
                )
            )
        } else {
            ResponseEntity.ok(
                ApiResponseD(
                    success = false,
                    error = "Student not found"
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
        val result = registerAdminService.getAllStudents(offset, limit, studentId)

        val response = mapOf(
            "success" to true,
            "message" to "Data retrieved successfully",
            "totalCount" to result.totalElements,
            "data" to result.content // This now contains StudentApplicationHistoryDTO objects
        )
        return ResponseEntity.ok(response)
    }
















    @PostMapping("/approve/{studentId}")
    fun handleApproval(
        @PathVariable studentId: String,
        @RequestParam applicationId: Int?,
        @RequestParam approve: String,
        @RequestParam approvedBy: String,
        @RequestParam(required = false) appointmentDate: String?,
        @RequestParam(required = false) startMonth: String?,
        @RequestParam(required = false) endMonth: String?
    ): ResponseEntity<ApiResponse<String>> {
        return try {
            if (approve != "Y" && approve != "N") {
                throw IllegalArgumentException("Invalid approve value. Use 'Y' for approval or 'N' for rejection.")
            }

            // Call the service with the new parameters for multi-stage approval
            registerAdminService.handleStudentApproval(studentId, applicationId, approve, approvedBy, appointmentDate, startMonth, endMonth)

            // Set message based on the approval decision
            val message = if (approve == "Y") {
                "Student application processed successfully"
            } else {
                "Student application rejected successfully"
            }

            val response = ApiResponse(
                success = true,
                data = message,
                message = "Operation completed"
            )
            ResponseEntity.ok(response)

        } catch (e: IllegalArgumentException) {
            val response = ApiResponse<String>(
                success = false,
                data = null,
                message = e.message,
                error = "Invalid request"
            )
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)

        } catch (e: Exception) {
            val response = ApiResponse<String>(
                success = false,
                data = null,
                message = "Internal server error",
                error = e.message
            )
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
        }
    }



    @PostMapping("/register")
    fun registerStudent(@RequestBody student: StudentEntities): ResponseEntity<ApiResponse<StudentResponseDto>> {
        return try {
            val (savedStudent, isNew) = registerAdminService.registerAdminStudent(student)
            val message = if (isNew) "Student registered successfully" else "Student updated successfully"

            val studentId = savedStudent.id ?: throw IllegalArgumentException("Student ID cannot be null after saving.")

            val responseData = StudentResponseDto(
                id = studentId,
                studentId = savedStudent.studentId,
                firstName = savedStudent.firstName,
                email = savedStudent.email
            )

            val response = ApiResponse(
                success = true,
                data = responseData,
                message = message
            )
            ResponseEntity.ok(response)
        } catch (e: IllegalArgumentException) {
            val response = ApiResponse<StudentResponseDto>(
                success = false,
                data = null,
                message = e.message,
                error = "Conflict: Student already exists"
            )
            ResponseEntity.status(HttpStatus.CONFLICT).body(response)
        } catch (e: Exception) {
            println("Error during registration: ${e.message}")
            val response = ApiResponse<StudentResponseDto>(
                success = false,
                data = null,
                message = "Internal server error",
                error = e.message
            )
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
        }
    }
















    @DeleteMapping("/deleteFile")
    fun deleteFile(
        @RequestParam("imageData") imageData: String
    ): ResponseEntity<ApiResponse<Void>> {
        val response = registerAdminService.deleteFileFromDriveAndDatabase(imageData)
        return ResponseEntity.ok(response)
    }






    @GetMapping("/downloadAllFiles")
    fun downloadAllFiles(
        @RequestParam("studentId") studentId: String,
        @RequestParam("imageType") imageType: String // Accept imageType as a request parameter
    ): ResponseEntity<ApiResponse<List<ImageDataResponseDto>>> {
        return try {
            // Pass the imageType to the service method
            val response = registerAdminService.downloadFilesByStudentId(studentId, imageType)
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse(success = false, data = null, error = "Error downloading files: ${e.message}"))
        }
    }











    @GetMapping("/studentsimg/{studentId}")
    fun getStudentImage(@PathVariable studentId: String): ResponseEntity<Map<String, Any>> {
        val images = studentsAdminImgRepository.findByStudentId(studentId)
        val response = mapOf(
            "success" to true,
            "message" to if (images.isNotEmpty()) "Images found" else "No images found",
            "data" to images
        )
        return ResponseEntity.ok(response)
    }
















    @GetMapping("/{id}")
    fun getStudentById(@PathVariable id: Int): ResponseEntity<StudentEntities> {
        val student = registerAdminService.getStudentById(id)
        return if (student != null) {
            ResponseEntity.ok(student)
        } else {
            ResponseEntity.notFound().build()
        }
    }



    @GetMapping("/studentId/{id}")
    fun getStudentByStudentId(@PathVariable id: String): ResponseEntity<StudentEntities> {
        val student = registerAdminService.getStudentByStudentId(id)
        return if (student != null) {
            ResponseEntity.ok(student)
        } else {
            ResponseEntity.notFound().build()
        }
    }














}
