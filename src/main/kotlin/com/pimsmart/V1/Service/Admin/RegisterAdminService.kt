package com.pimsmart.V1.Service.Admin

import com.pimsmart.V1.config.ApiResponse
import com.pimsmart.V1.dto.ImageDataResponseDto
import com.pimsmart.V1.dto.StudentApplicationHistoryDTO
import com.pimsmart.V1.entities.User.StudentEntities
import com.pimsmart.V1.entities.User.StudentsImgEntities
import org.springframework.data.domain.Page

interface RegisterAdminService {

    fun deleteStudent(ApplicationId: Int): Boolean


    fun getAllStudents(offset: Int, limit: Int, studentId: String?): Page<StudentApplicationHistoryDTO>












    fun handleStudentApproval(
        studentId: String,
        applicationId: Int?,
        approve: String,
        approvedBy: String,
        appointmentDate: String? = null,
        startMonth: String? = null,
        endMonth: String? = null
    ): Boolean

    fun registerAdminStudent(student: StudentEntities): Pair<StudentEntities, Boolean>








    fun deleteFileFromDriveAndDatabase(imageData: String): ApiResponse<Void>



    fun downloadFileFromDrive(fileId: String): ByteArray
    fun downloadFilesByStudentId(studentId: String, imageType: String): ApiResponse<List<ImageDataResponseDto>>

    fun getStudentImages(studentId: String): List<StudentsImgEntities>




    fun getStudentById(id: Int): StudentEntities?
    fun getStudentByStudentId(studentId: String): StudentEntities?
    fun getStudentByStudentIdAndFirstName(studentId: String, firstName: String): StudentEntities?


}
