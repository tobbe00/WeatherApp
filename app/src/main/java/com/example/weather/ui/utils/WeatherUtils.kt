package com.example.weather.ui.utils



import androidx.annotation.DrawableRes
import com.example.weather.R

@DrawableRes
fun getWeatherIcon(weatherCode: Int): Int {
    return when (weatherCode) {
        0 -> R.drawable.clear0        // Clear sky
        1, 2, 3 -> R.drawable.partlycloudy2 // Partly cloudy
        40,41,42,43,44,45,46,47,49, 48 -> R.drawable.fog45   // Fog
        50,51,52, 53,54, 55,56,57,58,59, -> R.drawable.drizzle51 // Drizzle
        60,61,62, 63,64, 65,66,67,68,69, 65,80,81,82 -> R.drawable.rain61 // Rain
        70,71,72, 74, 76, 77, 73, 75,77,79,83,84,85,86,87,88,89, -> R.drawable.snow71 // Snow
        95, 96,97,98, 99 -> R.drawable.thunder91 // Thunderstorms
        else -> R.drawable.unknown   // Default or unknown weather
    }
}
