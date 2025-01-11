package com.pimsmart.V1.Service.User

import com.pimsmart.V1.config.Res
import com.pimsmart.V1.entities.User.SpecialWorkEntity
import org.springframework.web.multipart.MultipartFile

interface SpecialWorkService {
    fun findAll(): List<SpecialWorkEntity>
    fun findById(id: Long): SpecialWorkEntity?
    fun findByActivityName(activityName: String): List<SpecialWorkEntity>
    fun findByStudentId(studentId: String): List<SpecialWorkEntity>
    fun save(specialWork: SpecialWorkEntity): SpecialWorkEntity
    fun uploadImagesToDrive(
        files: List<MultipartFile>,
        studentId: String,
        firstName: String,
        imageName: String,
        imageType: String,
        specialworkId:String
    ): List<Res>
    fun delete(id: Long)



}
