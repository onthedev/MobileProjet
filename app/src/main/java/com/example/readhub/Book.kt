package com.example.readhub

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Book(
    @Expose
    @SerializedName("book_img") val book_img: String,

    @Expose
    @SerializedName("book_path") val book_path: String,

    @Expose
    @SerializedName("book_name") val book_name: String,

    @Expose
    @SerializedName("description") val description: String,

    @Expose
    @SerializedName("writer_name") val writer_name: String,

    @Expose
    @SerializedName("btype_id") val btype_id: String,

    @Expose
    @SerializedName("pub_id") val pub_id: String,
    )
