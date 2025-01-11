package com.pimsmart.V1.service.ServiceIml.User

import com.pimsmart.V1.repository.User.RegisterRepository
import com.pimsmart.V1.Service.User.RegisterService
import com.google.api.client.http.FileContent
import com.pimsmart.V1.config.MailService
import com.pimsmart.V1.config.*
import com.pimsmart.V1.entities.User.ApplicationHistoryEntities

import com.pimsmart.V1.entities.User.RequestEntities
import com.pimsmart.V1.entities.User.StudentEntities
import com.pimsmart.V1.entities.User.StudentsImgEntities
import com.pimsmart.V1.repository.Admin.StudentsAdminImgRepository
import com.pimsmart.V1.repository.User.ApplicationHistoryRepository
import com.pimsmart.V1.repository.User.RequestRepository
import com.pimsmart.V1.repository.User.StudentsImgRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File


import java.time.LocalDateTime
import java.util.Collections


@Service
class RegisterServiceIml(
    private val studentRepository: RegisterRepository,
    private val appConfig: AppConfig,
    private val studentsImgRepository: StudentsImgRepository,
    private val studentsAdminImgRepository: StudentsAdminImgRepository,
    private val mailService: MailService,
    private val applicationHistoryRepository: ApplicationHistoryRepository,

    private val googleDriveHelper: GoogleDriveHelper,
    private val requestRepository: RequestRepository
) : RegisterService {
    
    override fun uploadImagesToDrive(
        files: List<MultipartFile>,
        registerId: Long,
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
                val tempFile = googleDriveHelper.createTempFileFromMultipart(file)
                try {
                    val currentImageName = imageNames[index] // ดึงชื่อไฟล์ที่สัมพันธ์กัน
                    uploadImageToDrive(tempFile, studentId, registerId, firstName, currentImageName, imageType)
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
        registerId : Long,
        firstName: String,
        imageName: String,
        imageType: String
    ): Res {
        val drive = googleDriveHelper.createDriveService()
        val folderId = googleDriveHelper.ensureFolderExists(drive,  "students", appConfig.folder.id)

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
        val studentsImgEntity = StudentsImgEntities().apply {
            this.studentId = studentId
            this.registerId = registerId
            this.firstName = firstName
            this.imageName = imageName
            this.imageData = uploadedFile.id  // เก็บเฉพาะ ID ของไฟล์ที่อัปโหลด
            this.createDate = LocalDateTime.now()
            this.imageType = imageType
        }
        studentsImgRepository.save(studentsImgEntity)

        return ResponseHelper.createResponse(200, "Image Successfully Uploaded To Drive", studentsImgEntity.imageData!!)
    }













    override fun getApplicationStatus(studentId: String): List<ApplicationHistoryEntities> {
        return applicationHistoryRepository.findByStudentId(studentId)
    }





    override fun registerStudent(student: StudentEntities): StudentEntities {
        val studentId = student.studentId ?: throw IllegalArgumentException("studentId must not be null")

        // ตรวจสอบว่ามี studentId ที่ซ้ำกันหรือไม่
        val existingStudent = studentRepository.findByStudentId(studentId)

        if (existingStudent != null) {
            throw IllegalArgumentException("Student with ID $studentId already exists.")
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

            val processedBy = if (status == "rejected") {
                "system"
            } else {
                null // หรือค่าอื่นในกรณีที่ต้องการ
            }

            // บันทึกข้อมูลนักศึกษาใหม่
            student.createDate = LocalDateTime.now()
            val savedStudent = studentRepository.save(student)

            // บันทึกข้อมูลลงใน application_history
            val applicationHistory = ApplicationHistoryEntities(
                registerId = savedStudent.id?.toLong(),
                studentId = savedStudent.studentId!!,
                applicationType = applicationType,
                processedBy = processedBy,
                status = status,
                submissionDate = LocalDateTime.now()
            )
            applicationHistoryRepository.save(applicationHistory)

            return savedStudent
        }
    }



    override fun saveRequest(request: RequestEntities): RequestEntities {
        val studentId = request.studentId ?: throw IllegalArgumentException("studentId must not be null")

        // ตรวจสอบว่า studentId มีคำร้องที่มีสถานะ "pending" หรือ "approved_stage_1"
        val existingRequests = applicationHistoryRepository.findByStudentIdAndMultipleStatuses(
            studentId, listOf("pending", "approved_stage_1")
        )

        // ถ้ามีคำร้องที่สถานะขัดแย้ง ให้โยนข้อผิดพลาด
        if (existingRequests.isNotEmpty()) {
            throw IllegalArgumentException("Cannot save request because there is already a request with status 'pending' or 'approved_stage_1'.")
        }

        // ถ้าไม่มีคำร้องขัดแย้ง ให้บันทึกคำร้องใหม่
        val requestToSave = request.copy(createDate = LocalDateTime.now())
        val savedRequest = requestRepository.save(requestToSave)

        // บันทึกประวัติคำร้องลงใน application_history
        val applicationHistory = ApplicationHistoryEntities(
            requestId = savedRequest.id,
            studentId = savedRequest.studentId.toString(),
            applicationType = "special_request",
            status = "pending",
            submissionDate = LocalDateTime.now(),
            specialRequest = savedRequest.specialRequest
        )
        applicationHistoryRepository.save(applicationHistory)

        return savedRequest
    }








    override fun sendStudentEmailWithAttachment(studentId: String, firstName: String, email: String): ApiResponse<String> {
        return try {
            // ค้นหา imageData จาก studentId ในฐานข้อมูล
            val studentImgs = studentsAdminImgRepository.findByStudentIdAndImageType(studentId, "PDF")
            if (studentImgs.isEmpty()) {
                return ApiResponse(
                    success = false,
                    data = null,
                    message = null,
                    error = "No PDF image data found for student ID: $studentId"
                )
            }

            // ใช้ไฟล์แรกในรายการ หากมีหลายรายการ
            val studentImg = studentImgs[0]

            // ดึง fileId จาก imageData เพื่อดาวน์โหลดไฟล์จาก Google Drive
            val fileId = studentImg.imageData ?: return ApiResponse(
                success = false,
                data = null,
                message = null,
                error = "No file ID found for student ID: $studentId"
            )
            val fileData = googleDriveHelper.downloadPdfFile(fileId)
            val fileName = "${studentId}_$firstName.pdf"

            // ส่งอีเมลพร้อมไฟล์แนบ
            sendEmail(
                to = email,
                subject = "ใบสมัครขอรับทุนกองทุน PIM SMART",
                content = """
                        <p>เรียนคุณ $firstName,</p>
                        <p>เราได้รับแบบฟอร์มการสมัครของท่านแล้ว</p>
                        <p>นศ. โปรดแอดไลน์ พี่ไอซ์ ID: iiceziing เพื่อติดตามการรับทุนต่อไป</p>
                        <p>อีเมลฉบับนี้เป็นการแจ้งข้อมูลจากระบบโดยอัตโนมัติ</p>
                        <p>กรุณาอย่าตอบกลับ</p>
                """.trimIndent(),
                attachment = fileData,
                fileName = fileName
            )


            // ส่งค่า ApiResponse สำเร็จ
            ApiResponse(
                success = true,
                data = "Email sent successfully with attachment",
                message = "Email sent to $email with attachment $fileName",
                error = null
            )

        } catch (ex: Exception) {
            // ส่งค่า ApiResponse เมื่อเกิดข้อผิดพลาด
            ApiResponse(
                success = false,
                data = null,
                message = null,
                error = "Failed to send email: ${ex.message}"
            )
        }
    }





    override fun sendEmail(to: String, subject: String, content: String, attachment: ByteArray?, fileName: String?) {
        mailService.sendEmailWithAttachment(to, subject, content, attachment, fileName)
    }







    override fun getStudentByStudentIdAndFirstName(studentId: String, firstName: String): StudentEntities? {
        // ตรวจสอบว่า studentId มีอยู่ในระบบ
        if (studentRepository.existsByStudentId(studentId)) {
            // ถ้ามี studentId แล้ว ให้ค้นหานักเรียนตาม studentId และ firstName
            val student = studentRepository.findByStudentIdAndFirstName(studentId, firstName)
            if (student != null) {
                return student
            } else {
                // หากไม่พบ student ตาม firstName
                println("No student found with studentId: $studentId and firstName: $firstName")
            }
        } else {
            println("StudentId $studentId does not exist")
        }
        return null // คืนค่า null ถ้า studentId ไม่พบหรือไม่มี student ที่ตรงกัน
    }

    override fun existsByStudentId(studentId: String): Boolean {
        return studentRepository.existsByStudentId(studentId) // เรียกใช้ repository
    }



































    override fun getStudentById(id: Int): StudentEntities? {
        return studentRepository.findById(id).orElse(null)
    }

    override fun getAllStudents(): List<StudentEntities> {
        return studentRepository.findAll()
    }

    override fun getStudentByStudentId(studentId: String): StudentEntities? {
        return studentRepository.findByStudentId(studentId)
    }


    override fun deleteStudent(id: Int): Boolean {
        return if (studentRepository.existsById(id)) {
            studentRepository.deleteById(id)
            true
        } else {
            false
        }
    }
}
