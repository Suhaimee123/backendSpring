package com.pimsmart.V1.Service.ServiceIml.Admin

import com.pimsmart.V1.Service.Admin.DashboardService
import com.pimsmart.V1.dto.StatusCountDTO
import com.pimsmart.V1.repository.Admin.SpecialWorkAdminRepository
import com.pimsmart.V1.repository.Admin.VolunteerAdminActivityRepository
import com.pimsmart.V1.repository.User.ApplicationHistoryRepository
import jakarta.persistence.Tuple
import org.springframework.stereotype.Service

@Service
class DashboardServiceIml(
    private val applicationHistoryRepository: ApplicationHistoryRepository,
    private val volunteerAdminActivityRepository : VolunteerAdminActivityRepository,
    private val specialWorkAdminRepository : SpecialWorkAdminRepository


) : DashboardService {

    override fun getActivityStatisticsSummary(): Map<String, Any> {
        val summary = mutableMapOf<String, Any>()

        // สถานะแยกตามเดือน
        val statusCountByMonth: List<StatusCountDTO> = convertTupleToStatusCountDTO(
            volunteerAdminActivityRepository.findStatusCountByMonth() + specialWorkAdminRepository.findStatusCountByMonth()
        ).sortedWith(
            compareBy<StatusCountDTO> { it.timePeriod.toInt() } // เรียงตาม timePeriod
                .thenBy { it.status } // ถ้ามี timePeriod เท่ากัน เรียงตาม status
        )
        summary["statusCountByMonth"] = statusCountByMonth

        // สถานะแยกตามปี
        val statusCountByYear: List<StatusCountDTO> = convertTupleToStatusCountDTO(
            volunteerAdminActivityRepository.findStatusCountByYear() + specialWorkAdminRepository.findStatusCountByYear()
        )
        summary["statusCountByYear"] = statusCountByYear

        // สถานะแยกตามวัน
        val statusCountByDay: List<StatusCountDTO> = convertTupleToStatusCountDTO(
            volunteerAdminActivityRepository.findStatusCountByDay() + specialWorkAdminRepository.findStatusCountByDay()
        ).sortedWith(
            compareBy<StatusCountDTO> { it.timePeriod.toInt() } // เรียงตาม timePeriod
                .thenBy { it.status } // ถ้ามี timePeriod เท่ากัน เรียงตาม status
        )
        summary["statusCountByDay"] = statusCountByDay

        return summary
    }







    // ฟังก์ชันสำหรับดึงข้อมูลสรุปของ Dashboard
    override fun getDashboardSummary(): Map<String, Any> {
        val summary = mutableMapOf<String, Any>()

        // ดึงข้อมูลสถานะแอปพลิเคชันแยกตามเดือน
        val statusCountByMonth: List<StatusCountDTO> = convertTupleToStatusCountDTO(applicationHistoryRepository.findStatusCountByMonth())
        summary["statusCountByMonth"] = statusCountByMonth

        // ดึงข้อมูลสถานะแอปพลิเคชันแยกตามปี
        val statusCountByYear: List<StatusCountDTO> = convertTupleToStatusCountDTO(applicationHistoryRepository.findStatusCountByYear())
        summary["statusCountByYear"] = statusCountByYear

        // ดึงข้อมูลสถานะแอปพลิเคชันแยกตามวัน
        val statusCountByDay: List<StatusCountDTO> = convertTupleToStatusCountDTO(applicationHistoryRepository.findStatusCountByDay())
        summary["statusCountByDay"] = statusCountByDay

        return summary
    }

    // ฟังก์ชันแปลง Tuple เป็น StatusCountDTO
    private fun convertTupleToStatusCountDTO(tuples: List<Tuple>): List<StatusCountDTO> {
        return tuples.map { tuple ->
            StatusCountDTO(
                status = tuple.get("status") as? String ?: "Unknown", // ตรวจสอบ null และตั้งค่า default เป็น "Unknown"
                count = tuple.get("count") as? Long ?: 0L, // ตรวจสอบ null และตั้งค่า default เป็น 0
                timePeriod = tuple.get("timePeriod")?.toString() ?: "Unknown" // ตรวจสอบ null และตั้งค่า default เป็น "Unknown"
            )
        }
    }






}
