package com.dhritiman.aura.apps

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings


data class SystemApp(
    val name: String,
    val intent: Intent
)



class SystemAppResolver(
    private val context: Context
) {


    fun resolve(
        name: String
    ): Intent? {


        return when(
            name.lowercase()
        ) {


            "settings" -> {

                Intent(
                    Settings.ACTION_SETTINGS
                )
            }


            "camera" -> {

                Intent(
                    android.provider.MediaStore.ACTION_IMAGE_CAPTURE
                )
            }


            "wifi" -> {

                Intent(
                    Settings.ACTION_WIFI_SETTINGS
                )
            }


            "bluetooth" -> {

                Intent(
                    Settings.ACTION_BLUETOOTH_SETTINGS
                )
            }


            else -> {

                null
            }
        }
    }
}