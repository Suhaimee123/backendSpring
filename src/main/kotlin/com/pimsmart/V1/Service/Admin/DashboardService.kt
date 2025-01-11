package com.pimsmart.V1.Service.Admin

interface DashboardService {
    fun getActivityStatisticsSummary(): Map<String, Any>


    // ฟังก์ชันในการดึงข้อมูลสรุปของ Dashboard
    fun getDashboardSummary(): Map<String, Any>


}
