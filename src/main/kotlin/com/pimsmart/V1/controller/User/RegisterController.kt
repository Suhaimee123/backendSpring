package com.pimsmart.V1.controller.User

import com.pimsmart.V1.config.ApiResponse
import com.pimsmart.V1.config.Res
import com.pimsmart.V1.dto.StudentDto
import com.pimsmart.V1.entities.User.RequestEntities
import com.pimsmart.V1.Service.User.RegisterService
import com.pimsmart.V1.dto.StudentResponseDto
import com.pimsmart.V1.entities.User.ApplicationHistoryEntities
import com.pimsmart.V1.entities.User.StudentEntities
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile


@RestController
@RequestMapping("/students")
class RegisterController(private val registerService: RegisterService) {


    @GetMapping("/status/{studentId}")
    fun getApplicationStatus(@PathVariable studentId: String): ResponseEntity<ApiResponse<List<ApplicationHistoryEntities>>> {
        val statusList = registerService.getApplicationStatus(studentId)
        return if (statusList.isNotEmpty()) {
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    data = statusList,
                    message = "Status fetched successfully"
                )
            )
        } else {
            // ส่ง HttpStatus.OK พร้อมข้อความว่าไม่พบข้อมูล
            ResponseEntity.ok(
                ApiResponse(
                    success = false,
                    data = null,
                    message = "No status found for the given studentId",
                    error = "No Data Found"
                )
            )
        }
    }







    @PostMapping("/request")
    fun createRequest(@RequestBody request: RequestEntities): ResponseEntity<ApiResponse<String?>> {
        return try {
            val savedRequest = registerService.saveRequest(request)

            // ถ้าบันทึกสำเร็จ ให้ส่งสถานะ CREATED กลับไปพร้อมข้อมูล ID ของคำร้อง
            val response = ApiResponse<String?>(
                success = true,
                message = "Request saved successfully",
                data = savedRequest.id?.toString(),
                error = null
            )
            ResponseEntity.status(HttpStatus.CREATED).body(response)

        } catch (e: IllegalArgumentException) {
            // จัดการกรณีคำร้องซ้ำโดยส่งสถานะ CONFLICT
            val conflictResponse = ApiResponse<String?>(
                success = false,
                message = "Request conflict",
                data = null,
                error = "Conflict: Duplicate request"
            )
            ResponseEntity.status(HttpStatus.CONFLICT).body(conflictResponse)
        }
    }




    @PostMapping("/register")
    fun registerStudent(@RequestBody student: StudentEntities): ResponseEntity<ApiResponse<StudentResponseDto>> {
        return try {
            val savedStudent = registerService.registerStudent(student)

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
                message = "Student registered successfully"
            )
            ResponseEntity.ok(response)
        } catch (e: IllegalArgumentException) {
            // Return 409 Conflict if the student already exists
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

    @PostMapping("/SendEmailWithAttachment")
    fun sendEmailWithAttachment(
        @RequestParam studentId: String,
        @RequestParam firstName: String,
        @RequestParam email: String
    ): ResponseEntity<ApiResponse<String>> {
        val response = registerService.sendStudentEmailWithAttachment(studentId, firstName, email)
        return ResponseEntity.ok(response)
    }








    @GetMapping("/search")
    fun getStudentByStudentIdAndFirstName(
        @RequestParam studentId: String,
        @RequestParam firstName: String
    ): ResponseEntity<ApiResponse<StudentDto?>> {
        val student = registerService.getStudentByStudentIdAndFirstName(studentId, firstName)

        return if (student != null) {
            // สร้าง StudentDto จากข้อมูลที่ไม่เป็น null
            val studentDto = StudentDto(studentId = student.studentId, firstName = student.firstName)
            ResponseEntity.ok(
                ApiResponse(
                success = true,
                message = "Student found",
                data = studentDto,
                error = null
            )
            )
        } else {
            if (registerService.existsByStudentId(studentId)) {
                // ถ้ามี studentId แต่ firstName ไม่ตรง
                ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body(
                        ApiResponse(
                        success = true,
                        message = "StudentId exists but firstName does not match",
                        data = null,
                        error = "Student name mismatch"
                    )
                    )
            } else {
                ResponseEntity.status(HttpStatus.ALREADY_REPORTED)
                    .body(
                        ApiResponse(
                            success = false,
                            message = "No student found or studentId is duplicate",
                            data = null,
                            error = "Student not found"
                        )
                    )

            }
        }
    }












    @GetMapping("/{id}")
    fun getStudentById(@PathVariable id: Int): ResponseEntity<StudentEntities> {
        val student = registerService.getStudentById(id)
        return if (student != null) {
            ResponseEntity.ok(student)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/All")
    fun getAllStudents(): ResponseEntity<List<StudentEntities>> {
        val students = registerService.getAllStudents()
        return ResponseEntity.ok(students)
    }

    @GetMapping("/studentId/{id}")
    fun getStudentByStudentId(@PathVariable id: String): ResponseEntity<StudentEntities> {
        val student = registerService.getStudentByStudentId(id)
        return if (student != null) {
            ResponseEntity.ok(student)
        } else {
            ResponseEntity.notFound().build()
        }
    }





    @DeleteMapping("/{id}")
    fun deleteStudent(@PathVariable id: Int): ResponseEntity<Void> {
        return if (registerService.deleteStudent(id)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }



    @PostMapping("/uploadToGoogleDrive")
    fun uploadImagesToGoogleDrive(
        @RequestParam("files") files: List<MultipartFile>,
        @RequestParam("id") registerId: Long,
        @RequestParam("studentId") studentId: String,
        @RequestParam("firstName") firstName: String,
        @RequestParam("imageName") imageName: String,
        @RequestParam("imageType") imageType: String

        ): ResponseEntity<List<Res>> {
        val responses = registerService.uploadImagesToDrive(files,registerId, studentId,firstName,imageName,imageType)
        return ResponseEntity.ok(responses)
    }


}
