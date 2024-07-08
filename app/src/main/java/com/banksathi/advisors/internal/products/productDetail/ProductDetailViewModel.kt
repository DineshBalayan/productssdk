package com.banksathi.advisors.internal.products.productDetail

import ApiRepository
import NetworkState
import com.banksathi.advisors.internal.products.productDetail.models.ProductDetailData
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banksathi.advisors.internal.products.productDetail.models.ProductDetailRequest
import kotlinx.coroutines.*

class ProductDetailViewModel(private val mainRepository: ApiRepository) : ViewModel() {

    var productId: Int = 0

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    val productDetail = MutableLiveData<ProductDetailData>()

    private var job: Job? = null

    val loading = MutableLiveData<Boolean>()

    private fun onError(message: String) {
        _errorMessage.value = message
        loading.value = false
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

    fun getProductDetail(productId: Int) {
        Log.d("Thread Outside", Thread.currentThread().name)
        val requestBody = ProductDetailRequest(
            productId = productId
        )
        viewModelScope.launch {
            Log.d("Thread Inside", Thread.currentThread().name)
            when (val response = mainRepository.getProductDetail(requestBody)) {
                is NetworkState.Success -> {
                    productDetail.postValue(response.data.data)
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