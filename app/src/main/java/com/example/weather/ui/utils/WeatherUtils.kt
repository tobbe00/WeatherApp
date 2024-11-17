package com.example.weather.ui.utils



import androidx.annotation.DrawableRes
import com.example.weather.R

@DrawableRes
fun getWeatherIcon(weatherCode: Int): Int {
    return when (weatherCode) {
        0 -> R.drawable.clear0        // Clear sky
        1, 2, 3 -> R.drawable.partlycloudy2 // Partly cloudy
        45, 48 -> R.drawable.fog45   // Fog
        51, 53, 55 -> R.drawable.drizzle51 // Drizzle
        61, 63, 65 -> R.drawable.rain61 // Rain
        71, 73, 75 -> R.drawable.snow71 // Snow
        95, 96, 99 -> R.drawable.thunder91 // Thunderstorms
        else -> R.drawable.unknown   // Default or unknown weather
    }
}
