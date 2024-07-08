package com.banksathi.advisors.internal.products.productDetail.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.banksathi.advisors.databinding.AdapterProductMarketingListBinding
import com.banksathi.advisors.internal.helper.Util
import com.banksathi.advisors.internal.products.ImagePreviewSheet

class ProductMarketingListAdapter : RecyclerView.Adapter<ProductMarketingViewHolder>() {

    var marketingList = mutableListOf<String?>()
    @SuppressLint("NotifyDataSetChanged")
    fun setMarketingData(data: List<String>) {
        this.marketingList = data.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductMarketingViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterProductMarketingListBinding.inflate(inflater, parent, false)
        return ProductMarketingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductMarketingViewHolder, position: Int) {
        val context: Context = holder.itemView.context
        Util().loadNetworkImage(context, marketingList[position] ?: "", holder.binding.imgMarketing)
        holder.binding.imgMarketing.setOnClickListener{
            val bottomSheetDialogFragment = ImagePreviewSheet(null,marketingList[position])
            bottomSheetDialogFragment.show(
                (context as AppCompatActivity).supportFragmentManager,
                "ShareBottomSheet"
            )
        }
    }

    override fun getItemCount(): Int {
        return marketingList.size
    }
}

class ProductMarketingViewHolder(val binding: AdapterProductMarketingListBinding) : RecyclerView.ViewHolder(binding.root) {
}