package com.pimsmart.V1.Service.ServiceIml.Admin

import com.google.api.client.http.FileContent
import com.pimsmart.V1.Service.Admin.ScholarshipAdminService
import com.pimsmart.V1.config.*
import com.pimsmart.V1.dto.StudentInfoWithVolunteerHours
import com.pimsmart.V1.entities.Admin.ScholarshipEntities
import com.pimsmart.V1.entities.Admin.ScholarshipImgEntities
import com.pimsmart.V1.repository.Admin.ScholarshipAdminImgRepository
import com.pimsmart.V1.repository.Admin.ScholarshipAdminRepository
import com.pimsmart.V1.repository.Admin.VolunteerAdminActivityRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class ScholarshipAdminServiceIml(
    private val scholarshipAdminRepository: ScholarshipAdminRepository,
    private val scholarshipAdminImgRepository  : ScholarshipAdminImgRepository,
    private val volunteerAdminActivityRepository : VolunteerAdminActivityRepository,

    private val appConfig: AppConfig,

    private val googleDriveHelper: GoogleDriveHelper,

    ) : ScholarshipAdminService {

    override fun getAllStudentsWithVolunteerHours(offset: Int, limit: Int, studentId: String?): Page<StudentInfoWithVolunteerHours> {
        val pageable: Pageable = PageRequest.of(offset, limit)
        val students = if (studentId != null) {
            scholarshipAdminImgRepository.searchByStudentId(studentId, pageable)
        } else {
            scholarshipAdminImgRepository.findAll(pageable)
        }

        return students.map { student ->
            val totalHours = volunteerAdminActivityRepository.sumHoursByStudentId(student.studentId ?: "") ?: 0
            StudentInfoWithVolunteerHours(
                id = student.id,
                studentId = student.studentId,
                firstName = student.firstName,
                imageName = student.imageName,
                imageData = student.imageData,
                createDate = student.createDate,
                imageType = student.imageType,
                totalVolunteerHours = totalHours
            )
        }
    }




    override fun delete(id: Int): String? {
        // ตรวจสอบว่ามีรูปภาพที่เกี่ยวข้องกับ scholarship นี้หรือไม่
        val images = scholarshipAdminImgRepository.findById(id)

        // ถ้าไม่พบรูปภาพที่เกี่ยวข้อง หรือไม่พบข้อมูล
        if (images.isEmpty) {
            return null  // คืนค่า null เมื่อไม่มีข้อมูล
        }

        // ถ้ามีรูปภาพที่เกี่ยวข้อง
        val image = images.get()
        image.imageData?.let {
            // ลบไฟล์จาก Google Drive
            googleDriveHelper.deleteFile(it)
        }

        // ลบข้อมูลรูปภาพออกจากฐานข้อมูล
        scholarshipAdminImgRepository.delete(image)
        println("Image related to scholarship deleted successfully.")

        // ลบข้อมูล scholarship จากฐานข้อมูล
        scholarshipAdminRepository.deleteById(id)

        println("Scholarship with ID: $id deleted successfully.")  // ข้อความใน log
        return "Scholarship deleted successfully"  // ข้อความที่ return ไป
    }















    override fun uploadImagesToDrive(
        files: List<MultipartFile>,
        studentId: String,
        firstName: String,
        imageName: String,
        imageType: String
    ): List<Res> {
        // แปลง imageName ให้เป็น List
        val imageNames = imageName.split(",").map { it.trim() }

        if (files.size != imageNames.size) {
            throw IllegalArgumentException("Number of files and image names must match.")
        }

        val responses = files.mapIndexed { index, file ->
            if (file.isEmpty) {
                ResponseHelper.createResponse(400, "File ${file.originalFilename} is empty")
            } else {
                val tempFile = googleDriveHelper.createTempPdfFileFromMultipart(file)
                try {
                    val currentImageName = imageNames[index] // ดึงชื่อไฟล์ที่สัมพันธ์กัน
                    uploadImageToDrive(tempFile, studentId, firstName, currentImageName, imageType)
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
        imageType: String
    ): Res {
        val drive = googleDriveHelper.createDriveService()
        val folderId = googleDriveHelper.ensureFolderExists(drive,  "scholarship", appConfig.folder.id)
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss") // รูปแบบวันที่และเวลา
        val currentDateTime = LocalDateTime.now()
        val formattedDateTime = currentDateTime.format(formatter)



        val newFileName = "${studentId}_${firstName}_$formattedDateTime.pdf"
        val fileMetaData = com.google.api.services.drive.model.File().apply {
            name = newFileName
            parents = Collections.singletonList(folderId)
        }
        val mediaContent = FileContent("application/pdf", file)
        val uploadedFile = drive.files().create(fileMetaData, mediaContent)
            .setFields("id")
            .execute()

        // Save image information to the database
        val scholarshipImgEntity = ScholarshipImgEntities().apply {
            this.studentId = studentId
            this.firstName = firstName
            this.imageName = imageName
            this.imageData = uploadedFile.id  // เก็บเฉพาะ ID ของไฟล์ที่อัปโหลด
            this.createDate = LocalDateTime.now()
            this.imageType = imageType
        }
        scholarshipAdminImgRepository.save(scholarshipImgEntity)

        return ResponseHelper.createResponse(200, "Image Successfully Uploaded To Drive", scholarshipImgEntity.imageData!!)
    }




    override fun manageScholarship(id: Int, action: String, startDate: LocalDateTime?, endDate: LocalDateTime?): Boolean {
        val scholarship = scholarshipAdminRepository.findById(id)

        if (!scholarship.isPresent) {
            // Return false or throw an exception if scholarship doesn't exist
            throw IllegalArgumentException("Scholarship with ID $id does not exist.")
        }

        val entity = scholarship.get()
        val newAcademicYear = "${startDate?.year}"

        // Check if the academicYear matches
        if (action.lowercase() == "open" && entity.academicYear != newAcademicYear) {
            // Create a new scholarship if the academicYear does not match
            return createScholarship(startDate!!, endDate!!)
        }

        when (action.lowercase()) {
            "open" -> {
                entity.startDate = startDate
                entity.endDate = endDate
                entity.status = "open"
            }
            "close" -> {
                entity.status = "close"
                entity.endDate = LocalDateTime.now() // Close immediately
            }
            else -> return false
        }
        scholarshipAdminRepository.save(entity)
        return true
    }


    override fun createScholarship(startDate: LocalDateTime, endDate: LocalDateTime): Boolean {
        val newScholarship = ScholarshipEntities(
            academicYear = "${startDate.year}",
            status = "open",
            startDate = startDate,
            endDate = endDate,
            createdAt = LocalDateTime.now()
        )
        scholarshipAdminRepository.save(newScholarship)
        return true
    }


    override fun getLatestScholarship(): ScholarshipEntities? {
        return scholarshipAdminRepository.findFirstByOrderByCreatedAtDesc()
    }

}
