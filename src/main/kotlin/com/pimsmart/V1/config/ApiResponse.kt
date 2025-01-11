package com.pimsmart.V1.config

data class ApiResponse<T>(
    val success: Boolean,      // เพิ่มฟิลด์ success
    val data: T?,              // ค่าที่จะส่งกลับ (อาจเป็น null ได้)
    val message: String? = null,
    val error: String? = null  // เพิ่มฟิลด์ error
)

data class ApiResponseD<T>(
    val success: Boolean,      // เพิ่มฟิลด์ success
    val message: String? = null,
    val error: String? = null  // เพิ่มฟิลด์ error
)