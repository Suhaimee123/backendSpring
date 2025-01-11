package com.pimsmart.V1.Service.Admin

import com.pimsmart.V1.config.Res
import com.pimsmart.V1.dto.StudentInfoWithVolunteerHours
import com.pimsmart.V1.entities.Admin.ScholarshipEntities
import org.springframework.data.domain.Page
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime



interface ScholarshipAdminService {

    fun getAllStudentsWithVolunteerHours(offset: Int, limit: Int , studentId: String?): Page<StudentInfoWithVolunteerHours>

    fun delete(id: Int): String?

    fun uploadImagesToDrive(files: List<MultipartFile>, studentId: String, firstName: String, imageName: String, imageType: String): List<Res>





    fun manageScholarship(id: Int, action: String, startDate: LocalDateTime? = null, endDate: LocalDateTime? = null): Boolean
    fun createScholarship(startDate: LocalDateTime, endDate: LocalDateTime): Boolean


    fun getLatestScholarship(): ScholarshipEntities?

}