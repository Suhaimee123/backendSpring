package com.pimsmart.V1.repository.Admin

import com.pimsmart.V1.entities.Admin.ScholarshipImgEntities
import com.pimsmart.V1.entities.User.StudentsImgEntities
import com.pimsmart.V1.entities.User.VolunteerImgEntities
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface ScholarshipAdminImgRepository : JpaRepository<ScholarshipImgEntities, Int> {
    @Query("SELECT s FROM ScholarshipImgEntities s WHERE s.id = :id")
    fun findScholarshipById(@Param("id") id: Int): Optional<ScholarshipImgEntities>



    fun searchByStudentId(studentId: String?, pageable: Pageable): Page<ScholarshipImgEntities>


}