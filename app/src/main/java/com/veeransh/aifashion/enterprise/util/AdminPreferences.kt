package com.veeransh.aifashion.enterprise.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "admin_prefs")

@Singleton
class AdminPreferences @Inject constructor(@ApplicationContext private val context: Context) {

    private val ADMIN_PIN = stringPreferencesKey("admin_pin")
    private val ENABLE_COD = booleanPreferencesKey("enable_cod")
    private val MIN_COD = intPreferencesKey("min_cod")
    private val MAX_COD = intPreferencesKey("max_cod")
    private val COD_CHARGE = intPreferencesKey("cod_charge")
    private val BANANA_AI_ENABLED = booleanPreferencesKey("banana_ai_enable")

    val adminPin: Flow<String> = context.dataStore.data.map { it[ADMIN_PIN] ?: "2026" }
    val enableCod: Flow<Boolean> = context.dataStore.data.map { it[ENABLE_COD] ?: true }
    val minCod: Flow<Int> = context.dataStore.data.map { it[MIN_COD] ?: 0 }
    val maxCod: Flow<Int> = context.dataStore.data.map { it[MAX_COD] ?: 5000 }
    val codCharge: Flow<Int> = context.dataStore.data.map { it[COD_CHARGE] ?: 50 }
    val bananaAiEnabled: Flow<Boolean> = context.dataStore.data.map { it[BANANA_AI_ENABLED] ?: true }

    suspend fun updateAdminPin(pin: String) {
        context.dataStore.edit { it[ADMIN_PIN] = pin }
    }

    suspend fun updateCodSettings(enabled: Boolean, min: Int, max: Int, charge: Int) {
        context.dataStore.edit {
            it[ENABLE_COD] = enabled
            it[MIN_COD] = min
            it[MAX_COD] = max
            it[COD_CHARGE] = charge
        }
    }

    suspend fun updateBananaAiEnabled(enabled: Boolean) {
        context.dataStore.edit { it[BANANA_AI_ENABLED] = enabled }
    }
}
