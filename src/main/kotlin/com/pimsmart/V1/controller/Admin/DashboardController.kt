package com.pimsmart.V1.controller.Admin

import com.pimsmart.V1.Service.Admin.DashboardService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/Admin/Dashboard")
class DashboardController(private val dashboardService: DashboardService) {

    @GetMapping("/activity-statistics")
    fun getActivityStatistics(): ResponseEntity<Map<String, Any>> {
        val statistics = dashboardService.getActivityStatisticsSummary()
        return ResponseEntity.ok(statistics)
    }





    // Endpoint สำหรับแสดงข้อมูลสรุป Dashboard
    @GetMapping("/summary")
    fun getDashboardSummary(): Map<String, Any> {
        return dashboardService.getDashboardSummary()
    }




}
