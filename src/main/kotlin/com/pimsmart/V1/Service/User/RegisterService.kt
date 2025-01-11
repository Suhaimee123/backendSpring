package com.pimsmart.V1.Service.User

import com.pimsmart.V1.config.ApiResponse
import com.pimsmart.V1.config.Res
import com.pimsmart.V1.entities.User.ApplicationHistoryEntities
import com.pimsmart.V1.entities.User.RequestEntities
import com.pimsmart.V1.entities.User.StudentEntities
import org.springframework.web.multipart.MultipartFile


interface RegisterService {
    fun uploadImagesToDrive(files: List<MultipartFile>,registerId : Long , studentId: String,firstName: String, imageName: String, imageType: String): List<Res>


    fun getApplicationStatus(studentId: String): List<ApplicationHistoryEntities>



    fun registerStudent(student: StudentEntities): StudentEntities




    fun saveRequest(request: RequestEntities): RequestEntities






    fun sendStudentEmailWithAttachment(studentId: String, firstName: String, email: String): ApiResponse<String>
    fun sendEmail(to: String, subject: String, content: String, attachment: ByteArray?, fileName: String?)








    fun getStudentByStudentIdAndFirstName(studentId: String, firstName: String): StudentEntities?
    fun existsByStudentId(studentId: String): Boolean // เพิ่มเมธอดนี้



    fun getStudentById(id: Int): StudentEntities?
    fun getAllStudents(): List<StudentEntities>
    fun getStudentByStudentId(studentId: String): StudentEntities?
    fun deleteStudent(id: Int): Boolean


}
