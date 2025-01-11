package com.pimsmart.V1.controller.Admin

import com.pimsmart.V1.Service.Admin.SpecialWorkAdminService
import com.pimsmart.V1.config.ApiResponse
import com.pimsmart.V1.config.ApiResponseD

import com.pimsmart.V1.dto.ImageDataResponseDto2
import com.pimsmart.V1.entities.User.SpecialWorkEntity

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/Admin/specialWorkAdmin")
class SpecialWorkAdminController(
    private val specialWorkAdminService: SpecialWorkAdminService
) {

    // Delete a Special Work record by its ID
    @DeleteMapping("/{id}")
    fun deleteSpecialWork(@PathVariable id: Long): ResponseEntity<ApiResponseD<Void>> {
        val activity = specialWorkAdminService.findById(id)
        return  if (activity != null) {
            specialWorkAdminService.delete(id)
            ResponseEntity.ok(
                ApiResponseD(
                    success = true,
                    message = "Activity with ID $id and related images were successfully deleted"
                )
            )
        } else {
            ResponseEntity.ok(
                ApiResponseD(
                    success = false,
                    error = "Activity with ID $id not found"
                )
            )
        }
    }

    @DeleteMapping("/deleteFile")
    fun deleteFile(
        @RequestParam("imageData") imageData: String
    ): ResponseEntity<ApiResponse<Void>> {
        val response = specialWorkAdminService.deleteFileFromDriveAndDatabase(imageData)
        return ResponseEntity.ok(response)
    }

    // Fetch all Special Work records
    @GetMapping
    fun getAllSpecialWork(): ResponseEntity<List<SpecialWorkEntity>> {
        val works = specialWorkAdminService.findAll()
        return ResponseEntity(works, HttpStatus.OK)
    }

    // Fetch a specific Special Work record by its ID
    @GetMapping("/{id}")
    fun getSpecialWorkById(@PathVariable id: Long): ResponseEntity<SpecialWorkEntity> {
        val work = specialWorkAdminService.findById(id)
        return if (work != null) {
            ResponseEntity(work, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    // Fetch Special Work records by the activity name
    @GetMapping("/search/{activityName}")
    fun getSpecialWorkActivitiesByName(@PathVariable activityName: String): ResponseEntity<List<SpecialWorkEntity>> {
        val activities = specialWorkAdminService.findByActivityName(activityName)
        return if (activities.isNotEmpty()) {
            ResponseEntity(activities, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    // Fetch Special Work records by the student's ID
    @GetMapping("/student/{studentId}")
    fun getSpecialWorkActivitiesByStudentId(@PathVariable studentId: String): ResponseEntity<List<SpecialWorkEntity>> {
        val activities = specialWorkAdminService.findByStudentId(studentId)
        return if (activities.isNotEmpty()) {
            ResponseEntity(activities, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    // Fetch all students' special work with pagination
    @GetMapping("/All")
    fun getAllStudents(
        @RequestParam("offset") offset: Int,
        @RequestParam("limit") limit: Int,
        @RequestParam("studentId", required = false) studentId: String?
    ): ResponseEntity<Map<String, Any>> {
        val result = specialWorkAdminService.getAllStudents(offset, limit, studentId)
        val response = mapOf(
            "success" to true,
            "message" to "Data retrieved successfully",
            "totalCount" to result.totalElements,
            "data" to result.content
        )
        return ResponseEntity.ok(response)
    }

    // Create a new Special Work record
    @PostMapping("/create")
    fun createSpecialWork(@RequestBody specialWork: SpecialWorkEntity): ResponseEntity<SpecialWorkEntity> {
        val createdWork = specialWorkAdminService.save(specialWork)
        return ResponseEntity(createdWork, HttpStatus.CREATED) // Return the created entity with HTTP 201 status
    }

    // Download all files related to a specific special work record
    @GetMapping("/downloadAllFiles")
    fun downloadAllFiles(
        @RequestParam("specialworkId") specialworkId: String,
        @RequestParam("imageType") imageType: String
    ): ResponseEntity<ApiResponse<List<ImageDataResponseDto2>>> {
        return try {
            val response = specialWorkAdminService.downloadFilesByStudentId(specialworkId, imageType)
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse(success = false, data = null, error = "Error downloading files: ${e.message}"))
        }
    }

    // Delete a specific file from the system

}
