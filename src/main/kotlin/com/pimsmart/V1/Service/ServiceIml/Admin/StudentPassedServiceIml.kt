package com.pimsmart.V1.Service.ServiceIml.Admin

import com.pimsmart.V1.Service.Admin.StudentPassedService
import com.pimsmart.V1.dto.StudentApplicationHistoryDTO
import com.pimsmart.V1.dto.mapToStudentApplicationHistoryDTO
import com.pimsmart.V1.repository.Admin.RegisterAdminRepository
import com.pimsmart.V1.repository.Admin.StudentsAdminImgRepository
import com.pimsmart.V1.repository.User.ApplicationHistoryRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service


@Service
class StudentPassedServiceIml (

    private val studentAdminRepository: RegisterAdminRepository,

    private val applicationHistoryRepository: ApplicationHistoryRepository,
    private val studentsAdminImgRepository: StudentsAdminImgRepository,

    ): StudentPassedService {


    override fun getAllStudents(offset: Int, limit: Int, studentId: String?): Page<StudentApplicationHistoryDTO> {
        val pageable: Pageable = PageRequest.of(offset, limit)

        // ค้นหาด้วย student ID และ status ถ้า studentId มีค่า; ถ้าไม่มีก็หาด้วย status
        val applicationPage = studentId?.let {
            applicationHistoryRepository.searchByStudentIdAndStatus(it, "approved_stage_1", pageable)
        } ?: applicationHistoryRepository.findByStatus("approved_stage_1", pageable)

        // ดึง student IDs ทั้งหมดจาก applicationPage
        val studentIds = applicationPage.content.map { it.studentId }
        val studentDetailsMap = studentAdminRepository.findByStudentIdIn(studentIds).associateBy { it.studentId }

        // นับจำนวนการเกิดขึ้นของแต่ละ studentId
        val studentIdCounts = studentIds.associateWith { id ->
            applicationHistoryRepository.countByStudentId(id)
        }

        // แปลงข้อมูลเป็น DTO
        val studentsWithDetails = applicationPage.content.map { applicationHistory ->
            val studentDetails = studentDetailsMap[applicationHistory.studentId]
            val studentIdCount = studentIdCounts[applicationHistory.studentId] ?: 0
            mapToStudentApplicationHistoryDTO(applicationHistory, studentDetails, studentIdCount.toString())
        }

        return PageImpl(studentsWithDetails, pageable, applicationPage.totalElements)
    }

}