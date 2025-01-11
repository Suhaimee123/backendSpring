package com.pimsmart.V1.config

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.security.GeneralSecurityException
import java.util.*

import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import java.io.*

// คลาสของคุณ
@Component
class GoogleDriveHelper(private val appConfig: AppConfig) {

    companion object {
        private val JSON_FACTORY: JsonFactory = GsonFactory.getDefaultInstance()
    }

    @Throws(IOException::class, GeneralSecurityException::class)
    fun downloadPdfFile(fileId: String): ByteArray {
        val driveService = createDriveService()
        val outputStream = ByteArrayOutputStream()

        driveService.files().get(fileId).executeMediaAndDownloadTo(outputStream)

        return outputStream.toByteArray()
    }


    @Throws(IOException::class, GeneralSecurityException::class)
    fun createDriveService(): Drive {
        val credentials = GoogleCredentials.fromStream(FileInputStream(appConfig.drive.credentialsPath))
            .createScoped(setOf(DriveScopes.DRIVE))

        return Drive.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            JSON_FACTORY,  // ใช้ JSON_FACTORY ที่ประกาศไว้ใน companion object
            HttpCredentialsAdapter(credentials)
        ).apply {
            applicationName = "PIMSMART"  // ใส่ชื่อแอปพลิเคชันของคุณ
        }.build()
    }

    fun deleteFile(fileId: String) {
        val driveService = createDriveService()
        driveService.files().delete(fileId).execute()
    }


    @Throws(IOException::class, GeneralSecurityException::class)
    fun ensureFolderExists(drive: Drive, folderName: String, parentId: String): String {
        val query = "mimeType='application/vnd.google-apps.folder' and name='$folderName' and '$parentId' in parents"
        val result = drive.files().list()
            .setQ(query)
            .setSpaces("drive")
            .setFields("files(id, name)")
            .execute()

        return if (result.files.isEmpty()) {
            createFolder(drive, folderName, parentId).id
        } else {
            result.files[0].id
        }
    }

    @Throws(IOException::class)
    private fun createFolder(drive: Drive, folderName: String, parentId: String): com.google.api.services.drive.model.File {
        val fileMetaData = com.google.api.services.drive.model.File().apply {
            name = folderName
            mimeType = "application/vnd.google-apps.folder"
            parents = listOf(parentId)
        }
        return drive.files().create(fileMetaData).setFields("id").execute()
    }

    @Throws(IOException::class)
    fun createTempFileFromMultipart(file: MultipartFile): File {
        return File.createTempFile("temp", null).apply {
            outputStream().use { file.inputStream.transferTo(it) }
        }
    }

    @Throws(IOException::class)
    fun createTempPdfFileFromMultipart(file: MultipartFile): File {
        // ตรวจสอบว่าไฟล์ที่ส่งมาคือ PDF หรือไม่
        if (file.contentType != "application/pdf") {
            throw IllegalArgumentException("The uploaded file must be a PDF.")
        }

        // สร้างไฟล์ PDF ชั่วคราว
        return File.createTempFile("temp", ".pdf").apply {
            outputStream().use { file.inputStream.transferTo(it) }
        }
    }

}
