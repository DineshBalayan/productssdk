package com.banksathi.advisors.internal.products.productList

import ApiRepository
import NetworkState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banksathi.advisors.internal.products.productList.models.CreditCardBenefits
import com.banksathi.advisors.internal.products.productList.models.ProductCategoriesData
import com.banksathi.advisors.internal.products.productList.models.ProductListingData
import com.banksathi.advisors.internal.products.productList.models.ProductListingRequest
import kotlinx.coroutines.*

class ProductsViewModel(private val mainRepository: ApiRepository) : ViewModel() {

    var selectedCategoryId: Int = 0

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    val productCategories = MutableLiveData<List<ProductCategoriesData>>()
    val copyProductList = MutableLiveData<List<ProductListingData>>()
    val productList = MutableLiveData<List<ProductListingData>>()
    val cardBenefitsList = MutableLiveData<List<CreditCardBenefits>>()

    val loading = MutableLiveData<Boolean>()

    private fun onError(message: String) {
        _errorMessage.value = message
        loading.value = false
    }

    fun getProductCategories() {
        loading.value = true
        viewModelScope.launch {
            when (val response = mainRepository.getProductCategories()) {
                is NetworkState.Success -> {
                    productCategories.postValue(response.data.data)
                    if (response.data.data.isNotEmpty()) {
                        selectedCategoryId = response.data.data[0].categoryId
                    }
                    loading.value = false
                }

                is NetworkState.Error -> {

                    loading.value = false
                    onError("sdk error")

                }

                else -> {}
            }
        }
    }

    fun getProductListing(searchString: String) {
        if (loading.value == false) {
            loading.value = true
            val reqBody =
                ProductListingRequest(categoryId = selectedCategoryId, searchString = searchString)
            viewModelScope.launch {
                when (val response = mainRepository.getProductListing(reqBody)) {
                    is NetworkState.Success -> {
                        loading.value = false
                        copyProductList.postValue(response.data.data?.products)
                        productList.postValue(response.data.data?.products)
                        cardBenefitsList.postValue(response.data.data?.creditCardBenefits)
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

    fun searchProducts(searchString: String) {
        loading.value = true
        val responseBody =
            ProductListingRequest(categoryId = selectedCategoryId, searchString = searchString)
        viewModelScope.launch {
            when (val response = mainRepository.getProductListing(responseBody)) {
                is NetworkState.Success -> {
                    loading.value = false
                    copyProductList.postValue(response.data.data?.products)
                    productList.postValue(response.data.data?.products)
                    cardBenefitsList.postValue(response.data.data?.creditCardBenefits)
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
