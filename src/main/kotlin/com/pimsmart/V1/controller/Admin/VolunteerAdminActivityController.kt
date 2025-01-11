package com.pimsmart.V1.controller.Admin

import com.pimsmart.V1.Service.Admin.VolunteerAdminActivityService
import com.pimsmart.V1.config.ApiResponse
import com.pimsmart.V1.config.ApiResponseD
import com.pimsmart.V1.dto.ImageDataResponseDto1
import com.pimsmart.V1.entities.User.VolunteerActivityEntities

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/Admin/volunteerAdminActivities")
class VolunteerAdminActivityController(private val volunteerAdminActivityService: VolunteerAdminActivityService) {

    @DeleteMapping("/{id}")
    fun deleteVolunteerActivity(@PathVariable id: Long): ResponseEntity<ApiResponseD<Void>> {
        val activity = volunteerAdminActivityService.findById(id)
        return if (activity != null) {
            volunteerAdminActivityService.delete(id)
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
        val response = volunteerAdminActivityService.deleteFileFromDriveAndDatabase(imageData)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/downloadAllFiles")
    fun downloadAllFiles(
        @RequestParam("volunteerId") volunteerId: String,
        @RequestParam("imageType") imageType: String // Accept imageType as a request parameter
    ): ResponseEntity<ApiResponse<List<ImageDataResponseDto1>>> {
        return try {
            // Pass the imageType to the service method
            val response = volunteerAdminActivityService.downloadFilesByStudentId(volunteerId, imageType)
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse(success = false, data = null, error = "Error downloading files: ${e.message}"))
        }
    }


    @GetMapping
    fun getAllVolunteerActivities(): ResponseEntity<List<VolunteerActivityEntities>> {
        val activities = volunteerAdminActivityService.findAll()
        return ResponseEntity(activities, HttpStatus.OK)
    }

    @GetMapping("/{id}")
    fun getVolunteerActivityById(@PathVariable id: Long): ResponseEntity<VolunteerActivityEntities> {
        val activity = volunteerAdminActivityService.findById(id)
        return if (activity != null) {
            ResponseEntity(activity, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @GetMapping("/search/{activityName}")
    fun getVolunteerActivitiesByName(@PathVariable activityName: String): ResponseEntity<List<VolunteerActivityEntities>> {
        val activities = volunteerAdminActivityService.findByActivityName(activityName)
        return if (activities.isNotEmpty()) {
            ResponseEntity(activities, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @GetMapping("/student/{studentId}")
    fun getVolunteerActivitiesByStudentId(@PathVariable studentId: String): ResponseEntity<List<VolunteerActivityEntities>> {
        val activities = volunteerAdminActivityService.findByStudentId(studentId)
        return if (activities.isNotEmpty()) {
            ResponseEntity(activities, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @GetMapping("/All")
    fun getAllStudents(
        @RequestParam("offset") offset: Int,
        @RequestParam("limit") limit: Int,
        @RequestParam("studentId", required = false) studentId: String?
    ): ResponseEntity<Map<String, Any>> {
        val result = volunteerAdminActivityService.getAllStudents(offset, limit,studentId)
        val response = mapOf(
            "success" to true,
            "message" to "Data retrieved successfully",
            "totalCount" to result.totalElements,
            "data" to result.content
        )
        return ResponseEntity.ok(response)
    }



}
