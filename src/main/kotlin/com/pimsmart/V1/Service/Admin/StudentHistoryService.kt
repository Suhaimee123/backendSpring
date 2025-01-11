package com.pimsmart.V1.Service.Admin

import com.pimsmart.V1.dto.StudentApplicationHistoryDTO
import org.springframework.data.domain.Page



interface StudentHistoryService {





    fun getAllStudents(offset: Int, limit: Int , studentId: String?): Page<StudentApplicationHistoryDTO>

}