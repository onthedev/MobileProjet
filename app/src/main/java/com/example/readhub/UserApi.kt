package com.example.readhub

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface UserApi {
    @GET("/login/{user_name}/{password}")
    fun loginUser(
        @Path("user_name") user_name:String,
        @Path("password") password:String
    ): Call<LoginClass>

    @FormUrlEncoded
    @POST("/insertUser")
    fun registerUser(
        @Field("user_name") user_name:String,
        @Field("email") email:String,
        @Field("password") password:String,
        ): Call<LoginClass>

    @FormUrlEncoded
    @POST("/addUserInterested")
    fun addUserInterested(
        @Field("itemselected") itemselected:String,
    ): Call<LoginClass>

    @GET("/search/{book_name}")
    fun searchBook(
        @Path("book_name") book_name:String,
    ): Call<Book>

    @Multipart
    @POST("/insertUpload")
    fun uploadFile(
        @Part bookPart: MultipartBody.Part,
        @Part("book_name") bookName: RequestBody,
        @Part("description") description: RequestBody,
        @Part("writer_name") writer_name: RequestBody,
        @Part("pub_name") pub_name: RequestBody,
        @Part("catagory") catagory: RequestBody,
        ): Call<Book>
//    @FormUrlEncoded
//    @POST("/insertBook")
//    fun insertBook(
//        @Field("book_name") book_name:String,
//        @Field("description") description:String,
//
//        ): Call<LoginClass>

    companion object{
        fun create(): UserApi{
            val stuClient: UserApi = Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(UserApi::class.java)
            return  stuClient
        }
    }
}
