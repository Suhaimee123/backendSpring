package com.pimsmart.V1.Service.Admin

import com.pimsmart.V1.config.ApiResponse

import com.pimsmart.V1.dto.ImageDataResponseDto2
import com.pimsmart.V1.entities.User.SpecialWorkEntity

import org.springframework.data.domain.Page

interface SpecialWorkAdminService {
    fun findAll(): List<SpecialWorkEntity>
    fun findById(id: Long): SpecialWorkEntity?
    fun delete(id: Long)
    fun deleteFileFromDriveAndDatabase(imageData: String): ApiResponse<Void>


    fun findByActivityName(activityName: String): List<SpecialWorkEntity>
    fun findByStudentId(studentId: String): List<SpecialWorkEntity>
    fun save(specialWork: SpecialWorkEntity): SpecialWorkEntity

    fun getAllStudents(offset: Int, limit: Int, studentId: String?): Page<SpecialWorkEntity>

    fun downloadFilesByStudentId(specialworkId: String, imageType: String): ApiResponse<List<ImageDataResponseDto2>>
    fun downloadFileFromDrive(fileId: String): ByteArray


}
