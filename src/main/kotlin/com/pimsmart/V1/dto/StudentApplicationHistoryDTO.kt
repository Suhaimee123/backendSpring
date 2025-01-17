package com.pimsmart.V1.dto

import com.pimsmart.V1.entities.User.ApplicationHistoryEntities
import com.pimsmart.V1.entities.User.StudentEntities
import java.math.BigDecimal
import java.time.LocalDateTime

data class StudentApplicationHistoryDTO(
    var id: Long? = null,  // Corresponding to `id` in the SQL table
    var ApplicationId: Int? = null,
    var requestId: Int? = null,
    var studentId: String? = null,
    var prefix: String? = null,
    var firstName: String? = null,
    var thaiName: String? = null,
    var lastName: String? = null,
    var nickname: String? = null,
    var dateOfBirth: String? = null,  // Use `LocalDate` for date fields
    var gender: String? = null,
    var nationalId: String? = null,
    var email: String? = null,
    var phoneNumber: String? = null,
    var faculty: String? = null,
    var fieldOfStudy: String? = null,
    var currentlyStudyingYear: String? = null,
    var studentType: String? = null,
    var block: String? = null,
    var currentGpa: BigDecimal? = null,  // Use `BigDecimal` for numeric fields
    var createDate: LocalDateTime? = null,  // Use `LocalDateTime` for timestamp fields
    var updateDate: LocalDateTime? = null,
    var placeOfStudy: String? = null,
    var otherPlace: String? = null,
    var currentAddress: String? = null,  // Use `String?` for `text` fields
    var studentResident: String? = null,
    var numberOfResidents: Int? = null,
    var currentProvince: String? = null,
    var currentDistrict: String? = null,
    var currentSubdistrict: String? = null,
    var currentPostalCode: String? = null,
    var addressAccordingToHouseRegistration: String? = null,
    var province: String? = null,
    var district: String? = null,
    var subdistrict: String? = null,
    var postalCode: String? = null,
    var advisorNameSurname: String? = null,
    var advisorPhoneNumber: String? = null,
    var knowThePimSmartFundFrom: String? = null,
    var additionalDetails: String? = null,
    var scholarshipReceived: String? = null,
    var otherScholarships: String? = null,
    var educationLoan: String? = null,
    var graduatedFromSchool: String? = null,
    var provinceSchool: String? = null,
    var lineId: String? = null,
    var facebook: String? = null,
    var fatherNameSurname: String? = null,
    var motherNameSurname: String? = null,
    var occupationFather: String? = null,
    var occupationMother: String? = null,
    var estimateFatherMonthlyIncome: String? = null,
    var motherApproximateMonthlyIncome: String? = null,
    var fatherAddress: String? = null,
    var fatherAddressDetails: String? = null,
    var motherAddress: String? = null,
    var motherAddressDetails: String? = null,
    var congenitalDisease: String? = null,
    var paternalMemoryDisorder: String? = null,
    var maternalMemoryDisorder: String? = null,
    var fatherStatus: String? = null,
    var fatherStatusDetails: String? = null,
    var maternalStatus: String? = null,
    var maternalStatusDetails: String? = null,
    var haveSiblings: String? = null,
    var woman: String? = null,
    var addressValue: String? = null,
    var roundTripTravel: String? = null,
    var householdExpenses: String? = null,
    var familyDebt: String? = null,
    var contactInformation: String? = null,
    var emergencyContact: String? = null,
    var relationship: String? = null,
    var emergencyContactPhoneNumber: String? = null,
    var beautyEnhancement: String? = null,
    var beautyEnhancementDetails: String? = null,
    var man: String? = null,
    var personWho: String? = null,
    var parentInformation: String? = null,
    var talent: String? = null,
    var primaryEducation: String? = null,
    var middleSchool: String? = null,
    var highSchool: String? = null,
    var current: String? = null,  // Reserved keyword, rename the field if possible
    var specialWork: String? = null,
    var hope: String? = null,
    var committee: String? = null,
    var familyHistory: String? = null,
    var specialRequest: String? = null,
    val Status: String? = null,
    var studentIdCount: String? = null,

    var submissionDate: LocalDateTime? = null,
    var processedDate: LocalDateTime? = null,
    var appointmentDate: String? = null,
    var endMonth: String? = null,
    var startMonth: String? = null,
    var processedBy: String? = null,
)



