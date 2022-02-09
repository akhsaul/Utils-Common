package me.akhsaul.common.network

import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import me.akhsaul.common.enums.HTTPMethod
import java.io.File

object Connector {
    private var mapBuilder = mutableMapOf<String, Any>()
    private var body: RequestBody? = null
    private var formEncoded = mutableMapOf<String, String>()

    fun get(url: String) = apply {
        mapBuilder["url"] = url
    }

    fun head(url: String) {
        mapBuilder["url"] = url
        mapBuilder["method"] = HTTPMethod.HEAD
    }

    fun post(url: String) {
        mapBuilder["url"] = url
        mapBuilder["method"] = HTTPMethod.POST
    }

    fun upload(url: String) {
        mapBuilder["url"] = url
        mapBuilder["method"] = HTTPMethod.POST
    }

    fun addBody(file: File) {
        body = file.asRequestBody("".toMediaType())
    }

    fun addBody(bytes: ByteArray) {
        body = bytes.toRequestBody("".toMediaType())
    }

    /**
     * use this if you have json class
     * */
    fun addBodyJson(data: Any) {
    }

    fun addBodyEncoded(key: String, value: String) {
        formEncoded[key] = value
    }

    fun addBodyEncoded(mapping: Map<String, String>) {
        formEncoded.putAll(mapping)
    }

    fun build() {
        if (formEncoded.isNotEmpty()) {
        }
        val a = FormBody.Builder()
            .add("", "")
            .addEncoded("", "")
            .build()
        MultipartBody.Builder()
            .addPart(a)
            .setType(MultipartBody.MIXED)
            .build()
    }

    fun setPriority() {
    }
}