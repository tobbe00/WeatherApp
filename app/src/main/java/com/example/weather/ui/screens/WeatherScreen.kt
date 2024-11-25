package com.example.weather.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weather.ui.utils.getWeatherIcon
import com.example.weather.ui.viewmodels.WeatherViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeatherScreen(weatherViewModel: WeatherViewModel) {
    var placeName by remember { mutableStateOf(TextFieldValue("")) }

    // Data streams for hourly and weekly weather data
    val todayHourlyData = weatherViewModel.todayHourlyData.collectAsState().value
    val weeklyMinMaxData = weatherViewModel.weeklyMinMaxData.collectAsState().value

    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    // Check internet availability
    val internetAvailable = weatherViewModel.isInternetAvailable(context)

    // Show dialog if there's no internet connection
    LaunchedEffect(internetAvailable) {
        if (!internetAvailable) {
            showDialog = true
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { /* Prevent dismissal */ },
            confirmButton = {
                Button(onClick = {
                    weatherViewModel.loadOldWeatherData()
                    showDialog = false
                }) {
                    Text("Load Old Data")
                }
            },
            title = { Text("No Internet Connection") },
            text = { Text("You are offline. Using cached weather data.") }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Input field for place name
        OutlinedTextField(
            value = placeName,
            onValueChange = { placeName = it },
            label = { Text("Enter Place Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                // Fetch coordinates based on place name
                weatherViewModel.fetchCoordinatesForPlace(placeName.text, context)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("Fetch Weather")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display weather data
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            // Section 1: Today's Hourly Weather
            item {
                Text(
                    text = "Today's Hourly Weather",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.Black
                    )
                )
            }

            items(todayHourlyData) { (time, temperature, weatherCode) ->
                WeatherItem(time, temperature, weatherCode)
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Section 2: Weekly Min/Max Temperatures
            item {
                Text(
                    text = "Weekly Forecast (Min/Max)",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.Black
                    )
                )
            }

            items(weeklyMinMaxData) { (date, minTemp, maxTemp, weatherCode) ->
                WeeklyMinMaxItem(date, minTemp, maxTemp, weatherCode)
            }
        }
    }
}



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

@Composable
fun WeeklyMinMaxItem(date: String, minTemp: Float, maxTemp: Float, weatherCode: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Date: $date", modifier = Modifier.weight(1.5f))
        Text(text = "Min: $minTemp°C", modifier = Modifier.weight(1f))
        Text(text = "Max: $maxTemp°C", modifier = Modifier.weight(1f))

        // Weather Icon for the maximum temperature
        Image(
            painter = painterResource(id = getWeatherIcon(weatherCode)),
            contentDescription = "Weather Icon",
            modifier = Modifier
                .size(40.dp)
                .padding(4.dp)
        )
    }
}

