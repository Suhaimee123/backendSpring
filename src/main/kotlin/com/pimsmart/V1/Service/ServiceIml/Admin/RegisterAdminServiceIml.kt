package com.pimsmart.V1.Service.ServiceIml.Admin


import com.pimsmart.V1.entities.User.StudentsImgEntities
import com.pimsmart.V1.repository.Admin.RegisterAdminRepository
import com.pimsmart.V1.repository.Admin.StudentsAdminImgRepository
import com.pimsmart.V1.Service.Admin.RegisterAdminService
import com.pimsmart.V1.config.*
import com.pimsmart.V1.dto.ImageDataResponseDto
import com.pimsmart.V1.dto.StudentApplicationHistoryDTO
import com.pimsmart.V1.dto.mapToStudentApplicationHistoryDTO
import com.pimsmart.V1.entities.User.ApplicationHistoryEntities
import com.pimsmart.V1.entities.User.StudentEntities
import com.pimsmart.V1.repository.User.ApplicationHistoryRepository
import com.pimsmart.V1.repository.User.RequestRepository
import jakarta.transaction.Transactional

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException


import java.time.LocalDateTime
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties


import org.springframework.data.domain.Pageable

@Service
class RegisterAdminServiceIml(
    private val studentAdminRepository: RegisterAdminRepository,
    private val applicationHistoryRepository: ApplicationHistoryRepository,
    private val studentsAdminImgRepository: StudentsAdminImgRepository,
    private val requestRepository: RequestRepository,
    private val googleDriveHelper: GoogleDriveHelper,

    ) : RegisterAdminService {

    override fun registerAdminStudent(student: StudentEntities): Pair<StudentEntities, Boolean> {
        val studentId = student.studentId ?: throw IllegalArgumentException("studentId must not be null")

        // ตรวจสอบว่านักศึกษามีอยู่ในระบบหรือไม่
        val existingStudent = studentAdminRepository.findByStudentId(studentId)

        return if (existingStudent != null) {
            // ถ้ามีนักศึกษาแล้ว ให้ปรับปรุงข้อมูล แต่ไม่บันทึกลงใน application_history
            student.updateDate = LocalDateTime.now()
            copyNonNullProperties(student, existingStudent)
            studentAdminRepository.save(existingStudent)
            existingStudent to false // ส่งคืนข้อมูลพร้อมสถานะว่าเป็นการอัปเดต
        } else {
            // ตรวจสอบว่า beautyEnhancement เป็น "ทำเสริมความงาม"
            val applicationType = if (student.beautyEnhancement == "ทำเสริมความงาม") {
                "cosmetic_procedure"
            } else {
                "initial_application"
            }

            val status = if (applicationType == "cosmetic_procedure") {
                "rejected"
            } else {
                "pending"
            }
            // ถ้าไม่มีนักศึกษาในระบบ ให้บันทึกเป็นนักศึกษาใหม่
            student.createDate = LocalDateTime.now()
            val savedStudent = studentAdminRepository.save(student)

            // บันทึกการลงทะเบียนใหม่ใน application_history
            val registrationHistory = ApplicationHistoryEntities(
                registerId = savedStudent.id?.toLong(),
                studentId = savedStudent.studentId!!, // ใช้ ID จากนักศึกษาที่บันทึกใหม่
                applicationType = applicationType,
                status = status,
                submissionDate = LocalDateTime.now()
            )
            applicationHistoryRepository.save(registrationHistory)

            savedStudent to true // ส่งคืนข้อมูลพร้อมสถานะว่าเป็นการลงทะเบียนใหม่
        }
    }



    override fun deleteStudent(ApplicationId: Int): Boolean {
        return try {
            // ค้นหาข้อมูล ApplicationHistoryEntities โดยใช้ applicationId
            val applicationHistoryOptional = applicationHistoryRepository.findById(ApplicationId)
            println("Received applicationId: $ApplicationId")

            if (applicationHistoryOptional.isPresent) {
                val applicationHistory = applicationHistoryOptional.get()
                val studentId = applicationHistory.studentId
                println("Student ID found: $studentId")

                // ค้นหา ApplicationHistoryEntities ทั้งหมดที่มี studentId นี้
                val studentEntries = applicationHistoryRepository.findByStudentId(studentId)
                println("Number of entries with this Student ID: ${studentEntries.size}")

                if (studentEntries.size == 1) {
                    // ลบทั้ง ApplicationHistoryEntities และ StudentEntities
                    applicationHistoryRepository.delete(applicationHistory)

                    val studentOptional = studentAdminRepository.findOptionalByStudentId(studentId)
                    if (studentOptional.isPresent) {
                        println("Deleting student record with Student ID: $studentId")
                        studentAdminRepository.delete(studentOptional.get())
                    } else {
                        println("Student with studentId $studentId not found in StudentEntities")
                        return false
                    }

                    // ลบข้อมูลใน StudentsImgEntities ตาม studentId
                    val studentImgEntities = studentsAdminImgRepository.findByStudentId(studentId)
                    if (studentImgEntities.isNotEmpty()) {
                        for (imgEntity in studentImgEntities) {
                            val imageData = imgEntity.imageData
                            println("Found imageData: $imageData")

                            // ลบไฟล์จาก Google Drive
                            googleDriveHelper.deleteFile(imageData.toString())
                            println("Deleted imageData $imageData from Google Drive")

                            // ลบข้อมูลจาก StudentsImgEntities
                            studentsAdminImgRepository.delete(imgEntity)
                            println("Deleted imageData $imageData from StudentsImgEntities")
                        }
                    } else {
                        println("No images found for studentId $studentId in StudentsImgEntities")
                    }

                } else if (studentEntries.isNotEmpty()) {
                    // ลบ ApplicationHistoryEntities และ RequestEntities
                    applicationHistoryRepository.delete(applicationHistory)

                    val requestId = applicationHistory.requestId
                    println("Found requestId: $requestId")

                    val requestOptional = requestId?.let { requestRepository.findById(it) }
                    if (requestOptional?.isPresent == true) {
                        println("Deleting request record with requestId: $requestId")
                        requestRepository.delete(requestOptional.get())
                    } else {
                        println("Request with requestId $requestId not found in RequestEntities")
                    }
                }
                true
            } else {
                println("No ApplicationHistory found with applicationId: $ApplicationId")
                false
            }
        } catch (e: Exception) {
            println("Error occurred while deleting student data: ${e.message}")
            e.printStackTrace()
            false
        }
    }



    @Transactional
    override fun deleteFileFromDriveAndDatabase(imageData: String): ApiResponse<Void> {
        return try {
            // Delete the file from Google Drive
            googleDriveHelper.deleteFile(imageData)

            // Delete the file record from the database
            val deletedCount = studentsAdminImgRepository.deleteByImageData(imageData)

            if (deletedCount > 0) {
                ApiResponse(success = true, data = null, message = "File with imageData $imageData successfully deleted from Google Drive and database")
            } else {
                ApiResponse(success = false, data = null, message = "File with imageData $imageData not found in the database")
            }
        } catch (e: Exception) {
            ApiResponse(success = false, data = null, message = null, error = "Error deleting file with imageData $imageData: ${e.message}")
        }
    }


    override fun getAllStudents(offset: Int, limit: Int, studentId: String?): Page<StudentApplicationHistoryDTO> {
        val pageable: Pageable = PageRequest.of(offset, limit)

        // ค้นหาด้วย student ID และ status ถ้า studentId มีค่า; ถ้าไม่มีก็หาด้วย status
        val applicationPage = studentId?.let {
            applicationHistoryRepository.searchByStudentIdAndStatus(it, "pending", pageable)
        } ?: applicationHistoryRepository.findByStatus("pending", pageable)

        // ดึง student IDs ทั้งหมดจาก applicationPage
        val studentIds = applicationPage.content.map { it.studentId }
        val studentDetailsMap = studentAdminRepository.findByStudentIdIn(studentIds).associateBy { it.studentId }

        // นับจำนวนการเกิดขึ้นของแต่ละ studentId
        val studentIdCounts = studentIds.associateWith { id ->
            applicationHistoryRepository.countByStudentId(id)
        }

        // แปลงข้อมูลเป็น DTO
        val studentsWithDetails = applicationPage.content.map { applicationHistory ->
            val studentDetails = studentDetailsMap[applicationHistory.studentId]
            val studentIdCount = studentIdCounts[applicationHistory.studentId] ?: 0
            mapToStudentApplicationHistoryDTO(applicationHistory, studentDetails, studentIdCount.toString())
        }

        return PageImpl(studentsWithDetails, pageable, applicationPage.totalElements)
    }



















    override fun handleStudentApproval(
        studentId: String,
        applicationId: Int?,
        approve: String,
        approvedBy: String,
        appointmentDate: String?,
        startMonth: String?,
        endMonth: String?
    ): Boolean {
        // ค้นหา application history
        val applicationHistory = findApplicationHistory(studentId, applicationId)

        // Logic to determine stage based on status
        when (applicationHistory.status) {
            "pending" -> {
                if (approve == "Y") {
                    applicationHistory.status = "approved_stage_1"
                    applicationHistory.appointmentDate = appointmentDate
                        ?: throw IllegalArgumentException("Appointment date is required for stage 1.")
                } else {
                    applicationHistory.status = "rejected"
                }
            }
            "approved_stage_1" -> {
                if (approve == "Y") {
                    applicationHistory.status = "complete"
                    applicationHistory.startMonth = startMonth
                        ?: throw IllegalArgumentException("Start month is required for stage 2.")
                    applicationHistory.endMonth = endMonth
                        ?: throw IllegalArgumentException("End month is required for stage 2.")
                } else {
                    applicationHistory.status = "rejected"
                }
            }
            else -> throw IllegalArgumentException("Invalid status or parameters")
        }

        applicationHistory.processedDate = LocalDateTime.now()
        applicationHistory.processedBy = approvedBy
        applicationHistoryRepository.save(applicationHistory)

        return true
    }

    private fun findApplicationHistory(studentId: String, applicationId: Int?): ApplicationHistoryEntities {
        val applications = applicationHistoryRepository.findByStudentId(studentId)

        if (applications.isEmpty()) {
            throw IllegalArgumentException("Application not found for student ID: $studentId")
        }

        return if (applications.size > 1) {
            if (applicationId == null) {
                throw IllegalArgumentException("Multiple applications found for student ID: $studentId. Please provide applicationId.")
            }
            applications.find { it.ApplicationId == applicationId?.toInt() }
                ?: throw IllegalArgumentException("Application with ID $applicationId not found for student ID: $studentId")
        } else {
            applications.first()
        }
    }











    override fun downloadFilesByStudentId(studentId: String, imageType: String): ApiResponse<List<ImageDataResponseDto>> {
        // Fetch all images associated with the studentId and filter by imageType
        val studentImgs = studentsAdminImgRepository.findAllByStudentId(studentId).filter { it.imageType == imageType }

        return if (studentImgs.isNotEmpty()) {
            val imageDataResponses = studentImgs.map { studentImg ->
                // Ensure imageData is not null before passing it to downloadFileFromDrive
                val fileContent = studentImg.imageData?.let { downloadFileFromDrive(it) } ?: ByteArray(0) // Return an empty array if imageData is null

                ImageDataResponseDto(
                    imageType = studentImg.imageType ?: "unknown",
                    imageData = studentImg.imageData?: "unknown",
                    studentId = studentImg.studentId ?: "no_id",
                    name = studentImg.imageName ?: "unknown",
                    image = fileContent, // Set the actual file content
                )
            }
            ApiResponse(success = true, data = imageDataResponses, message = "Image data retrieved successfully.")
        } else {
            ApiResponse(success = false, data = null, error = "No images found for student ID $studentId")
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








    // Function to copy non-null properties from source to target object
    private fun copyNonNullProperties(source: StudentEntities, target: StudentEntities) {
        val properties = StudentEntities::class.memberProperties
        properties.forEach { property ->
            val value = property.get(source)
            if (value != null) {
                (property as KMutableProperty<*>).setter.call(target, value)
            }
        }
    }










    override fun getStudentImages(studentId: String): List<StudentsImgEntities> {
        return studentsAdminImgRepository.findByStudentId(studentId)
    }































    override fun getStudentById(id: Int): StudentEntities? {
        return studentAdminRepository.findById(id).orElse(null)
    }



    override fun getStudentByStudentId(studentId: String): StudentEntities? {
        return studentAdminRepository.findByStudentId(studentId)
    }

    override fun getStudentByStudentIdAndFirstName(studentId: String, firstName: String): StudentEntities? {
        return studentAdminRepository.findByStudentIdAndFirstName(studentId, firstName)
    }


}
