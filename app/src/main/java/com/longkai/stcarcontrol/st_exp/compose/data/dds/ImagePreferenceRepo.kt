package com.longkai.stcarcontrol.st_exp.compose.data.dds

import com.longkai.stcarcontrol.st_exp.STCarApplication
import com.longkai.stcarcontrol.st_exp.Utils.SharedPreferencesUtil

/**
 * Simple preference repository for storing up to [IMAGE_COUNT] image uri strings
 * used by DDS settings page. Keys: IMAGE_URI_PREFIX + index
 */
object ImagePreferenceRepo {
    const val IMAGE_COUNT = 10
    private const val IMAGE_URI_PREFIX = "DDS_IMAGE_URI_"

    fun getImageUris(count: Int = IMAGE_COUNT): List<String?> =
        (0 until count).map { i -> getImageUri(i) }

    fun getImageUri(index: Int): String? {
        if (index !in 0 until IMAGE_COUNT) return null
        val any = SharedPreferencesUtil.get(
            STCarApplication.CONTEXT,
            IMAGE_URI_PREFIX + index,
            "" // non-null default so getString is used
        )
        val value = any as? String
        return if (value.isNullOrBlank()) null else value
    }

    fun saveImageUri(index: Int, uri: String) {
        if (index !in 0 until IMAGE_COUNT) return
        SharedPreferencesUtil.put(
            STCarApplication.CONTEXT,
            IMAGE_URI_PREFIX + index,
            uri
        )
    }

    fun clearImageUri(index: Int) {
        if (index !in 0 until IMAGE_COUNT) return
        SharedPreferencesUtil.remove(
            STCarApplication.CONTEXT,
            IMAGE_URI_PREFIX + index
        )
    }
}
