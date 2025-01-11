package com.pimsmart.V1.controller.Admin

import com.pimsmart.V1.Service.Admin.ScholarshipAdminService
import com.pimsmart.V1.config.ApiResponse
import com.pimsmart.V1.config.ApiResponseD
import com.pimsmart.V1.config.Res
import com.pimsmart.V1.dto.ImageDataResponseDto
import com.pimsmart.V1.entities.Admin.ScholarshipEntities
import jakarta.persistence.EntityNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime




@RestController
class ScholarshipAdminController(
    private val scholarshipAdminService: ScholarshipAdminService
) {


    @GetMapping("/Admin/scholarship/All")
    fun getAllStudentsWithHours(
        @RequestParam("offset") offset: Int,
        @RequestParam("limit") limit: Int,
        @RequestParam("studentId", required = false) studentId: String?
    ): ResponseEntity<Map<String, Any>> {
        val pageResult = scholarshipAdminService.getAllStudentsWithVolunteerHours(offset, limit, studentId)
        val responseData = pageResult.content.map {
            mapOf(
                "id" to it.id,
                "studentId" to it.studentId,
                "firstName" to it.firstName,
                "imageName" to it.imageName,
                "imageData" to it.imageData,
                "createDate" to it.createDate.toString(),
                "imageType" to it.imageType,
                "totalVolunteerHours" to it.totalVolunteerHours
            )
        }
        val response = mapOf(
            "success" to true,
            "message" to "Data retrieved successfully",
            "totalCount" to pageResult.totalElements,
            "data" to responseData
        )
        return ResponseEntity.ok(response)
    }


    @DeleteMapping("/Admin/scholarship/delete/{id}")
    fun deletePost(@PathVariable("id") id: Int): ResponseEntity<ApiResponseD<Void>> {
        return try {
            // เรียกใช้ method delete จาก service
            val result = scholarshipAdminService.delete(id)

            // ตรวจสอบว่าผลลัพธ์เป็น null (หมายความว่าไม่พบข้อมูล)
            return if (result == null) {
                // ถ้าไม่มีข้อมูลหรือไม่พบรูปภาพ ให้ส่ง HTTP Status 204 (No Content)
                ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponseD(success = true, message = "Scholarship not found", error = null))
            } else {
                // ถ้าลบสำเร็จ, ให้ส่งข้อความสำเร็จ
                ResponseEntity.ok(ApiResponseD(success = true, message = result, error = null))
            }
        } catch (e: Exception) {
            // ถ้ามีข้อผิดพลาด, ส่งข้อมูล error
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseD(success = false, message = null, error = "An error occurred"))
        }
    }





    @PostMapping("/scholarship/uploadToGoogleDrive")
    fun uploadImagesToGoogleDrive(
        @RequestParam("files") files: List<MultipartFile>,
        @RequestParam("studentId") studentId: String,
        @RequestParam("firstName") firstName: String,
        @RequestParam("imageName") imageName: String,
        @RequestParam("imageType") imageType: String

    ): ResponseEntity<List<Res>> {
        val responses = scholarshipAdminService.uploadImagesToDrive(files, studentId,firstName,imageName,imageType)
        return ResponseEntity.ok(responses)
    }



    @GetMapping("/scholarship")
    fun getLatestScholarship(): ResponseEntity<ApiResponse<ScholarshipEntities>> {
        val latestScholarship = scholarshipAdminService.getLatestScholarship()
        return if (latestScholarship != null) {
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    data = latestScholarship,
                    message = "Latest scholarship retrieved successfully."
                )
            )
        } else {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    data = null,
                    error = "No scholarship data found."
                )
            )
        }
    }

    @PostMapping("/Admin/scholarship/manage")
    fun manageScholarship(
        @RequestParam("id", required = false) id: Int?,
        @RequestParam("action") action: String,
        @RequestParam("startDate", required = false) startDate: String?,
        @RequestParam("endDate", required = false) endDate: String?
    ): ResponseEntity<ApiResponseD<Any>> {
        return try {
            when (action.lowercase()) {
                "open" -> {
                    if (startDate != null && endDate != null) {
                        val success = if (id != null) {
                            // Open an existing scholarship
                            scholarshipAdminService.manageScholarship(
                                id = id,
                                action = "open",
                                startDate = LocalDateTime.parse(startDate),
                                endDate = LocalDateTime.parse(endDate)
                            )
                        } else {
                            // Create a new scholarship
                            scholarshipAdminService.createScholarship(
                                startDate = LocalDateTime.parse(startDate),
                                endDate = LocalDateTime.parse(endDate)
                            )
                        }
                        if (success) {
                            ResponseEntity.ok(ApiResponseD(success = true, message = "Scholarship is now open!"))
                        } else {
                            throw IllegalStateException("Failed to open scholarship.")
                        }
                    } else {
                        throw IllegalArgumentException("StartDate and EndDate are required for opening.")
                    }
                }
                "close" -> {
                    if (id != null) {
                        val success = scholarshipAdminService.manageScholarship(id = id, action = "close")
                        if (success) {
                            ResponseEntity.ok(ApiResponseD(success = true, message = "Scholarship with ID $id is now closed!"))
                        } else {
                            throw IllegalStateException("Failed to close scholarship with ID $id.")
                        }
                    } else {
                        throw IllegalArgumentException("ID is required to close a scholarship.")
                    }
                }
                else -> throw IllegalArgumentException("Invalid action. Use 'open' or 'close'.")
            }
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(ApiResponseD(success = false, error = e.message))
        } catch (e: IllegalStateException) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseD(success = false, error = e.message)
            )
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseD(success = false, error = "An unexpected error occurred: ${e.message}")
            )
        }
    }



}
