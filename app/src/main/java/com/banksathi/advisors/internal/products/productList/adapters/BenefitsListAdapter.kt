package com.banksathi.advisors.internal.products.productList.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.banksathi.advisors.databinding.AdapterBenefitsFlexBinding
import com.google.android.flexbox.FlexboxLayoutManager

class BenefitsListAdapter : RecyclerView.Adapter<BenefitsViewHolder>() {

    private var cardBenefits = mutableListOf<String>()

    @SuppressLint("NotifyDataSetChanged")
    fun setFeaturesList(products: List<String>) {
        this.cardBenefits = products.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BenefitsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterBenefitsFlexBinding.inflate(inflater, parent, false)
        return BenefitsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BenefitsViewHolder, position: Int) {
        val pos = position % cardBenefits.size
        holder.bindTo(cardBenefits[pos])
    }

    override fun getItemCount(): Int {
        return cardBenefits.size
    }
}

class BenefitsViewHolder(val binding: AdapterBenefitsFlexBinding) : RecyclerView.ViewHolder(binding.root) {

    internal fun bindTo(text:String) {
        binding.benefitText.text = text
        val lp = binding.benefitText.layoutParams
        if (lp is FlexboxLayoutManager.LayoutParams) {
            lp.flexGrow = 1f
        }
    }
}