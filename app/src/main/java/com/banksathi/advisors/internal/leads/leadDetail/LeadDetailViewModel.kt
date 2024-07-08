package com.banksathi.advisors.internal.leads.leadDetail

import ApiRepository
import NetworkState
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banksathi.advisors.internal.leads.leadDetail.models.LeadDetailData
import com.banksathi.advisors.internal.leads.leadDetail.models.LeadDetailRequest
import kotlinx.coroutines.*

class LeadDetailViewModel(private val mainRepository: ApiRepository) : ViewModel() {

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    val leadDetail = MutableLiveData<LeadDetailData>()

    private var job: Job? = null

    var leadId : Int = 0

    val loading = MutableLiveData<Boolean>()

    private fun onError(message: String) {
        _errorMessage.value = message
        loading.value = false
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

    fun getLeadDetail(leadId: Int) {
        Log.d("Thread Outside", Thread.currentThread().name)

        val requestBody = LeadDetailRequest(
            leadId
        )
        loading.value = true
        viewModelScope.launch {
            Log.d("Thread Inside", Thread.currentThread().name)
            when (val response = mainRepository.getLeadDetail(requestBody)) {
                is NetworkState.Success -> {
                    leadDetail.postValue(response.data.data)
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
}