package com.longkai.stcarcontrol.st_exp.Utils

import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.Files
import android.provider.MediaStore.Images.Media
import com.google.gson.Gson
import com.longkai.stcarcontrol.st_exp.model.SoundsInfo
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader

object FileUtils10 {
  fun getFilesUnderDownloadST(
    context: Context
  ): List<Triple<String, Uri?, SoundsInfo?>> {
    val projection = arrayOf(
      Media._ID,
      MediaStore.Downloads.DATA,
      MediaStore.Downloads.DISPLAY_NAME
    )
    val selection: String? = null
    val selectionArgs: Array<String>? = null
    val sortOrder: String? = null

    val uriMap = mutableMapOf<String, Uri?>()
    val soundsInfoMap = mutableMapOf<String, SoundsInfo?>()

    // DocumentsContract.buildChildDocumentsUriUsingTree()

    if (android.os.Build.VERSION.SDK_INT >= 29) { // 30
      // path   Android/data/com.longkai.stcarcontrol.st_exp/files/Download/
      val downloadsFolder = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
      val stwavFolder = File(downloadsFolder, "stwav")
      val files = stwavFolder.listFiles()
      files?.sortByDescending { it.lastModified() }
      files?.forEach { file ->
        var wavUri: Uri? = null
        var soundsInfo: SoundsInfo? = null
        if (file.isFile) {
          if (file.name.endsWith(".json")) {
            val name = file.name.substringBefore(".json")
            println("longkai22 Name " + name)
            val contentUri = Uri.fromFile(file)
            soundsInfo = readSoundsInfoFile(contentUri, context)
            soundsInfoMap[name] = soundsInfo
          } else {
            val name = file.name.substringBefore(".")
            println("longkai22 Name " + name)
            wavUri = Uri.fromFile(file)
            uriMap[name] = wavUri
          }
        }
      }
    } else { // only work on android 10
      val cursor =
        context.contentResolver.query(
          // android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI,
          Files.getContentUri(MediaStore.VOLUME_EXTERNAL),
          projection,
          selection,
          selectionArgs,
          sortOrder
        )

      if (cursor != null) {
        cursor.moveToFirst()
        //iterate over rows
        var wavUri: Uri? = null
        var soundsInfo: SoundsInfo? = null
        for (i in 0 until cursor.count) {
          //iterate over the columns

          if (cursor.getString(1).contains("emulated/0/Download/stwav")) {
            val idIndex = cursor.getColumnIndex(Media._ID)
            val id = cursor.getLong(idIndex)
            println("longkai22 id $id")
            println("longkai22 " + cursor.columnNames[1] + " " + cursor.getString(1))
            println("longkai22 " + cursor.columnNames[2] + " " + cursor.getString(2))

            if (cursor.getString(2).endsWith(".json")) {
              val name = cursor.getString(2).substringBefore(".json")
              println("longkai22 Name " + name)
              val contentUri = Uri.fromFile(File(cursor.getString(1)))
              soundsInfo = readSoundsInfoFile(contentUri, context)
              soundsInfoMap[name] = soundsInfo
            } else {
              val name = cursor.getString(2).substringBefore(".")
              println("longkai22 Name " + name)
              wavUri = Uri.fromFile(File(cursor.getString(1)))
              uriMap[name] = wavUri
            }

            // if (cursor.getString(2) == "st01.json") {
            //   val contentUri = Uri.fromFile(File(cursor.getString(1)))
            //   soundsInfo = readSoundsInfoFile(contentUri, context)
            // }

            // if (cursor.getString(2) == "st01.wav") {
            //   wavUri = Uri.fromFile(File(cursor.getString(1)))
            // }
          }
          for (j in cursor.columnNames.indices) {
            // Append the column value to the string builder and delimit by \n
            // dirContent.append(cursor.getString(j))
            // dirContent.append("\n")
            println("longkai22 " + cursor.columnNames[j] + " " + cursor.getString(j))
          }
          cursor.moveToNext()
        }
        //close the cursor
        cursor.close()
      }
    }




    val resultList = mutableListOf<Triple<String, Uri?, SoundsInfo?>>()
    soundsInfoMap.forEach { entry ->
      resultList.add(Triple(entry.key, uriMap[entry.key], soundsInfoMap[entry.key]))

      // resultMap[entry.key] = Pair(
      //   uriMap[entry.key], soundsInfoMap[entry.key]
      // )
    }

    return resultList
  }

  fun readWAVFromInternalStorage(
    context: Context,
  ): String? {
    val cw = ContextWrapper(context)
    // path to /data/data/packageName/app_data/visualSearch
    val appDir = cw.filesDir
    val subDir = File(appDir, "stwav")
    if (!subDir.exists()) subDir.mkdir()

    if (subDir.isDirectory) {

      println("longkai22 " + android.os.Build.VERSION.SDK_INT)
      appDir.listFiles()?.forEach { file ->
        println("longkai22 " + file.path)
      }
    }
    // Create imageDir
    return "a"
  }

  fun readSoundsInfoFile(
    uri: Uri,
    context: Context
  ): SoundsInfo? {
    try {
      val input = context.getContentResolver().openInputStream(uri)

      val r = BufferedReader(InputStreamReader(input))
      val text = StringBuilder()
      var line: String?
      while (r.readLine().also { line = it } != null) {
        text.append(line).append('\n')
      }

      val content = text.toString()
      val gson = Gson() // Or use new GsonBuilder().create();
      val soundsInfo = gson.fromJson(content, SoundsInfo::class.java)
      // println("longkai22 " + soundsInfo)
      r.close()
      return soundsInfo
    } catch (e: IOException) {
      //You'll need to add proper error handling here
      println(e.message)
      return null
    }
  }
}