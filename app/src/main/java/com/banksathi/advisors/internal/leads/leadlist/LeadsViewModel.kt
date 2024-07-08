package com.banksathi.advisors.internal.leads.leadlist

import ApiRepository
import NetworkState
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banksathi.advisors.internal.helper.BanksathiBase
import com.banksathi.advisors.internal.leads.leadlist.models.*
import kotlinx.coroutines.*

class LeadsViewModel(private val mainRepository: ApiRepository) : ViewModel() {

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    val selectedStatus = MutableLiveData<Int>()

    val leadsResponse = MutableLiveData<LeadListingData>()

    private var job: Job? = null

    var currentPage = 1
    val loading = MutableLiveData<Boolean>()
    val loadMore = MutableLiveData<Boolean>()

    private fun onError(message: String) {
        _errorMessage.value = message
        loading.value = false
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

    fun getLeadsListing() {
        currentPage = 1
        var pId: String? = null
        if (selectedStatus.value != 0) pId = selectedStatus.value.toString()

        val requestBody =
            LeadListingRequest(BanksathiBase.getInstance().advisorMobile, currentPage, pId)

        viewModelScope.launch {
            loading.value = true
            when (val response = mainRepository.getLeadsListing(requestBody)) {
                is NetworkState.Success -> {
                    leadsResponse.postValue(response.data.data)
                    loading.value = false
                }

                is NetworkState.Error -> {
                    loading.value = false
                    onError(response.response.message())
                }

                else -> {}
            }
        }
    }

    fun getLeadsLoadMore() {
        var pId: String? = null
        if (selectedStatus.value != 0) pId = selectedStatus.value.toString()

        val requestBody =
            LeadListingRequest(BanksathiBase.getInstance().advisorMobile, currentPage, pId)

        viewModelScope.launch {
            loadMore.value = true
            when (val response = mainRepository.getLeadsListing(requestBody)) {
                is NetworkState.Success -> {
                    leadsResponse.postValue(response.data.data)
                    loadMore.value = false
                }

                is NetworkState.Error -> {
                    loadMore.value = false
                    onError(response.response.message())
                }

                else -> {}
            }
        }
    }
}
