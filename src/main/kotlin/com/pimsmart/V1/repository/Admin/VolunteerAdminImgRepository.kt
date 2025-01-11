package com.pimsmart.V1.repository.Admin

import com.pimsmart.V1.entities.User.VolunteerImgEntities
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface VolunteerAdminImgRepository : JpaRepository<VolunteerImgEntities, Int> {
    @Modifying
    @Transactional
    @Query("DELETE FROM VolunteerImgEntities v WHERE v.volunteerId = :volunteerId")
    fun deleteByVolunteerId(volunteerId: String)

    fun findByStudentId(studentId: String): List<VolunteerImgEntities>
    fun findAllByVolunteerId(volunteerId: String): List<VolunteerImgEntities>
    fun deleteByImageData(imageData: String): Int



}