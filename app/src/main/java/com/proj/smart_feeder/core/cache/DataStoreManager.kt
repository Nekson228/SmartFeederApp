package com.proj.smart_feeder.core.cache

import android.content.Context
import android.net.Uri
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import java.io.FileOutputStream

private val Context.dataStore by preferencesDataStore(name = "smart_feeder_cache")

class DataStoreManager(private val context: Context) {

    companion object {
        val FEEDER_STATE_KEY = stringPreferencesKey("feeder_state")
        val PROFILES_KEY = stringPreferencesKey("profiles")
        val SETTINGS_KEY = stringPreferencesKey("settings")
        val BOWL_ID_KEY = stringPreferencesKey("bowl_id")
        fun getPetPhotoKey(petId: String) = stringPreferencesKey("pet_photo_$petId")
    }

    suspend fun savePetPhoto(petId: String, uriString: String) {
        val permanentUri = saveImageToInternalStorage(uriString)
        if (permanentUri != null) {
            context.dataStore.edit { preferences ->
                preferences[getPetPhotoKey(petId)] = permanentUri
            }
        }
    }

    fun getPetPhoto(petId: String): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[getPetPhotoKey(petId)]
        }
    }

    suspend fun saveToCache(key: androidx.datastore.preferences.core.Preferences.Key<String>, json: String) {
        context.dataStore.edit { preferences ->
            preferences[key] = json
        }
    }

    fun getFromCache(key: androidx.datastore.preferences.core.Preferences.Key<String>): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[key]
        }
    }

    
    fun saveImageToInternalStorage(uriString: String): String? {
        return try {
            val uri = Uri.parse(uriString)
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val fileName = "pet_photo_${System.currentTimeMillis()}.jpg"
            val file = File(context.filesDir, fileName)
            
            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            
            Uri.fromFile(file).toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

