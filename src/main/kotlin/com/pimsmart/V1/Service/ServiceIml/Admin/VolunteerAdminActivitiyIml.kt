package com.pimsmart.V1.Service.ServiceIml.Admin

import com.pimsmart.V1.Service.Admin.VolunteerAdminActivityService

import com.pimsmart.V1.config.*
import com.pimsmart.V1.dto.ImageDataResponseDto1
import com.pimsmart.V1.entities.User.VolunteerActivityEntities
import com.pimsmart.V1.repository.Admin.VolunteerAdminActivityRepository
import com.pimsmart.V1.repository.Admin.VolunteerAdminImgRepository
import jakarta.transaction.Transactional

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException





@Service
class VolunteerAdminActivitiyIml(
    private val volunteerAdminActivityRepository: VolunteerAdminActivityRepository,
    private val googleDriveHelper: GoogleDriveHelper,
    private val appConfig: AppConfig,
    private val volunteerAdminImgRepository: VolunteerAdminImgRepository
) : VolunteerAdminActivityService {

    override fun findById(id: Long): VolunteerActivityEntities? {
        return volunteerAdminActivityRepository.findById(id).orElse(null)
    }

    @Transactional
    override fun delete(id: Long) {
        val activity = volunteerAdminActivityRepository.findById(id).orElse(null)
        if (activity != null) {
            // ดึงรายการรูปภาพที่เกี่ยวข้องกับ activity
            val images = volunteerAdminImgRepository.findAllByVolunteerId(id.toString())

            if (images.isEmpty()) {
                // พิมพ์ข้อความเมื่อไม่มีข้อมูลใน database
                println("No image found in VolunteerImgEntities for Volunteer ID: $id")
            } else {
                // ลบไฟล์แต่ละไฟล์ออกจาก Google Drive
                images.forEach { image ->
                    image.imageData?.let { googleDriveHelper.deleteFile(it) }
                }

                // ลบข้อมูลในตาราง volunteeractivity_img
                volunteerAdminImgRepository.deleteByVolunteerId(id.toString())
            }

            // ลบข้อมูลในตาราง volunteeractivity
            volunteerAdminActivityRepository.deleteById(id)
        } else {
            throw RuntimeException("Activity with ID $id not found")
        }
    }



    @Transactional
    override fun deleteFileFromDriveAndDatabase(imageData: String): ApiResponse<Void> {
        return try {
            // Delete the file from Google Drive
            googleDriveHelper.deleteFile(imageData)

            // Delete the file record from the database
            val deletedCount = volunteerAdminImgRepository.deleteByImageData(imageData)

            if (deletedCount > 0) {
                ApiResponse(success = true, data = null, message = "File with imageData $imageData successfully deleted from Google Drive and database")
            } else {
                ApiResponse(success = false, data = null, message = "File with imageData $imageData not found in the database")
            }
        } catch (e: Exception) {
            ApiResponse(success = false, data = null, message = null, error = "Error deleting file with imageData $imageData: ${e.message}")
        }
    }







    override fun downloadFilesByStudentId(volunteerId: String, imageType: String): ApiResponse<List<ImageDataResponseDto1>> {
        // Fetch all images associated with the studentId and filter by imageType
        val volunteerImgs = volunteerAdminImgRepository.findAllByVolunteerId(volunteerId).filter { it.imageType == imageType }

        return if (volunteerImgs.isNotEmpty()) {
            val imageDataResponses = volunteerImgs.map { studentImg ->
                // Ensure imageData is not null before passing it to downloadFileFromDrive
                val fileContent = studentImg.imageData?.let { downloadFileFromDrive(it) } ?: ByteArray(0) // Return an empty array if imageData is null

                ImageDataResponseDto1(
                    imageType = studentImg.imageType ?: "unknown",
                    imageData = studentImg.imageData?: "unknown",
                    volunteerId = studentImg.volunteerId ?: "no_id",
                    name = studentImg.imageName ?: "unknown",
                    image = fileContent, // Set the actual file content
                )
            }
            ApiResponse(success = true, data = imageDataResponses, message = "Image data retrieved successfully.")
        } else {
            ApiResponse(success = false, data = null, error = "No images found for student ID $volunteerId")
        }
    }

    override fun downloadFileFromDrive(fileId: String): ByteArray {
        val drive = googleDriveHelper.createDriveService()
        val outputStream = ByteArrayOutputStream()

        try {
            // Attempt to download the file from Google Drive using its fileId
            drive.files().get(fileId).executeMediaAndDownloadTo(outputStream)
        } catch (e: com.google.api.client.googleapis.json.GoogleJsonResponseException) {
            // Handle the case where the file was not found or there is a permission error
            if (e.statusCode == 404) {
                throw FileNotFoundException("File with ID $fileId not found on Google Drive")
            } else {
                throw RuntimeException("Error occurred while downloading file: ${e.details?.message}", e)
            }
        } catch (e: Exception) {
            // General exception handling
            throw RuntimeException("Unexpected error occurred while downloading file: ${e.message}", e)
        }

        return outputStream.toByteArray()
    }




    override fun findAll(): List<VolunteerActivityEntities> {
        return volunteerAdminActivityRepository.findAll()
    }


    override fun findByActivityName(activityName: String): List<VolunteerActivityEntities> {
        return volunteerAdminActivityRepository.findByActivityName(activityName)
    }

    override fun save(volunteerActivity: VolunteerActivityEntities): VolunteerActivityEntities {
        return volunteerAdminActivityRepository.save(volunteerActivity)
    }



    override fun findByStudentId(studentId: String): List<VolunteerActivityEntities> {
        return volunteerAdminActivityRepository.findByStudentId(studentId)
    }
    override fun getAllStudents(offset: Int, limit: Int ,  studentId: String?): Page<VolunteerActivityEntities> {
        val pageable: Pageable = PageRequest.of(offset, limit)

        return if (studentId != null){
            volunteerAdminActivityRepository.searchByStudentId(studentId, pageable)
        }
        else {
            volunteerAdminActivityRepository.findAll(pageable)

        }

    }




}