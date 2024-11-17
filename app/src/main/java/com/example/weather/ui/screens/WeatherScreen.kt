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
    var latitude by remember { mutableStateOf(TextFieldValue("")) }
    var longitude by remember { mutableStateOf(TextFieldValue("")) }

    // Collect the two separate data streams
    val todayHourlyData = weatherViewModel.todayHourlyData.collectAsState().value
    val weeklyMinMaxData = weatherViewModel.weeklyMinMaxData.collectAsState().value

    val context = LocalContext.current // Get the Context
    val internetAvailable = weatherViewModel.isInternetAvailable(context)
    val showDialog = remember { mutableStateOf(false) }
    var checkedInternet by remember { mutableStateOf(false) } // Ensures one-time check

    LaunchedEffect(internetAvailable) {
        if (!checkedInternet && !internetAvailable) {
            showDialog.value = true
            checkedInternet = true
        }
    }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { /* Prevent dismissal */ },
            confirmButton = {
                Button(onClick = {
                    // Load old data and dismiss dialog
                    //weatherViewModel.loadOldWeatherData()  // Implement the function
                    showDialog.value = false
                }) {
                    Text("Load Old Data")
                }
            },
            title = { Text("No Internet Connection") },
            text = { Text("You are offline. Load old weather data?") }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Input fields for latitude and longitude
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

        // Combined LazyColumn to scroll through both hourly and weekly forecast
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            // Section 1: Today's Hourly Weather
            item {
                Text(
                    "Today's Hourly Weather",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold, // Make the text bold
                        fontSize = 20.sp,             // Adjust the size if necessary
                        color = Color.Black          // Optional: Change the color to make it stand out more
                    )
                )
            }

            items(todayHourlyData) { (time, temperature, weatherCode) ->
                WeatherItem(time, temperature, weatherCode)
            }

            //Spacer(modifier = Modifier.height(16.dp))

            // Section 2: Weekly Min/Max Temperatures
            item {
                Text(
                    "Weekly Forecast (Min/Max)",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold, // Make the text bold
                        fontSize = 20.sp,             // Adjust the size if necessary
                        color = Color.Black          // Optional: Change the color to make it stand out more
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

