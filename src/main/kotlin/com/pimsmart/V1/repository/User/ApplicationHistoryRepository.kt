package com.pimsmart.V1.repository.User

import com.pimsmart.V1.entities.User.ApplicationHistoryEntities
import jakarta.persistence.Tuple
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ApplicationHistoryRepository : JpaRepository<ApplicationHistoryEntities, Int> {

    @Query(
        value = """
            SELECT a.status AS status, COUNT(a) AS count, EXTRACT(MONTH FROM a.submission_date) AS timePeriod 
            FROM application_history a 
            WHERE EXTRACT(YEAR FROM a.submission_date) = EXTRACT(YEAR FROM CURRENT_DATE)
            GROUP BY EXTRACT(MONTH FROM a.submission_date), a.status
            ORDER BY EXTRACT(MONTH FROM a.submission_date) ASC
        """,
        nativeQuery = true
    )
    fun findStatusCountByMonth(): List<Tuple>

    // Query สำหรับดึงข้อมูลสถานะตามวัน
    @Query(
        value = """
        SELECT a.status AS status, COUNT(a) AS count, EXTRACT(DAY FROM a.submission_date) AS timePeriod 
        FROM application_history a 
        WHERE EXTRACT(YEAR FROM a.submission_date) = EXTRACT(YEAR FROM CURRENT_DATE)
          AND EXTRACT(MONTH FROM a.submission_date) = EXTRACT(MONTH FROM CURRENT_DATE)
        GROUP BY EXTRACT(DAY FROM a.submission_date), a.status
        ORDER BY EXTRACT(DAY FROM a.submission_date) ASC

    """,
        nativeQuery = true
    )
    fun findStatusCountByDay(): List<Tuple>


    // Query สำหรับดึงข้อมูลสถานะตามปี
    @Query(
        value = """
        SELECT a.status AS status, COUNT(a) AS count, EXTRACT(YEAR FROM a.submission_date) AS timePeriod 
        FROM application_history a 
        GROUP BY EXTRACT(YEAR FROM a.submission_date), a.status
        ORDER BY EXTRACT(YEAR FROM a.submission_date) ASC
    """,
        nativeQuery = true
    )
    fun findStatusCountByYear(): List<Tuple>




    @Query("SELECT a FROM ApplicationHistoryEntities a WHERE a.ApplicationId = :applicationId")
    fun findByApplicationId(@Param("applicationId") applicationId: Int): ApplicationHistoryEntities?


    fun findByStudentIdAndStatus(studentId: String, status: String): ApplicationHistoryEntities?




    fun findByStudentId(studentId: String): List<ApplicationHistoryEntities>



    fun searchByStudentIdAndStatus(studentId: String, status: String, pageable: Pageable): Page<ApplicationHistoryEntities>
    fun findByStatus(status: String, pageable: Pageable): Page<ApplicationHistoryEntities>
    fun countByStudentId(studentId: String): String


    fun findByStudentIdAndStatusIn(studentId: String, statuses: List<String>): ApplicationHistoryEntities?

    @Query("SELECT a FROM ApplicationHistoryEntities a WHERE a.studentId = :studentId AND a.status IN :statuses")
    fun findByStudentIdAndMultipleStatuses(studentId: String, statuses: List<String>): List<ApplicationHistoryEntities>




}

