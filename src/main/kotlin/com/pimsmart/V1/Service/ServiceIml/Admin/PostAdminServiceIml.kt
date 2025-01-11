package com.pimsmart.V1.Service.ServiceIml.Admin


import com.google.api.client.http.FileContent

import com.pimsmart.V1.Service.Admin.PostAdminService
import com.pimsmart.V1.config.AppConfig
import com.pimsmart.V1.config.GoogleDriveHelper
import com.pimsmart.V1.config.Res
import com.pimsmart.V1.config.ResponseHelper
import com.pimsmart.V1.dto.ImageDataResponseDto3
import com.pimsmart.V1.entities.Admin.PostEntity

import com.pimsmart.V1.repository.Admin.PostNewsRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.io.File

import java.time.LocalDateTime
import java.util.*


@Service
@Transactional
class   PostAdminServiceIml(
    private val postNewsRepository: PostNewsRepository,
    private val googleDriveHelper: GoogleDriveHelper,
    private val appConfig: AppConfig
) : PostAdminService {


    override fun downloadFilesByImageType(imageType: String): List<ImageDataResponseDto3> {
        val posts = postNewsRepository.findAllByImageType(imageType)

        return posts.map { post ->
            val fileContent = try {
                post.imageData?.let { fileId ->
                    googleDriveHelper.downloadPdfFile(fileId) // ใช้ downloadPdfFile จาก GoogleDriveHelper
                } ?: ByteArray(0)
            } catch (e: Exception) {
                println("Error downloading file: ${e.message}")
                ByteArray(0)
            }



            // ส่งผลลัพธ์
            ImageDataResponseDto3(
                postId = post.id,
                imageType = post.imageType ?: "unknown",
                imageData = post.imageData ?: "unknown",
                name = post.imageNames ?: "unknown",
                createDate = post.createDate.toString(),
                content= post.content ?: "unknown",
                image = fileContent
            )
        }
    }






    override fun uploadImagesToGoogleDrive(
        files: List<MultipartFile>,
        content: String,
        id: Long,
        imageName: String,
        imageType: String
    ): List<Res> {
        val imageIds = mutableListOf<String>()

        val responses = files.map { file ->
            if (file.isEmpty) {
                ResponseHelper.createResponse(400, "File ${file.originalFilename} is empty")
            } else {
                val tempFile = googleDriveHelper.createTempFileFromMultipart(file)
                try {
                    val imageId = uploadImageToDrive(tempFile, id, imageType)
                    imageIds.add(imageId)
                    ResponseHelper.createResponse(200, "Image ${file.originalFilename} successfully uploaded to Drive", imageId)
                } catch (e: Exception) {
                    ResponseHelper.createResponse(500, "Error uploading image ${file.originalFilename}: ${e.message}")
                } finally {
                    tempFile.delete()
                }
            }
        }

        // Concatenate all image IDs with commas and save in PostEntity
        if (imageIds.isNotEmpty()) {
            val postEntity = PostEntity().apply {
                this.id = id
                this.content = content
                this.imageNames = imageName
                this.imageData = imageIds.joinToString(",") // Join image IDs with commas
                this.createDate = LocalDateTime.now()
                this.imageType = imageType
            }
            postNewsRepository.save(postEntity)
        }

        return responses
    }

    private fun uploadImageToDrive(
        file: File,
        id: Long,
        imageType: String
    ): String {
        val drive = googleDriveHelper.createDriveService()
        val folderId = googleDriveHelper.ensureFolderExists(drive, "Post", appConfig.folder.id)

        val newFileName = "${imageType}_${id}_${file.name}"
        val fileMetaData = com.google.api.services.drive.model.File().apply {
            name = newFileName
            parents = Collections.singletonList(folderId)
        }

        val mediaContent = FileContent("image/jpeg", file)
        val uploadedFile = drive.files().create(fileMetaData, mediaContent)
            .setFields("id")
            .execute()

        return uploadedFile.id
    }





    override fun updatePost(id: Long, updatedPost: PostEntity, imageFile: MultipartFile?): PostEntity? {
        val existingPost = postNewsRepository.findById(id).orElse(null) ?: return null

        // อัปเดตข้อมูลใน existingPost
        existingPost.content = updatedPost.content

        imageFile?.let { file ->
            val tempFile = googleDriveHelper.createTempFileFromMultipart(file)
            try {
                // ตรวจสอบและแปลงค่าที่อาจจะเป็น null เป็นค่าที่ไม่เป็น null
                val postId = existingPost.id ?: 0L // ใช้ 0L ถ้า id เป็น null
                val imageType = existingPost.imageType ?: "defaultType" // ใช้ค่า default ถ้า imageType เป็น null

                // เรียกใช้ uploadImageToDrive ด้วยค่าที่ไม่เป็น null
                val newImageId = uploadImageToDrive(tempFile, postId, imageType)
                existingPost.imageData = newImageId
                existingPost.imageNames = file.originalFilename ?: "unknown"
            } finally {
                tempFile.delete()
            }
        }

        return postNewsRepository.save(existingPost)
    }


    override fun deletePost(id: Long) {
        if (postNewsRepository.existsById(id)) postNewsRepository.deleteById(id)
    }



    override fun getAllPosts(): List<PostEntity> = postNewsRepository.findAll()

    override fun getPostById(id: Long): PostEntity? = postNewsRepository.findById(id).orElse(null)
}

