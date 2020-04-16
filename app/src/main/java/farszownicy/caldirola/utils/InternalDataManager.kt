package farszownicy.caldirola.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

fun writeObjectToSharedPreferences(
    ctx : Context,
    fileName: String,
    key: String,
    obj: Any
) {
    val sharedPref = ctx.getSharedPreferences(fileName, Context.MODE_PRIVATE)
    val jsonString = Gson().toJson(obj)

    with(sharedPref.edit()){
        putString(key, jsonString)
        commit()
    }
}

inline fun <reified T> readObjectsFromSharedPreferences(
    ctx: Context,
    fileName: String,
    key: String
) : T? {
    val sharedPref = ctx.getSharedPreferences(fileName, Context.MODE_PRIVATE)
    if(sharedPref.contains(key)) {
        val type = object : TypeToken<T>() {}.type
        return Gson().fromJson<T>(sharedPref.getString(key, ""), type)
    }
    return null
}