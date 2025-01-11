package com.pimsmart.V1.Service.User

import com.pimsmart.V1.config.Res
import com.pimsmart.V1.entities.User.VolunteerActivityEntities
import org.springframework.web.multipart.MultipartFile

interface VolunteerActivityService {
    fun findAll(): List<VolunteerActivityEntities>
    fun findById(id: Long): VolunteerActivityEntities?
    fun findByActivityName(activityName: String): List<VolunteerActivityEntities>
    fun save(volunteerActivity: VolunteerActivityEntities): VolunteerActivityEntities
    fun delete(id: Long)
    fun uploadImagesToDrive(files: List<MultipartFile>, studentId: String, firstName: String, imageName: String, imageType: String , volunteerId:String): List<Res>
    fun findByStudentId(studentId: String): List<VolunteerActivityEntities> // เพิ่มฟังก์ชันใหม่

}
