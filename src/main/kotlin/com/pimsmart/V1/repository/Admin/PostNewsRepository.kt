package com.pimsmart.V1.repository.Admin

import com.pimsmart.V1.dto.ImageDataResponseDto3
import com.pimsmart.V1.entities.Admin.PostEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PostNewsRepository : JpaRepository<PostEntity, Long>{
    fun findAllByImageType(imageType: String): List<PostEntity>

}