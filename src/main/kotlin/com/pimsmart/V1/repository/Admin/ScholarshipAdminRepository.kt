package com.pimsmart.V1.repository.Admin

import com.pimsmart.V1.entities.Admin.ScholarshipEntities
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository



@Repository
interface ScholarshipAdminRepository : JpaRepository<ScholarshipEntities, Int> {

    fun findFirstByOrderByCreatedAtDesc(): ScholarshipEntities?
}