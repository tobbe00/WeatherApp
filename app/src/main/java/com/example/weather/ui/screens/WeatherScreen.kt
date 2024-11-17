package com.example.weather.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.weather.ui.utils.getWeatherIcon
import com.example.weather.ui.viewmodels.WeatherViewModel

@Composable
fun WeatherScreen(weatherViewModel: WeatherViewModel) {
    var latitude by remember { mutableStateOf(TextFieldValue("")) }
    var longitude by remember { mutableStateOf(TextFieldValue("")) }
    val weatherData = weatherViewModel.weatherData.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Inputfält för latitud och longitud
        OutlinedTextField(
            value = latitude,
            onValueChange = { latitude = it },
            label = { Text("Enter Latitude") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = longitude,
            onValueChange = { longitude = it },
            label = { Text("Enter Longitude") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        Button(
            onClick = {
                val lat = latitude.text.toFloatOrNull()
                val lon = longitude.text.toFloatOrNull()
                if (lat != null && lon != null) {
                    weatherViewModel.fetchWeather(lat, lon)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("Fetch Weather")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Visa väderdata
        weatherData?.hourly?.let { hourly ->
            val timeList = hourly.time ?: emptyList()
            val temperatureList = hourly.temperature_2m ?: emptyList()
            val weatherCodeList = hourly.weather_code ?: emptyList()

            val combinedData = timeList.zip(temperatureList).zip(weatherCodeList) { (time, temp), code ->
                Triple(time, temp, code)
            }

            LazyColumn {
                items(combinedData) { (time, temperature, weatherCode) ->
                    WeatherItem(time, temperature, weatherCode)
                }
            }
        } ?: Text("No weather data available", modifier = Modifier.padding(top = 16.dp))
    }
}
/*
@Composable
fun WeatherItem(time: String, temperature: Float, weatherCode: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(text = "Time: $time", modifier = Modifier.weight(1f))
        Text(text = "Temp: $temperature°C", modifier = Modifier.weight(1f))
        Text(text = "Weather Code: $weatherCode", modifier = Modifier.weight(1f))
    }
}

 */

@Composable
fun WeatherItem(time: String, temperature: Float, weatherCode: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Time
        Text(text = "Time: $time", modifier = Modifier.weight(1f))

        // Temperature
        Text(text = "Temp: $temperature°C", modifier = Modifier.weight(1f))

        // Weather Icon
        Image(
            painter = painterResource(id = getWeatherIcon(weatherCode)),
            contentDescription = "Weather Icon",
            modifier = Modifier
                .size(40.dp)
                .padding(4.dp)
        )
    }
}
