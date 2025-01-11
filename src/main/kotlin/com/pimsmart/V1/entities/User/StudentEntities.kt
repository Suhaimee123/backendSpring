package com.pimsmart.V1.entities.User

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "student") // Match the exact table name
class StudentEntities (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null , // Corresponding to `id` in the SQL table

    @Column(name = "student_id")
    var studentId: String? = null,

    @Column(name = "prefix")
    var prefix: String? = null,

    @Column(name = "first_name")
    var firstName: String? = null,

    @Column(name = "thai_name")
    var thaiName: String? = null,

    @Column(name = "last_name")
    var lastName: String? = null,

    @Column(name = "nickname")
    var nickname: String? = null,

    @Column(name = "date_of_birth")
    var dateOfBirth: String? = null,  // Use `LocalDate` for date fields

    @Column(name = "gender")
    var gender: String? = null,

    @Column(name = "national_id")
    var nationalId: String? = null,

    @Column(name = "email")
    var email: String? = null,

    @Column(name = "phone_number")
    var phoneNumber: String? = null,

    @Column(name = "faculty")
    var faculty: String? = null,

    @Column(name = "field_of_study")
    var fieldOfStudy: String? = null,

    @Column(name = "currently_studying_year")
    var currentlyStudyingYear: String? = null,

    @Column(name = "student_type")
    var studentType: String? = null,

    @Column(name = "block")
    var block: String? = null,

    @Column(name = "current_gpa")
    var currentGpa: BigDecimal? = null,  // Use `BigDecimal` for numeric fields

    @Column(name = "create_date")
    var createDate: LocalDateTime? = null,  // Use `LocalDateTime` for timestamp fields

    @Column(name = "update_date")
    var updateDate: LocalDateTime? = null,

    @Column(name = "place_of_study")
    var placeOfStudy: String? = null,

    @Column(name = "other_place")
    var otherPlace: String? = null,

    @Column(name = "current_address")
    var currentAddress: String? = null,  // Use `String?` for `text` fields

    @Column(name = "student_resident")
    var studentResident: String? = null,

    @Column(name = "number_of_residents")
    var numberOfResidents: Int? = null,

    @Column(name = "current_province")
    var currentProvince: String? = null,

    @Column(name = "current_district")
    var currentDistrict: String? = null,

    @Column(name = "current_subdistrict")
    var currentSubdistrict: String? = null,

    @Column(name = "current_postal_code")
    var currentPostalCode: String? = null,

    @Column(name = "address_according_to_house_registration")
    var addressAccordingToHouseRegistration: String? = null,

    @Column(name = "province")
    var province: String? = null,

    @Column(name = "district")
    var district: String? = null,

    @Column(name = "subdistrict")
    var subdistrict: String? = null,

    @Column(name = "postal_code")
    var postalCode: String? = null,

    @Column(name = "advisor_name_surname")
    var advisorNameSurname: String? = null,

    @Column(name = "advisor_phone_number")
    var advisorPhoneNumber: String? = null,

    @Column(name = "know_the_pim_smart_fund_from")
    var knowThePimSmartFundFrom: String? = null,

    @Column(name = "additional_details")
    var additionalDetails: String? = null,

    @Column(name = "scholarship_received")
    var scholarshipReceived: String? = null,

    @Column(name = "other_scholarships")
    var otherScholarships: String? = null,

    @Column(name = "education_loan")
    var educationLoan: String? = null,

    @Column(name = "graduated_from_school")
    var graduatedFromSchool: String? = null,

    @Column(name = "province_school")
    var provinceSchool: String? = null,

    @Column(name = "line_id")
    var lineId: String? = null,

    @Column(name = "facebook")
    var facebook: String? = null,

    @Column(name = "father_name_surname")
    var fatherNameSurname: String? = null,

    @Column(name = "mother_name_surname")
    var motherNameSurname: String? = null,

    @Column(name = "occupation_father")
    var occupationFather: String? = null,

    @Column(name = "occupation_mother")
    var occupationMother: String? = null,

    @Column(name = "estimate_father_monthly_income")
    var estimateFatherMonthlyIncome: String? = null,

    @Column(name = "mother_approximate_monthly_income")
    var motherApproximateMonthlyIncome: String? = null,

    @Column(name = "father_address")
    var fatherAddress: String? = null,

    @Column(name = "father_address_details")
    var fatherAddressDetails: String? = null,

    @Column(name = "mother_address")
    var motherAddress: String? = null,

    @Column(name = "mother_address_details")
    var motherAddressDetails: String? = null,

    @Column(name = "congenital_disease")
    var congenitalDisease: String? = null,

    @Column(name = "paternal_memory_disorder")
    var paternalMemoryDisorder: String? = null,

    @Column(name = "maternal_memory_disorder")
    var maternalMemoryDisorder: String? = null,

    @Column(name = "father_status")
    var fatherStatus: String? = null,

    @Column(name = "father_status_details")
    var fatherStatusDetails: String? = null,

    @Column(name = "maternal_status")
    var maternalStatus: String? = null,

    @Column(name = "maternal_status_details")
    var maternalStatusDetails: String? = null,

    @Column(name = "have_siblings")
    var haveSiblings: String? = null,

    @Column(name = "woman")
    var woman: String? = null,

    @Column(name = "address_value")
    var addressValue: String? = null,

    @Column(name = "round_trip_travel")
    var roundTripTravel: String? = null,

    @Column(name = "household_expenses")
    var householdExpenses: String? = null,

    @Column(name = "family_debt")
    var familyDebt: String? = null,

    @Column(name = "contact_information")
    var contactInformation: String? = null,

    @Column(name = "emergency_contact")
    var emergencyContact: String? = null,

    @Column(name = "relationship")
    var relationship: String? = null,

    @Column(name = "emergency_contact_phone_number")
    var emergencyContactPhoneNumber: String? = null,

    @Column(name = "beauty_enhancement")
    var beautyEnhancement: String? = null,

    @Column(name = "beauty_enhancement_details")
    var beautyEnhancementDetails: String? = null,

    @Column(name = "man")
    var man: String? = null,

    @Column(name = "person_who")
    var personWho: String? = null,

    @Column(name = "parent_information")
    var parentInformation: String? = null,

    @Column(name = "talent")
    var talent: String? = null,

    @Column(name = "primary_education")
    var primaryEducation: String? = null,

    @Column(name = "middle_school")
    var middleSchool: String? = null,

    @Column(name = "high_school")
    var highSchool: String? = null,

    @Column(name = "current")
    var current: String? = null,  // Reserved keyword, rename the field if possible

    @Column(name = "special_work")
    var specialWork: String? = null,

    @Column(name = "hope")
    var hope: String? = null,

    @Column(name = "committee")
    var committee: String? = null,

    @Column(name = "family_history")
    var familyHistory: String? = null,

    @Column(name = "special_request")
    var specialRequest: String? = null,






    // Any additional mappings...
)
