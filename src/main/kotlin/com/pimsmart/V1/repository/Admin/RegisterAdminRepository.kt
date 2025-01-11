package com.pimsmart.V1.repository.Admin

import com.pimsmart.V1.entities.User.ApplicationHistoryEntities
import com.pimsmart.V1.entities.User.StudentEntities
import com.pimsmart.V1.entities.User.VolunteerActivityEntities
import org.springframework.data.domain.Page
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.data.domain.Pageable // Correct import
import java.util.*


@Repository
interface RegisterAdminRepository : JpaRepository<StudentEntities, Int> {
    fun searchByStudentId(studentId: String?, pageable: Pageable): Page<StudentEntities>



    fun findOptionalByStudentId(studentId: String): Optional<StudentEntities>


    fun findByStudentIdIn(studentIds: List<String>): List<StudentEntities>


    fun findByStudentId(studentId: String): StudentEntities?

    fun findByStudentIdAndFirstName(studentId: String, firstName: String): StudentEntities?

}



