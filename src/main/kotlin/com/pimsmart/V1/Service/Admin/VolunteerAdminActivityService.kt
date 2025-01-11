package com.pimsmart.V1.Service.Admin

import com.pimsmart.V1.config.ApiResponse
import com.pimsmart.V1.dto.ImageDataResponseDto1
import com.pimsmart.V1.entities.User.VolunteerActivityEntities
import org.springframework.data.domain.Page

interface VolunteerAdminActivityService {

    fun findById(id: Long): VolunteerActivityEntities?
    fun delete(id: Long)
    fun deleteFileFromDriveAndDatabase(imageData: String): ApiResponse<Void>




    fun downloadFilesByStudentId(volunteerId: String, imageType: String): ApiResponse<List<ImageDataResponseDto1>>
    fun downloadFileFromDrive(fileId: String): ByteArray


    fun findAll(): List<VolunteerActivityEntities>

    fun findByActivityName(activityName: String): List<VolunteerActivityEntities>
    fun save(volunteerActivity: VolunteerActivityEntities): VolunteerActivityEntities

    fun findByStudentId(studentId: String): List<VolunteerActivityEntities> // เพิ่มฟังก์ชันใหม่
    fun getAllStudents(offset: Int, limit: Int , studentId: String?): Page<VolunteerActivityEntities>

}
