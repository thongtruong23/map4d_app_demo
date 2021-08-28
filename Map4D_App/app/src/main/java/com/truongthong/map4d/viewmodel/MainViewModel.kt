package com.truongthong.map4d.viewmodel

import android.accounts.NetworkErrorException
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.truongthong.map4d.api.RetrofitInstance
import com.truongthong.map4d.model.search.MapResponse
import com.truongthong.map4d.util.Constants.Companion.API_KEY
import kotlinx.coroutines.launch
import retrofit2.Response


class MainViewModel : ViewModel() {

    val searchLocation: MutableLiveData<Response<MapResponse>> = MutableLiveData()

    fun getSearchLocation(searchQuery: String) {
        viewModelScope.launch {
            val response = RetrofitInstance.api.searchLocation(searchQuery, API_KEY)
            try {
                searchLocation.value = response
            } catch (e: NetworkErrorException) {

            }
        }
    }

}