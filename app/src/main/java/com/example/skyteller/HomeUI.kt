package com.example.skyteller

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.graphics.drawable.shapes.Shape
import android.util.Log
import android.widget.ProgressBar
import androidx.annotation.RestrictTo
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageScope
import com.example.skyteller.network.NetworkResponse
import com.example.skyteller.network.WeatherResponse
import com.example.skyteller.ui.theme.Purple40
import com.example.skyteller.ui.theme.Purple80
import com.example.skyteller.ui.theme.SkyBlue
import kotlinx.serialization.encoding.ChunkedDecoder
import java.nio.file.WatchEvent
val navColor = Color(0xFF87CEFA)
val bgColor = Color(0xFFE0FFFF)
val cardColor = Color(0xFFB2FFFF)

@Composable
fun HomeUI(viewModel: SkyViewmodel) {

    var city by remember { mutableStateOf("") }
    val weatherData = viewModel.weatherData.observeAsState()

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current



    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(Unit) {
        viewModel.getData("lucknow")
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .clickable(interactionSource = interactionSource, indication = null, onClick = {
                focusManager.clearFocus()
                keyboardController?.hide()
            })
    ) {

        Row(
            modifier = Modifier
                .background(navColor)
                .padding(top = 24.dp, bottom = 10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SkyTeller",
                fontSize = 30.sp,
                modifier = Modifier.padding(10.dp),
                color = Color.Black
            )
            OutlinedTextField(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                shape = CircleShape,
                keyboardActions = KeyboardActions(onDone = {
                    if (city.isNotBlank()) {
                        viewModel.getData(city)
                        city = ""
                    }
                    keyboardController?.hide()
                    focusManager.clearFocus()
                }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedBorderColor = Color.LightGray,
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                value = city,
                onValueChange = { city = it },
                placeholder = { Text("Search Location") },
                maxLines = 1,
                singleLine = true,
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (city.isNotBlank()) {
                                viewModel.getData(city)
                                city = ""
                            }
                            keyboardController?.hide()
                            focusManager.clearFocus()
                        },
                        modifier = Modifier
                            .size(OutlinedTextFieldDefaults.MinHeight)
                            .padding(2.dp)
                            .clip(RoundedCornerShape(topEnd = 100f, bottomEnd = 100f))
                            .background(bgColor)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Color.Black
                        )
                    }
                }
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bgColor)
                .weight(1f)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {when (val response = weatherData.value) {
            is NetworkResponse.Error -> {
                Text(text = response.message)
            }

            is NetworkResponse.Loading -> {
                CircularProgressIndicator()
            }

            is NetworkResponse.Success -> {
                WeatherDetails(data = response.data)
            }

            null -> {
                Box(modifier = Modifier.fillMaxSize())
            }
        }

        }
    }
}

@Composable
fun WeatherDetails(data: WeatherResponse) {

    Text(
        text = data.location.name,
        modifier = Modifier.fillMaxWidth(),
        fontSize = 30.sp,
        textAlign = TextAlign.Center
    )
    Text(
        text = data.location.country,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
    Spacer(Modifier.height(10.dp))
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardColor)
                .padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .height(180.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = data.current.temp_c + "°", fontSize = 50.sp)

                Text(text = data.current.condition.text, maxLines = 1)
            }
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .height(140.dp),
                verticalArrangement = Arrangement.Center
            ) {
                SubcomposeAsyncImage(
                    loading = {
                        LinearProgressIndicator(
                            modifier = Modifier.padding(65.dp),
                            gapSize = 4.dp
                        )
                    },
                    modifier = Modifier.size(160.dp),
                    model = "https:${data.current.condition.icon}".replace("64x64", "128x128"),
                    contentDescription = null
                )

            }
        }
    }
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.background(cardColor)
        ) {

            item {
                Details(key = "Date", value = data.location.localtime.split(" ")[0])
            }
            item {
                Details(key = "Time", value = data.location.localtime.split(" ")[1])
            }
            item {
                Details(key = "Cloud", value = data.current.cloud)
            }
            item {
                Details(key = "Humidity", value = data.current.humidity)
            }
            item {
                Details(key = "Wind(kph)", value = data.current.wind_kph)
            }
            item {
                Details(key = "UV", value = data.current.uv)
            }
            item {
                Details(key = "Latitude", value = data.location.lat)
            }
            item {
                Details(key = "Longitude", value = data.location.lon)
            }

        }
    }
    Spacer(Modifier.height(100.dp))

}

@Composable
fun Details(key: String, value: String) {


    ElevatedCard(modifier = Modifier.padding(8.dp)) {
        Column(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
            Text(text = key, modifier = Modifier.align(Alignment.CenterHorizontally))
            Text(text = value,modifier = Modifier.align(Alignment.CenterHorizontally), fontSize = 26.sp)
        }
    }
}