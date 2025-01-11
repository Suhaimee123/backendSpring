package com.pimsmart.V1.repository.User

import com.pimsmart.V1.entities.User.StudentEntities
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository



@Repository
interface RegisterRepository : JpaRepository<StudentEntities, Int> {
    fun findByStudentId(studentId: String): StudentEntities?

    fun findByStudentIdAndFirstName(studentId: String, firstName: String): StudentEntities?
    fun existsByStudentId(studentId: String): Boolean // เมธอดนี้ต้องมี


}



