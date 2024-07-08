package com.banksathi.advisors.internal.helper

import com.banksathi.advisors.internal.products.productDetail.models.ProductDetailResponse
import RequestInterceptor
import com.banksathi.advisors.internal.leads.info_queries.models.LeadDisputeOptionsResponse
import com.banksathi.advisors.internal.leads.leadDetail.models.LeadDetailRequest
import com.banksathi.advisors.internal.leads.leadDetail.models.LeadDetailResponse
import com.banksathi.advisors.internal.leads.leadlist.models.*
import com.banksathi.advisors.internal.products.productDetail.models.ProductDetailRequest
import com.banksathi.advisors.internal.products.productList.models.ProductCategoriesResponse
import com.banksathi.advisors.internal.products.productList.models.ProductListingRequest
import com.banksathi.advisors.internal.products.productList.models.ProductListingResponse
import okhttp3.MultipartBody
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*

interface RetrofitService {

    @GET("product_categories")
    suspend fun getProductCategories(): Response<ProductCategoriesResponse>

    @POST("product_listing")
    suspend fun getProductListing(
        @Body request: ProductListingRequest
    ): Response<ProductListingResponse>

    @POST("product_details")
    suspend fun getProductDetail(
        @Body request: ProductDetailRequest
    ): Response<ProductDetailResponse>

    @POST("lead_listing")
    suspend fun getLeadsListing(@Body request: LeadListingRequest): Response<LeadListingResponse>

    @POST("lead_details")
    suspend fun getLeadDetail(@Body request: LeadDetailRequest): Response<LeadDetailResponse>

    @POST("get_all_disputes")
    suspend fun getLeadDisputeQueries(
        @Body request: LeadDisputeQueriesRequest
    ): Response<LeadDisputeQueriesResponse>

    @GET("dispute_options")
    suspend fun getLeadDisputeOptions(): Response<LeadDisputeOptionsResponse>

    @Multipart
    @POST("save_dispute_query")
    suspend fun submitLeadDisputeQuery(
        @Part("comment") comment: String?,
        @Part("queryType") queryType: String?,
        @Part("leadId") leadId: Int?,
        @Part files: List<MultipartBody.Part>?
    ): Response<LeadDisputeQueriesResponse>

    companion object {

        private val okHttpClient: OkHttpClient =
            OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(RequestInterceptor())
                .build()

        private val retrofit: Retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BanksathiBase.getInstance().PROD_BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        private val retrofitService: RetrofitService by lazy {
            retrofit.create(RetrofitService::class.java)
        }

        fun getInstance(): RetrofitService = retrofitService
    }
}
