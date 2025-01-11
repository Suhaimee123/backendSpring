package com.pimsmart.V1.repository.Admin



import org.springframework.stereotype.Repository
import org.springframework.data.jpa.repository.JpaRepository
import com.pimsmart.V1.entities.User.StudentsImgEntities
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

@Repository
interface StudentsAdminImgRepository : JpaRepository<StudentsImgEntities, Int> {



    fun findByStudentIdAndImageType(studentId: String, imageType: String): List<StudentsImgEntities>


    fun findByStudentId(studentId: String): List<StudentsImgEntities>
    fun findAllByStudentId(studentId: String): List<StudentsImgEntities>

    fun deleteByImageData(imageData: String): Int



}
