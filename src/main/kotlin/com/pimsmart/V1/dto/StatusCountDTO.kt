package com.pimsmart.V1.dto

data class StatusCountDTO(
    val status: String,  // อาจจะเป็น null ถ้าไม่มีค่า
    val count: Long,     // ใช้ค่า default เป็น 0 หากไม่พบ
    val timePeriod: String  // ใช้ค่า default เป็น "Unknown" หากไม่พบ
)

data class statisticsDTO(
    val label: String, // ชื่อช่วงเวลา เช่น "January", "2023"
    val count: Int // จำนวนชั่วโมง หรือข้อมูลรวม
)
