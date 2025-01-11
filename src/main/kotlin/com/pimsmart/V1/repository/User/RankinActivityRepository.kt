package com.pimsmart.V1.repository.User

import com.pimsmart.V1.entities.User.RankinActivitykEntities
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RankinActivityRepository : JpaRepository<RankinActivitykEntities, Long>
