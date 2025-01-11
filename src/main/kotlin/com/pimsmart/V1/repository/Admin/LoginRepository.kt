package com.pimsmart.V1.repository.Admin

import com.pimsmart.V1.entities.Admin.LoginEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LoginRepository : JpaRepository<LoginEntity, Int> {
    fun existsByEmail(email: String): Boolean

    // ค้นหาผู้ใช้ตามอีเมล
    fun findByEmail(email: String): LoginEntity?
}
