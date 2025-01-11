package com.pimsmart.V1.repository.User


import com.pimsmart.V1.entities.User.VolunteerImgEntities
import org.springframework.data.jpa.repository.JpaRepository

interface VolunteerImgRepository  : JpaRepository<VolunteerImgEntities, Int> {
}