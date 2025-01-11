package com.pimsmart.V1.repository.Admin

import com.pimsmart.V1.entities.User.SpecialWorkEntity
import jakarta.persistence.Tuple

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
interface SpecialWorkAdminRepository : JpaRepository<SpecialWorkEntity, Long> {

    @Query("""
    SELECT 'special' AS status, COUNT(s) AS count, MONTH(s.createDate) AS timePeriod
    FROM SpecialWorkEntity s
    WHERE YEAR(s.createDate) = YEAR(CURRENT_DATE)
    GROUP BY  MONTH(s.createDate)
    ORDER BY MONTH(s.createDate) ASC

""")
    fun findStatusCountByMonth(): List<Tuple>

    @Query("""
    SELECT 'special' AS status, COUNT(s) AS count, DAY(s.createDate) AS timePeriod
    FROM SpecialWorkEntity s
    WHERE YEAR(s.createDate) = YEAR(CURRENT_DATE)
      AND MONTH(s.createDate) = MONTH(CURRENT_DATE)
    GROUP BY DAY(s.createDate)
    ORDER BY DAY(s.createDate) ASC

    """)
    fun findStatusCountByDay(): List<Tuple>




    @Query("""
    SELECT 'special' AS status, COUNT(s) AS count, YEAR(s.createDate) AS timePeriod
    FROM SpecialWorkEntity s
    GROUP BY YEAR(s.createDate)
""")
    fun findStatusCountByYear(): List<Tuple>



    @Query("SELECT s FROM SpecialWorkEntity s WHERE s.firstName = :activityName")
    fun findByActivityName(@Param("activityName") activityName: String): List<SpecialWorkEntity>

    @Query("SELECT s FROM SpecialWorkEntity s WHERE s.studentId = :studentId")
    fun findByStudentId(@Param("studentId") studentId: String): List<SpecialWorkEntity>
    fun searchByStudentId(studentId: String?, pageable: Pageable): Page<SpecialWorkEntity>
}
