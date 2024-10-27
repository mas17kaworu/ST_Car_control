package com.longkai.stcarcontrol.st_exp.compose.data

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.longkai.stcarcontrol.st_exp.STCarApplication
import com.longkai.stcarcontrol.st_exp.Utils.writeToExternalStorage
import com.longkai.stcarcontrol.st_exp.ai.esrFsaList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface AIRepo {
    val initStateFlow: StateFlow<Boolean>

    val abilityResult: Flow<String>

    val keyWordList: List<List<String>>

    fun initSuccess()

    fun emitAbilityResult(result: String)

    fun updateKeyword(index: Int, value: String)

    fun updateKeyword(cnList: List<String>, enList: List<String>)

    fun getKeyWordFromPrefs(key: String): List<String>

    fun initKeyWordsList()
}

class AIRepoImpl : AIRepo {
    private val _initStateFlow = MutableStateFlow(false)
    private val _abilityResult = MutableStateFlow("")
    private val _keyWordList: MutableList<List<String>> = arrayListOf(
        listOf("打开第一个服务", "我无聊了", "I am boring"), // 第一个服务默认的命令
        listOf("打开第二个务"),
        listOf("打开第三个务"),
        listOf("打开第四个务"),
    )

    override val initStateFlow: StateFlow<Boolean> = _initStateFlow
    override val abilityResult: Flow<String> = _abilityResult
    override val keyWordList: List<List<String>>
        get() = _keyWordList.toList()

    override fun initSuccess() {
        _initStateFlow.update {
            true
        }
    }

    private var job: Job? = null

    override fun emitAbilityResult(result: String) {
        job?.cancel()
        _abilityResult.update { result }
        job = CoroutineScope(Dispatchers.Default).launch {
            delay(3000)
            _abilityResult.update { "" }
        }
    }

    // deprecated
    override fun updateKeyword(index: Int, value: String) {
        if (_keyWordList.size > index) {
            _keyWordList[index] = _keyWordList[index].toMutableList().apply { add(value) }.toList()
        }
    }

    override fun updateKeyword(cnList: List<String>, enList: List<String>) {
        val modifiedList = esrFsaList.toMutableList()
        var cnScript = modifiedList[0].removeSuffix(";")
        var enScript = modifiedList[1].removeSuffix(";")
        cnList.forEachIndexed { index, keyWord ->
            if (keyWord.isNotEmpty()) {
                cnScript += "|${keyWord}"
                updateKeyword(index, keyWord)
            }
        }
        enList.forEachIndexed { index, keyWord ->
            if (keyWord.isNotEmpty()) {
                enScript += "|${keyWord}"
                updateKeyword(index, keyWord)
            }
        }
        cnScript += ";"
        enScript += ";"
        modifiedList[0] = cnScript
        modifiedList[1] = enScript

        saveStringListToSharedPref(
            context = STCarApplication.CONTEXT,
            key = KEY_CN_KEY_WORDS,
            stringList = cnList,
        )
        saveStringListToSharedPref(
            context = STCarApplication.CONTEXT,
            key = KEY_EN_KEY_WORDS,
            stringList = enList,
        )

        try {
            writeToExternalStorage(modifiedList)
        } catch (_: Exception) {

        }
    }

    override fun getKeyWordFromPrefs(key: String): List<String> {
        return getStringListFromSharedPref(STCarApplication.CONTEXT, key) ?: emptyList()
    }

    override fun initKeyWordsList() {
        val cnKeywords = getStringListFromSharedPref(STCarApplication.CONTEXT, KEY_CN_KEY_WORDS) ?: emptyList()
        cnKeywords.forEachIndexed { index, keyWord ->
            if (keyWord.isNotEmpty()) {
                updateKeyword(index, keyWord)
            }
        }
        val enKeywords = getStringListFromSharedPref(STCarApplication.CONTEXT, KEY_EN_KEY_WORDS) ?: emptyList()
        enKeywords.forEachIndexed { index, keyWord ->
            if (keyWord.isNotEmpty()) {
                updateKeyword(index, keyWord)
            }
        }
    }

    val gson = Gson()
    // Function to save the list of strings to SharedPreferences
    private fun saveStringListToSharedPref(
        context: Context,
        key: String,
        stringList: List<String>
    ) {
        // Get SharedPreferences editor
        val sharedPref: SharedPreferences =
            context.getSharedPreferences("AIRepo", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        // Convert the list to a Set<String> to store in SharedPreferences
//        editor.putStringSet(key, HashSet(stringList))

        val json = gson.toJson(stringList)
        editor.putString(key, json).apply()

        // Apply changes
        editor.apply()
    }

    // Function to retrieve the list of strings from SharedPreferences
    private fun getStringListFromSharedPref(context: Context, key: String): List<String>? {
        // Get SharedPreferences
        val sharedPref: SharedPreferences =
            context.getSharedPreferences("AIRepo", Context.MODE_PRIVATE)

        val json = sharedPref.getString(key, null)
        return if (json != null) {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }

        // Retrieve the Set<String> from SharedPreferences
//        val stringSet = sharedPref.getStringSet(key, null)


        // Convert Set<String> back to List<String> and return it
//        return stringSet?.toList()
    }

    companion object {
        val KEY_CN_KEY_WORDS = "KEY_CN_KEY_WORDS_JSON" //KEY_CN_KEY_WORDS
        val KEY_EN_KEY_WORDS = "KEY_EN_KEY_WORDS_JSON"
    }
}
