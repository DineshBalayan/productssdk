package com.banksathi.advisors.internal.leads.info_queries

import ApiRepository
import NetworkState
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banksathi.advisors.internal.leads.info_queries.models.LeadDisputeOptionsResponse
import com.banksathi.advisors.internal.leads.info_queries.models.QueryType
import java.io.File
import kotlinx.coroutines.*

class AddQueryViewModel(private val mainRepository: ApiRepository) : ViewModel() {

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    var disputeOptionsList = mutableListOf<QueryType>()
    val leadDisputeOptions = MutableLiveData<LeadDisputeOptionsResponse>()

    var job: Job? = null

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    val loading = MutableLiveData<Boolean>()
    val isSubmitted = MutableLiveData<Boolean>()

    private fun onError(message: String) {
        _errorMessage.value = message
        loading.value = false
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

    fun getLeadDisputeOptions() {
        Log.d("Thread Outside", Thread.currentThread().name)
        loading.value = true
        viewModelScope.launch {
            Log.d("Thread Inside", Thread.currentThread().name)
            when (val response = mainRepository.getLeadDisputeOptions()) {
                is NetworkState.Success -> {
                    leadDisputeOptions.postValue(response.data)
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

    fun submitLeadDisputeQuery(
        comment: String,
        queryType: String,
        leadId: Int,
        files: List<File>?
    ) {
        Log.d("Thread Outside", Thread.currentThread().name)
        loading.value = true
        viewModelScope.launch {
            Log.d("Thread Inside", Thread.currentThread().name)
            when (val response =
                mainRepository.submitQueryData(
                    comment,
                    queryType,
                    leadId,
                )
            ) {
                is NetworkState.Success -> {
                    isSubmitted.value = true
                    loading.value = false
                }
                is NetworkState.Error -> {
                    loading.value = false
                    isSubmitted.value = false
                    onError(response.response.message())
                }
                else -> {}
            }
        }
    }
}