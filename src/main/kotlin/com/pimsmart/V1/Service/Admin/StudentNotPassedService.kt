package com.pimsmart.V1.Service.Admin

import com.pimsmart.V1.dto.ApprovalRequestDto
import com.pimsmart.V1.dto.StudentApplicationHistoryDTO
import org.springframework.data.domain.Page



interface StudentNotPassedService {
    fun approveStudent(request: ApprovalRequestDto) // Specify the type of the parameter





    fun getAllStudents(offset: Int, limit: Int, studentId: String?): Page<StudentApplicationHistoryDTO>

}