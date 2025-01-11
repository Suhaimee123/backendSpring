package com.pimsmart.V1.Service.Admin

import com.pimsmart.V1.entities.Admin.LoginEntity

interface LoginService {

    fun register(email: String, password: String): LoginEntity // Change Student to Login
    fun login(email: String, password: String): String // ฟังก์ชันสำหรับการเข้าสู่ระบบ

}