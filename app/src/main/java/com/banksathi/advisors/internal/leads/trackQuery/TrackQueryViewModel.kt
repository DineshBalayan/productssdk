package com.banksathi.advisors.internal.leads.trackQuery

import ApiRepository
import NetworkState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banksathi.advisors.internal.leads.leadlist.models.LeadDisputeQueriesRequest
import com.banksathi.advisors.internal.leads.leadlist.models.LeadQueryData
import kotlinx.coroutines.launch

class TrackQueryViewModel(private val mainRepository: ApiRepository) : ViewModel() {
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage


    val queriesList = MutableLiveData<LeadQueryData>()
    val loading = MutableLiveData(true)
    val buttonLoading = MutableLiveData(false)

    private fun onError(message: String) {
        _errorMessage.value = message
        loading.value = false
    }

    fun getQueriesListData(leadId: Int) {
        viewModelScope.launch {
            val requestBody = LeadDisputeQueriesRequest(leadId)
            when (val response = mainRepository.getChatListData(requestBody)) {
                is NetworkState.Success -> {
                    val responseData = response.data.data
                    responseData?.populateShouldShowDate() // Populate shouldShowDate property
                    queriesList.postValue(responseData)
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

    fun submitQueryData(
        comment: String,
        leadId: Int,
        queryType: String?,
    ) {
        buttonLoading.value = true
        viewModelScope.launch {
            when (val response = mainRepository.submitQueryData(
                comment,
                queryType,
                leadId,
            )) {
                is NetworkState.Success -> {
                    getQueriesListData(leadId)
                    buttonLoading.value = false
                }

                is NetworkState.Error -> {
                    buttonLoading.value = false
                    onError(response.response.message())
                }

                else -> {
                    buttonLoading.value = false
                }

            }
        }
    }
}

fun LeadQueryData.populateShouldShowDate() {
    var previousDate: String? = null
    queries?.forEach { query ->
        val currentDate = query.createdAt?.substring(4, 10) // Extract the date part
        query.shouldShowDate = currentDate != previousDate
        previousDate = currentDate
    }
}