package com.pimsmart.V1.repository.User



import org.springframework.stereotype.Repository
import org.springframework.data.jpa.repository.JpaRepository
import com.pimsmart.V1.entities.User.StudentsImgEntities

@Repository
interface StudentsImgRepository : JpaRepository<StudentsImgEntities, Int> {

}
