package com.yash.edusmart.api

import android.os.Build
import androidx.annotation.RequiresApi
import com.yash.edusmart.data.TimeTableEntry
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query
import java.time.LocalDate

interface StudentApi
{

    @GET("/student/getHolidays")
    suspend fun getHolidays(): Response<List<HolidayEntity>>
    @GET("/student/getTimeTableByDay")
    suspend fun getTimeTableByBranch(
        @Query("branch") branch: String,
        @Query("semester") semester: String
    ): Response<List<TimeTableEntry>>

    @GET("/student/getAttendance")
    suspend fun getAttendance(
        @Query("email") email: String
    ): Response<List<AttendanceDTO>>

    @POST("/student/getStudentsList")
    suspend fun getStudentsByBranchAndSemester(
        @Body studentsListDTO : StudentsListDTO
    ): Response<List<StudentData>>

    @GET("/student/getAllSubjects")
    suspend fun getSubjects(
        @Query("branch") branch: String,
        @Query("semester")semester: String
    ): Response<List<String>>

    @GET("/student/getAllAssignmentsByBranchAndSem")
    suspend fun getAllAssign(
        @Query("branch") branch: String,
        @Query("sem") sem: String
    ): Response<List<AssignmentStudent>>

    @POST("/student/markAssignment")
    suspend fun markAssignment(
        @Query("idAss") idAss: Long,
        @Query("enroll")enroll: String
    ): Response<String>

    @GET("/student/getMessagesByBranchAndSem")
    suspend fun getGroupMessages(
        @Query("branch") branch: String,
        @Query("sem") sem: String
    ): Response<List<ChatEntity>>

    @GET("/student/getPvtMsg")
    suspend fun getPrivateConversation(
        @Query("email") email: String,
        @Query("receiverEmail") receiverEmail: String
    ): Response<List<ChatEntity>>

    @PUT("/student/addPvtMsg")
    suspend fun addMsg(
        @Body chatEntity: ChatEntity
    ): Response<String>

    @GET("/student/getAllTeachers")
    suspend fun getAllTeacher(
        @Query("branch") branch: String,
        @Query("sem") sem: String
    ): Response<List<TeacherDTO>>

    @POST("/student/presign-download")
    suspend fun preSignDownload(
        @Body request: PresignDownloadRequest
    ): Response<PresignDownloadResponse>

    @POST("/AI/upload-doc")
    suspend fun presignUpload(
        @Body req : PresignUploadRequest
    ): Response<PresignUploadResponse>

    @POST("/AI/create-vector")
    suspend fun createVector(
        @Body file_url: String
    ): Response<Unit>

    @POST("/AI/chat-general")
    suspend fun generalChat(
        @Body request:GeneralRequest
    ): Response<String>

    @POST("/AI/rag")
    suspend fun rag(
        @Body request: ChatRequest
    ): Response<String>

    @POST("/AI/plan")
    suspend fun plannerS(
        @Body query: String
    ): Response<String>







}


data class ChatRequest(
    val query: String,
    val history:List<String> = emptyList()
)

data class GeneralRequest @RequiresApi(Build.VERSION_CODES.O) constructor(
    val user_query: String,
    val current_date: String= LocalDate.now().toString(),
    val history:List<String> = emptyList()
)


data class TeacherDTO(
    val name: String,
    val email: String
)

data class ChatEntity(
    val id: Long? = null,
    val msg: String,
    val isSent: Boolean,
    val sender: String,
    val receiver: String,
    val timeStamp: Long
)


data class HolidayEntity(
    val id: Long,
    val date : String,
    val occasion: String
)

data class AssignmentStudent(
    val id: Long,
    val branch: String,
    val sem: String,
    val assignment: String,
    val deadline: Long,
    val path: String
)

data class StudentData(
    val email: String,
    val name: String
)

data class StudentsListDTO(
    val branch: String,
    val semester: String
)

data class AttendanceDTO(
    val date: String,
    val status: String,
    val subject: String
)