package com.pimsmart.V1.controller.User

import com.pimsmart.V1.Service.User.SpecialWorkService
import com.pimsmart.V1.config.ApiResponse
import com.pimsmart.V1.config.Res
import com.pimsmart.V1.entities.User.SpecialWorkEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/special-work")
class SpecialWorkController(
    private val specialWorkService: SpecialWorkService,
) {

    // Fetch all Special Work records
    @GetMapping
    fun getAllSpecialWork(): ResponseEntity<List<SpecialWorkEntity>> {
        val works = specialWorkService.findAll()
        return ResponseEntity(works, HttpStatus.OK)
    }

    // Fetch a specific Special Work record by its ID
    @GetMapping("/{id}")
    fun getSpecialWorkById(@PathVariable id: Long): ResponseEntity<SpecialWorkEntity> {
        val work = specialWorkService.findById(id)
        return if (work != null) {
            ResponseEntity(work, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    // Fetch Special Work records by the activity name
    @GetMapping("/activity/{activityName}")
    fun getSpecialWorkByActivityName(@PathVariable activityName: String): ResponseEntity<List<SpecialWorkEntity>> {
        val works = specialWorkService.findByActivityName(activityName)
        return if (works.isNotEmpty()) {
            ResponseEntity(works, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    // Fetch Special Work records by the student's ID
    @GetMapping("/student/{studentId}")
    fun getSpecialWorkByStudentId(@PathVariable studentId: String): ResponseEntity<List<SpecialWorkEntity>> {
        val works = specialWorkService.findByStudentId(studentId)
        return if (works.isNotEmpty()) {
            ResponseEntity(works, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    // Create a new Special Work record
    @PostMapping("/create")
    fun createSpecialWork(@RequestBody specialWork: SpecialWorkEntity): ResponseEntity<ApiResponse<SpecialWorkEntity>> {
        return try {
            val createdWork = specialWorkService.save(specialWork)
            val response = ApiResponse(
                success = true,
                data = createdWork,
                message = "Special work created successfully."
            )
            ResponseEntity(response, HttpStatus.CREATED) // ส่งกลับ HTTP 201 พร้อม ApiResponse
        } catch (e: Exception) {
            val errorResponse = ApiResponse<SpecialWorkEntity>(
                success = false,
                data = null,
                error = e.message
            )
            ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR) // ส่งกลับ HTTP 500 ในกรณีเกิดข้อผิดพลาด
        }
    }


    // Delete a Special Work record by its ID
    @DeleteMapping("/{id}")
    fun deleteSpecialWork(@PathVariable id: Long): ResponseEntity<Map<String, String>> {
        return if (specialWorkService.findById(id) != null) {
            specialWorkService.delete(id)
            val response = mapOf("message" to "Special Work with ID $id was successfully deleted")
            ResponseEntity(response, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    // Upload images to Google Drive
    @PostMapping("/uploadToGoogleDrive")
    fun uploadImagesToGoogleDrive(
        @RequestParam("files") files: List<MultipartFile>,
        @RequestParam("studentId") studentId: String,
        @RequestParam("firstName") firstName: String,
        @RequestParam("imageName") imageName: String,
        @RequestParam("imageType") imageType: String,
        @RequestParam("specialworkId") specialworkId: String

    ): ResponseEntity<List<Res>> {
        val responses = specialWorkService.uploadImagesToDrive(files, studentId, firstName, imageName, imageType,specialworkId)
        return ResponseEntity.ok(responses)
    }
}
