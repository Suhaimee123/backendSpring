package com.pimsmart.V1.entities.Admin

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "scholarship_img") // Match the exact table name

class ScholarshipImgEntities {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Int? = null


    @Column(name = "student_id")
    var studentId: String? = null

    @Column(name = "first_name")
    var firstName: String? = null

    @Column(name = "image_name")
    var imageName: String? = null

    @Column(name = "image_data")  // ตรวจสอบให้แน่ใจว่าชื่อนี้ตรงกับชื่อคอลัมน์ในฐานข้อมูล
    var imageData: String? = null  // กำหนด imageData เป็น String ตามที่ต้องการ

    @Column(name = "create_date")
    var createDate: LocalDateTime? = null

    @Column(name = "image_type")
    var imageType: String? = null
}
