package com.example.skyteller

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skyteller.network.Constants
import com.example.skyteller.network.NetworkResponse
import com.example.skyteller.network.RetrofitInstance
import com.example.skyteller.network.WeatherApi
import com.example.skyteller.network.WeatherResponse
import kotlinx.coroutines.launch

class SkyViewmodel : ViewModel() {

    private val apiService = RetrofitInstance.getInstance().create(WeatherApi::class.java)
    private val _weatherData = MutableLiveData<NetworkResponse<WeatherResponse>>()
    val weatherData = _weatherData

    fun getData(city: String){
        _weatherData.postValue(NetworkResponse.Loading)
        viewModelScope.launch {
            try {
                val response = apiService.getWeather(apiKey = Constants.API_KEY, city)
                if (response.isSuccessful){
                    response.body()?.let {
                        _weatherData.value = NetworkResponse.Success(it)
                    }
                }else{
                    _weatherData.value = NetworkResponse.Error("Unable to fetch data")
                }
            }catch(e: Exception){
                _weatherData.value= NetworkResponse.Error( "Unknown error")
            }
        }
    }

}