package com.longkai.stcarcontrol.st_exp.compose.data

import android.content.Context
import android.content.SharedPreferences
import com.longkai.stcarcontrol.st_exp.STCarApplication
import com.longkai.stcarcontrol.st_exp.Utils.writeToExternalStorage
import com.longkai.stcarcontrol.st_exp.ai.esrFsaList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

interface AIRepo {
    val initStateFlow: StateFlow<Boolean>

    val abilityResult: Flow<String>

    val keyWordList: List<List<String>>

    fun initSuccess()

    fun emitAbilityResult(result: String)

    fun updateKeyword(index: Int, value: String)

    fun updateKeyword(list: List<String>)

    fun getKeyWordFromPrefs(): List<String>

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

    override fun emitAbilityResult(result: String) {
        _abilityResult.update { result }
    }

    // deprecated
    override fun updateKeyword(index: Int, value: String) {
        _keyWordList[index] = _keyWordList[index].toMutableList().apply { add(value) }.toList()
    }

    override fun updateKeyword(list: List<String>) {
        val modifiedList = esrFsaList.toMutableList()
        var script = modifiedList[0].removeSuffix(";")
        list.forEachIndexed { index, keyWord ->
            if (keyWord.isNotEmpty()) {
                script += "|${keyWord}"
                updateKeyword(index, keyWord)
            }
        }
        script += ";"
        modifiedList[0] = script

        saveStringListToSharedPref(
            context = STCarApplication.CONTEXT,
            key = KEY_KEY_WORDS,
            stringList = list,
        )

        try {
            writeToExternalStorage(modifiedList)
        } catch (_: Exception) {

        }
    }

    override fun getKeyWordFromPrefs(): List<String> {
        return getStringListFromSharedPref(STCarApplication.CONTEXT, KEY_KEY_WORDS) ?: emptyList()
    }

    override fun initKeyWordsList() {
        val keywords = getStringListFromSharedPref(STCarApplication.CONTEXT, KEY_KEY_WORDS) ?: emptyList()
        keywords.forEachIndexed { index, keyWord ->
            if (keyWord.isNotEmpty()) {
                updateKeyword(index, keyWord)
            }
        }
    }

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
        editor.putStringSet(key, stringList.toSet())

        // Apply changes
        editor.apply()
    }

    // Function to retrieve the list of strings from SharedPreferences
    private fun getStringListFromSharedPref(context: Context, key: String): List<String>? {
        // Get SharedPreferences
        val sharedPref: SharedPreferences =
            context.getSharedPreferences("AIRepo", Context.MODE_PRIVATE)

        // Retrieve the Set<String> from SharedPreferences
        val stringSet = sharedPref.getStringSet(key, null)

        // Convert Set<String> back to List<String> and return it
        return stringSet?.toList()
    }

    companion object {
        private val KEY_KEY_WORDS = "KEY_KEY_WORDS"
    }
}
