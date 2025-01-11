package com.pimsmart.V1.controller.User

import com.pimsmart.V1.Service.User.VolunteerActivityService
import com.pimsmart.V1.config.ApiResponse
import com.pimsmart.V1.config.Res
import com.pimsmart.V1.entities.User.VolunteerActivityEntities

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/volunteer-activities")
class VolunteerActivityController(private val volunteerActivityService: VolunteerActivityService) {

    @GetMapping
    fun getAllVolunteerActivities(): ResponseEntity<List<VolunteerActivityEntities>> {
        val activities = volunteerActivityService.findAll()
        return ResponseEntity(activities, HttpStatus.OK)
    }

    @GetMapping("/{id}")
    fun getVolunteerActivityById(@PathVariable id: Long): ResponseEntity<VolunteerActivityEntities> {
        val activity = volunteerActivityService.findById(id)
        return if (activity != null) {
            ResponseEntity(activity, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @GetMapping("/search/{activityName}")
    fun getVolunteerActivitiesByName(@PathVariable activityName: String): ResponseEntity<List<VolunteerActivityEntities>> {
        val activities = volunteerActivityService.findByActivityName(activityName)
        return if (activities.isNotEmpty()) {
            ResponseEntity(activities, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @GetMapping("/student/{studentId}")
    fun getVolunteerActivitiesByStudentId(@PathVariable studentId: String): ResponseEntity<List<VolunteerActivityEntities>> {
        val activities = volunteerActivityService.findByStudentId(studentId)
        return if (activities.isNotEmpty()) {
            ResponseEntity(activities, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @PostMapping("/create")
    fun addVolunteerActivity(@RequestBody volunteerActivity: VolunteerActivityEntities): ResponseEntity<ApiResponse<VolunteerActivityEntities>> {
        return try {
            val createdActivity = volunteerActivityService.save(volunteerActivity)
            // ส่งค่า success และข้อมูลที่สร้างใหม่กลับไป
            ResponseEntity(
                ApiResponse(
                    success = true,
                    data = createdActivity,
                    message = "Volunteer activity created successfully"
                ),
                HttpStatus.CREATED
            )
        } catch (ex: Exception) {
            // กรณีที่เกิดข้อผิดพลาด
            ResponseEntity(
                ApiResponse(
                    success = false,
                    data = null,
                    error = "Failed to create volunteer activity: ${ex.message}"
                ),
                HttpStatus.INTERNAL_SERVER_ERROR
            )
        }
    }


    @DeleteMapping("/{id}")
    fun deleteVolunteerActivity(@PathVariable id: Long): ResponseEntity<Map<String, String>> {
        return if (volunteerActivityService.findById(id) != null) {
            volunteerActivityService.delete(id)
            val response = mapOf("message" to "Activity with ID $id was successfully deleted")
            ResponseEntity(response, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }



    @PostMapping("/uploadToGoogleDrive")
    fun uploadImagesToGoogleDrive(
        @RequestParam("files") files: List<MultipartFile>,
        @RequestParam("studentId") studentId: String,
        @RequestParam("firstName") firstName: String,
        @RequestParam("imageName") imageName: String,
        @RequestParam("imageType") imageType: String,
        @RequestParam("volunteerId") volunteerId: String


    ): ResponseEntity<List<Res>> {
        val responses = volunteerActivityService.uploadImagesToDrive(files, studentId,firstName,imageName,imageType,volunteerId)
        return ResponseEntity.ok(responses)
    }
}
