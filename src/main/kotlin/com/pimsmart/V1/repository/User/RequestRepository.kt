package com.pimsmart.V1.repository.User

import com.pimsmart.V1.entities.User.RequestEntities
import org.springframework.data.jpa.repository.JpaRepository

interface RequestRepository : JpaRepository<RequestEntities, Int>