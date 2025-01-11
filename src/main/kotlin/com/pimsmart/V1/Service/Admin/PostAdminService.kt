package com.pimsmart.V1.Service.Admin

import com.pimsmart.V1.config.Res
import com.pimsmart.V1.dto.ImageDataResponseDto3
import com.pimsmart.V1.entities.Admin.PostEntity
import org.springframework.web.multipart.MultipartFile

interface PostAdminService {
    fun uploadImagesToGoogleDrive(
        files: List<MultipartFile>,
        content: String,
        id: Long,
        imageName: String,
        imageType: String


    ): List<Res>
    fun downloadFilesByImageType(imageType: String): List<ImageDataResponseDto3>
    fun getAllPosts(): List<PostEntity>
    fun getPostById(id: Long): PostEntity?

    fun updatePost(id: Long, updatedPost: PostEntity, imageFile: MultipartFile?): PostEntity?

    fun deletePost(id: Long)


}
