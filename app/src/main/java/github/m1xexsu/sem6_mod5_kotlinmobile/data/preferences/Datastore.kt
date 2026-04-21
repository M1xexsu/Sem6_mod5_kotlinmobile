package github.m1xexsu.sem6_mod5_kotlinmobile.data.preferences

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
//class Datastore (private val dataStore: DataStore<Preferences>): Application()
//{
    object Datastore{
        val IS_COLORED = booleanPreferencesKey("is_colored")
    }
//}