package com.pimsmart.V1.service.ServiceIml.User

import com.google.api.client.http.FileContent
import com.pimsmart.V1.Service.User.SpecialWorkService
import com.pimsmart.V1.config.AppConfig
import com.pimsmart.V1.config.GoogleDriveHelper
import com.pimsmart.V1.config.Res
import com.pimsmart.V1.config.ResponseHelper
import com.pimsmart.V1.entities.User.SpecialWorkEntity
import com.pimsmart.V1.entities.Admin.SpecialworkimgEntities
import com.pimsmart.V1.repository.Admin.SpecialworkAdminImgRepository
import com.pimsmart.V1.repository.User.SpecialWorkRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.time.LocalDateTime
import java.util.*

@Service
class SpecialWorkServiceIml(
    private val specialWorkRepository: SpecialWorkRepository,
    private val specialworkAdminImgRepository: SpecialworkAdminImgRepository,
    private val googleDriveHelper: GoogleDriveHelper,
    private val appConfig: AppConfig
) : SpecialWorkService {

    override fun findAll(): List<SpecialWorkEntity> {
        return specialWorkRepository.findAll()
    }

    override fun findById(id: Long): SpecialWorkEntity? {
        return specialWorkRepository.findById(id).orElse(null)
    }

    override fun findByActivityName(activityName: String): List<SpecialWorkEntity> {
        return specialWorkRepository.findByActivityName(activityName)
    }

    override fun findByStudentId(studentId: String): List<SpecialWorkEntity> {
        return specialWorkRepository.findByStudentId(studentId)
    }

    override fun save(specialWork: SpecialWorkEntity): SpecialWorkEntity {
        specialWork.createDate = LocalDateTime.now() // Assign createDate to the entity
        return specialWorkRepository.save(specialWork)
    }

    override fun delete(id: Long) {
        specialWorkRepository.deleteById(id)
    }

    // Function to upload images to Google Drive and save file info to database
    override fun uploadImagesToDrive(
        files: List<MultipartFile>,
        studentId: String,
        firstName: String,
        imageName: String,
        imageType: String,
        specialworkId:String
    ): List<Res>
    {
        val imageNames = imageName.split(",").map { it.trim() }

        if (files.size != imageNames.size) {
            throw IllegalArgumentException("Number of files and image names must match.")
        }
        val responses = files.mapIndexed { index,file ->
            if (file.isEmpty) {
                ResponseHelper.createResponse(400, "File ${file.originalFilename} is empty")
            } else {
                val tempFile = googleDriveHelper.createTempFileFromMultipart(file)
                try {
                    val currentImageName = imageNames[index] // ดึงชื่อไฟล์ที่สัมพันธ์กัน
                    uploadImageToDrive(tempFile, studentId, firstName, currentImageName, imageType ,specialworkId)
                } catch (e: Exception) {
                    ResponseHelper.createResponse(500, "Error uploading image ${file.originalFilename}: ${e.message}")
                } finally {
                    tempFile.delete()
                }
            }
        }
        return responses
    }

    private fun uploadImageToDrive(
        file: File,
        studentId: String,
        firstName: String,
        imageName: String,
        imageType: String,
        specialworkId:String

    ): Res {
        val drive = googleDriveHelper.createDriveService()
        val folderId = googleDriveHelper.ensureFolderExists(drive, "Specialwork", appConfig.folder.id)

        val newFileName = "${imageType}_${studentId}_${file.name}"
        val fileMetaData = com.google.api.services.drive.model.File().apply {
            name = newFileName
            parents = Collections.singletonList(folderId)
        }
        val mediaContent = FileContent("image/jpeg", file)
        val uploadedFile = drive.files().create(fileMetaData, mediaContent)
            .setFields("id")
            .execute()

        // Debugging log for file upload
        println("File uploaded to Google Drive with ID: ${uploadedFile.id}")

        // Save the uploaded file information to the database
        val specialworkimgEntities = SpecialworkimgEntities().apply {
            this.studentId = studentId
            this.firstName = firstName
            this.imageName = imageName
            this.imageData = uploadedFile.id  // Store only the ID of the uploaded file
            this.createDate = LocalDateTime.now()
            this.imageType = imageType
            this.specialworkId = specialworkId
        }
        specialworkAdminImgRepository.save(specialworkimgEntities)


        return ResponseHelper.createResponse(200, "Image Successfully Uploaded To Drive", specialworkimgEntities.imageData!!)
    }
}
