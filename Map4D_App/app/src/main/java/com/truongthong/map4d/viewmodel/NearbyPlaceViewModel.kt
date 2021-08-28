package com.truongthong.map4d.viewmodel

import android.accounts.NetworkErrorException
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.truongthong.map4d.api.RetrofitInstance
import com.truongthong.map4d.model.nearby.NeabyPoint
import com.truongthong.map4d.model.route.RouteLocation
import com.truongthong.map4d.model.search.MapResponse
import com.truongthong.map4d.util.Constants.Companion.API_KEY
import kotlinx.coroutines.launch
import retrofit2.Response


class NearbyPlaceViewModel : ViewModel() {

    val getNearby: MutableLiveData<Response<NeabyPoint>> = MutableLiveData()

    fun getNearby(location: String, radius: Int, text: String) {
        viewModelScope.launch {
            val response = RetrofitInstance.api.getNearby(API_KEY, location, radius, text)
            try {
                getNearby.value = response
            } catch (e: NetworkErrorException) {

            }
        }
    }

}