fun mapToStudentApplicationHistoryDTO(
    applicationHistory: ApplicationHistoryEntities,
    studentDetails: StudentEntities?,
    studentIdCount: String
): StudentApplicationHistoryDTO {
    return StudentApplicationHistoryDTO(
        id = applicationHistory.registerId ?: applicationHistory.requestId?.toLong(), // Assumed conversion to Long
        requestId = applicationHistory.requestId,
        ApplicationId = applicationHistory.ApplicationId,
        studentId = studentDetails?.studentId ?: "",
        prefix = studentDetails?.prefix,
        firstName = studentDetails?.firstName ?: "",
        thaiName = studentDetails?.thaiName,
        lastName = studentDetails?.lastName ?: "",
        nickname = studentDetails?.nickname,
        dateOfBirth = studentDetails?.dateOfBirth,
        gender = studentDetails?.gender ?: "",
        nationalId = studentDetails?.nationalId,
        email = studentDetails?.email ?: "",
        phoneNumber = studentDetails?.phoneNumber,
        faculty = studentDetails?.faculty,
        fieldOfStudy = studentDetails?.fieldOfStudy,
        currentlyStudyingYear = studentDetails?.currentlyStudyingYear,
        studentType = studentDetails?.studentType,
        block = studentDetails?.block,
        currentGpa = studentDetails?.currentGpa,
        createDate = studentDetails?.createDate,
        updateDate = studentDetails?.updateDate,
        placeOfStudy = studentDetails?.placeOfStudy,
        otherPlace = studentDetails?.otherPlace,
        currentAddress = studentDetails?.currentAddress,
        studentResident = studentDetails?.studentResident,
        numberOfResidents = studentDetails?.numberOfResidents,
        currentProvince = studentDetails?.currentProvince,
        currentDistrict = studentDetails?.currentDistrict,
        currentSubdistrict = studentDetails?.currentSubdistrict,
        currentPostalCode = studentDetails?.currentPostalCode,
        addressAccordingToHouseRegistration = studentDetails?.addressAccordingToHouseRegistration,
        province = studentDetails?.province,


        district = studentDetails?.district,
        subdistrict = studentDetails?.subdistrict,
        postalCode = studentDetails?.postalCode,
        advisorNameSurname = studentDetails?.advisorNameSurname,
        advisorPhoneNumber = studentDetails?.advisorPhoneNumber,
        knowThePimSmartFundFrom = studentDetails?.knowThePimSmartFundFrom,
        additionalDetails = studentDetails?.additionalDetails,
        scholarshipReceived = studentDetails?.scholarshipReceived,
        otherScholarships = studentDetails?.otherScholarships,
        educationLoan = studentDetails?.educationLoan,
        graduatedFromSchool = studentDetails?.graduatedFromSchool,
        provinceSchool = studentDetails?.provinceSchool,
        lineId = studentDetails?.lineId,
        facebook = studentDetails?.facebook,
        fatherNameSurname = studentDetails?.fatherNameSurname,
        motherNameSurname = studentDetails?.motherNameSurname,
        occupationFather = studentDetails?.occupationFather,
        occupationMother = studentDetails?.occupationMother,
        estimateFatherMonthlyIncome = studentDetails?.estimateFatherMonthlyIncome,
        motherApproximateMonthlyIncome = studentDetails?.motherApproximateMonthlyIncome,
        fatherAddress = studentDetails?.fatherAddress,
        fatherAddressDetails = studentDetails?.fatherAddressDetails,
        motherAddress = studentDetails?.motherAddress,
        motherAddressDetails = studentDetails?.motherAddressDetails,
        congenitalDisease = studentDetails?.congenitalDisease,
        paternalMemoryDisorder = studentDetails?.paternalMemoryDisorder,
        maternalMemoryDisorder = studentDetails?.maternalMemoryDisorder,
        fatherStatus = studentDetails?.fatherStatus,
        fatherStatusDetails = studentDetails?.fatherStatusDetails,
        maternalStatus = studentDetails?.maternalStatus,
        maternalStatusDetails = studentDetails?.maternalStatusDetails,
        haveSiblings = studentDetails?.haveSiblings,
        woman = studentDetails?.woman,
        addressValue = studentDetails?.addressValue,
        roundTripTravel = studentDetails?.roundTripTravel,
        householdExpenses = studentDetails?.householdExpenses,
        familyDebt = studentDetails?.familyDebt,
        contactInformation = studentDetails?.contactInformation,
        emergencyContact = studentDetails?.emergencyContact,
        relationship = studentDetails?.relationship,
        emergencyContactPhoneNumber = studentDetails?.emergencyContactPhoneNumber,
        beautyEnhancement = studentDetails?.beautyEnhancement,
        beautyEnhancementDetails = studentDetails?.beautyEnhancementDetails,
        man = studentDetails?.man,
        personWho = studentDetails?.personWho,
        parentInformation = studentDetails?.parentInformation,
        talent = studentDetails?.talent,
        primaryEducation = studentDetails?.primaryEducation,
        middleSchool = studentDetails?.middleSchool,
        highSchool = studentDetails?.highSchool,
        current = studentDetails?.current,
        hope = studentDetails?.hope,
        committee = studentDetails?.committee,
        familyHistory = studentDetails?.familyHistory,


        Status = applicationHistory.status.toString() ,
        specialRequest = applicationHistory.specialRequest,

        studentIdCount = studentIdCount,

        submissionDate = applicationHistory.submissionDate,
        processedDate = applicationHistory.processedDate,
        appointmentDate = applicationHistory.appointmentDate,
        endMonth = applicationHistory.endMonth,
        startMonth = applicationHistory.startMonth,
        processedBy = applicationHistory.processedBy,





    )
}
