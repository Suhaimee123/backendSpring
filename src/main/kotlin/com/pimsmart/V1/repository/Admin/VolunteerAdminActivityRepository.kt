package com.pimsmart.V1.repository.Admin


import com.pimsmart.V1.entities.User.VolunteerActivityEntities
import jakarta.persistence.Tuple
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface VolunteerAdminActivityRepository : JpaRepository<VolunteerActivityEntities, Long> {


    @Query("SELECT SUM(v.hours) FROM VolunteerActivityEntities v WHERE v.studentId = :studentId")
    fun sumHoursByStudentId(studentId: String): Int?


    @Query("""
    SELECT 'volunteer' AS status, COUNT(v) AS count, MONTH(v.createDate) AS timePeriod
    FROM VolunteerActivityEntities v
    WHERE YEAR(v.createDate) = YEAR(CURRENT_DATE)
    GROUP BY  MONTH(v.createDate)
    ORDER BY MONTH(v.createDate) ASC
""")
    fun findStatusCountByMonth(): List<Tuple>

    @Query("""
    SELECT 'volunteer' AS status, COUNT(v) AS count, DAY(v.createDate) AS timePeriod
    FROM VolunteerActivityEntities v
    WHERE YEAR(v.createDate) = YEAR(CURRENT_DATE)
      AND MONTH(v.createDate) = MONTH(CURRENT_DATE)
    GROUP BY DAY(v.createDate)
    ORDER BY DAY(v.createDate) ASC
    """)
    fun findStatusCountByDay(): List<Tuple>


    @Query("""
    SELECT 'volunteer' AS status, COUNT(v) AS count, YEAR(v.createDate) AS timePeriod
    FROM VolunteerActivityEntities v
    GROUP BY YEAR(v.createDate)
    ORDER BY YEAR(v.createDate) ASC

""")
    fun findStatusCountByYear(): List<Tuple>



    // ค้นหากิจกรรมอาสาสมัครตามชื่อกิจกรรม
    @Query("SELECT v FROM VolunteerActivityEntities v WHERE v.activityName = :activityName")
    fun findByActivityName(@Param("activityName") activityName: String): List<VolunteerActivityEntities>

    // ค้นหากิจกรรมอาสาสมัครตาม studentId
    @Query("SELECT v FROM VolunteerActivityEntities v WHERE v.studentId = :studentId")
    fun findByStudentId(@Param("studentId") studentId: String): List<VolunteerActivityEntities>

    fun searchByStudentId(studentId: String?, pageable: Pageable): Page<VolunteerActivityEntities>
}
