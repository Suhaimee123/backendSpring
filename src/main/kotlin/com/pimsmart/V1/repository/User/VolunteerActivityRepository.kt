package com.pimsmart.V1.repository.User

import com.pimsmart.V1.entities.User.VolunteerActivityEntities
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface VolunteerActivityRepository : JpaRepository<VolunteerActivityEntities, Long> {

    // ค้นหากิจกรรมอาสาสมัครตามชื่อกิจกรรม
    @Query("SELECT v FROM VolunteerActivityEntities v WHERE v.activityName = :activityName")
    fun findByActivityName(@Param("activityName") activityName: String): List<VolunteerActivityEntities>

    // ค้นหากิจกรรมอาสาสมัครตาม studentId
    @Query("SELECT v FROM VolunteerActivityEntities v WHERE v.studentId = :studentId")
    fun findByStudentId(@Param("studentId") studentId: String): List<VolunteerActivityEntities>
}
