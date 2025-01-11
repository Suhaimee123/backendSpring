package com.pimsmart.V1.service.ServiceIml.User

import com.google.api.client.http.FileContent
import com.pimsmart.V1.Service.User.VolunteerActivityService
import com.pimsmart.V1.config.AppConfig
import com.pimsmart.V1.config.GoogleDriveHelper
import com.pimsmart.V1.config.Res
import com.pimsmart.V1.config.ResponseHelper
import com.pimsmart.V1.entities.User.VolunteerActivityEntities
import com.pimsmart.V1.entities.User.VolunteerImgEntities
import com.pimsmart.V1.repository.User.VolunteerActivityRepository
import com.pimsmart.V1.repository.User.VolunteerImgRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.time.LocalDateTime
import java.util.*

@Service
class VolunteerActivityServiceIml(
    private val volunteerActivityRepository: VolunteerActivityRepository,
    private val googleDriveHelper: GoogleDriveHelper,
    private val appConfig: AppConfig,
    private val volunteerImgRepository: VolunteerImgRepository
) : VolunteerActivityService {

    override fun findAll(): List<VolunteerActivityEntities> {
        return volunteerActivityRepository.findAll()
    }

    override fun findById(id: Long): VolunteerActivityEntities? {
        return volunteerActivityRepository.findById(id).orElse(null)
    }

    override fun findByActivityName(activityName: String): List<VolunteerActivityEntities> {
        return volunteerActivityRepository.findByActivityName(activityName)
    }

    override fun save(volunteerActivity: VolunteerActivityEntities): VolunteerActivityEntities {
        return volunteerActivityRepository.save(volunteerActivity)
    }

    override fun delete(id: Long) {
        volunteerActivityRepository.deleteById(id)
    }

    override fun findByStudentId(studentId: String): List<VolunteerActivityEntities> {
        return volunteerActivityRepository.findByStudentId(studentId)
    }


    override fun uploadImagesToDrive(
        files: List<MultipartFile>,
        studentId: String,
        firstName: String,
        imageName: String,
        imageType: String,
        volunteerId:String
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
                    val currentImageName = imageNames[index]
                    uploadImageToDrive(tempFile, studentId, firstName, currentImageName, imageType,volunteerId)
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
        volunteerId:String,
    ): Res {
        val drive = googleDriveHelper.createDriveService()
        val folderId = googleDriveHelper.ensureFolderExists(drive,  "Volunteer", appConfig.folder.id)

        val newFileName = "${imageType}_${studentId}_${file.name}"
        val fileMetaData = com.google.api.services.drive.model.File().apply {
            name = newFileName
            parents = Collections.singletonList(folderId)
        }
        val mediaContent = FileContent("image/jpeg", file)
        val uploadedFile = drive.files().create(fileMetaData, mediaContent)
            .setFields("id")
            .execute()

        // Save image information to the database
        val volunteerImgEntities = VolunteerImgEntities().apply {
            this.studentId = studentId
            this.firstName = firstName
            this.imageName = imageName
            this.imageData = uploadedFile.id  // เก็บเฉพาะ ID ของไฟล์ที่อัปโหลด
            this.createDate = LocalDateTime.now()
            this.imageType = imageType
            this.volunteerId = volunteerId

        }
        volunteerImgRepository.save(volunteerImgEntities)

        return ResponseHelper.createResponse(200, "Image Successfully Uploaded To Drive", volunteerImgEntities.imageData!!)
    }
}