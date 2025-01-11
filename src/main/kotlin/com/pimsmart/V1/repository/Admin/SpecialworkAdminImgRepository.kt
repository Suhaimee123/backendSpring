package com.pimsmart.V1.repository.Admin



import com.pimsmart.V1.entities.Admin.SpecialworkimgEntities
import org.springframework.stereotype.Repository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional


@Repository
interface SpecialworkAdminImgRepository : JpaRepository<SpecialworkimgEntities, Int> {
    @Modifying
    @Transactional
    @Query("DELETE FROM SpecialworkimgEntities v WHERE v.specialworkId = :specialworkId")
    fun deleteBySpecialworkId(specialworkId: String)

    fun findAllBySpecialworkId(specialworkId: String): List<SpecialworkimgEntities>
    fun deleteByImageData(imageData: String): Int
}

