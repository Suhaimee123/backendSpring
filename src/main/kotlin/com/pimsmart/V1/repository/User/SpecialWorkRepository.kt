package com.pimsmart.V1.repository.User

import com.pimsmart.V1.entities.User.SpecialWorkEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface SpecialWorkRepository : JpaRepository<SpecialWorkEntity, Long> {

    @Query("SELECT s FROM SpecialWorkEntity s WHERE s.firstName = :activityName")
    fun findByActivityName(@Param("activityName") activityName: String): List<SpecialWorkEntity>

    @Query("SELECT s FROM SpecialWorkEntity s WHERE s.studentId = :studentId")
    fun findByStudentId(@Param("studentId") studentId: String): List<SpecialWorkEntity>
}
