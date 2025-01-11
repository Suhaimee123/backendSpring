package com.pimsmart.V1.Service.ServiceIml.Admin

import com.pimsmart.V1.Service.Admin.SpecialWorkAdminService
import com.pimsmart.V1.config.*
import com.pimsmart.V1.dto.ImageDataResponseDto2
import com.pimsmart.V1.entities.User.SpecialWorkEntity

import com.pimsmart.V1.repository.Admin.SpecialWorkAdminRepository
import com.pimsmart.V1.repository.Admin.SpecialworkAdminImgRepository
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.time.LocalDateTime

@Service
class SpecialWorkAdminServiceIml(
    private val specialWorkAdminRepository: SpecialWorkAdminRepository,
    private val specialworkAdminImgRepository: SpecialworkAdminImgRepository,
    private val googleDriveHelper: GoogleDriveHelper,
) : SpecialWorkAdminService {

    override fun findAll(): List<SpecialWorkEntity> {
        return specialWorkAdminRepository.findAll()
    }

    override fun findById(id: Long): SpecialWorkEntity? {
        return specialWorkAdminRepository.findById(id).orElse(null)
    }




    @Transactional
    override fun delete(id: Long) {
        val activity = specialWorkAdminRepository.findById(id).orElse(null)
        if (activity != null) {

            val images = specialworkAdminImgRepository.findAllBySpecialworkId(id.toString())


            if (images.isEmpty()) {
            println("No images found for Special Work ID: $id. Proceeding to delete the activity.")
        } else {
            // ลบไฟล์แต่ละไฟล์จาก Google Drive
            images.forEach { image ->
                image.imageData?.let { googleDriveHelper.deleteFile(it) }
            }
            // ลบข้อมูลในตาราง specialwork_img
            specialworkAdminImgRepository.deleteBySpecialworkId(id.toString())
        }

        // ลบข้อมูลในตาราง specialwork
        specialWorkAdminRepository.deleteById(id)

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
            val deletedCount = specialworkAdminImgRepository.deleteByImageData(imageData)

            if (deletedCount > 0) {
                ApiResponse(success = true, data = null, message = "File with imageData $imageData successfully deleted from Google Drive and database")
            } else {
                ApiResponse(success = false, data = null, message = "File with imageData $imageData not found in the database")
            }
        } catch (e: Exception) {
            ApiResponse(success = false, data = null, message = null, error = "Error deleting file with imageData $imageData: ${e.message}")
        }
    }













    override fun findByActivityName(activityName: String): List<SpecialWorkEntity> {
        return specialWorkAdminRepository.findByActivityName(activityName)
    }

    override fun findByStudentId(studentId: String): List<SpecialWorkEntity> {
        return specialWorkAdminRepository.findByStudentId(studentId)
    }


    override fun getAllStudents(offset: Int, limit: Int , studentId: String?): Page<SpecialWorkEntity> {
        val pageable: Pageable = PageRequest.of(offset, limit)
        return  if (studentId != null){
            specialWorkAdminRepository.searchByStudentId(studentId, pageable)
        }
        else{
            specialWorkAdminRepository.findAll(pageable)
        }
    }


    override fun save(specialWork: SpecialWorkEntity): SpecialWorkEntity {
        specialWork.createDate = LocalDateTime.now() // Assign createDate to the entity
        return specialWorkAdminRepository.save(specialWork)
    }







    override fun downloadFilesByStudentId(specialworkId: String, imageType: String): ApiResponse<List<ImageDataResponseDto2>> {
        // Fetch all images associated with the studentId and filter by imageType
        val specialworkImgs = specialworkAdminImgRepository.findAllBySpecialworkId(specialworkId).filter { it.imageType == imageType }

        return if (specialworkImgs.isNotEmpty()) {
            val imageDataResponses = specialworkImgs.map { studentImg ->
                // Ensure imageData is not null before passing it to downloadFileFromDrive
                val fileContent = studentImg.imageData?.let { downloadFileFromDrive(it) } ?: ByteArray(0) // Return an empty array if imageData is null

                ImageDataResponseDto2(
                    imageType = studentImg.imageType ?: "unknown",
                    imageData = studentImg.imageData?: "unknown",
                    specialworkId = studentImg.specialworkId ?: "no_id",
                    name = studentImg.imageName ?: "unknown",
                    image = fileContent, // Set the actual file content
                )
            }
            ApiResponse(success = true, data = imageDataResponses, message = "Image data retrieved successfully.")
        } else {
            ApiResponse(success = false, data = null, error = "No images found for student ID $specialworkId")
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






}
