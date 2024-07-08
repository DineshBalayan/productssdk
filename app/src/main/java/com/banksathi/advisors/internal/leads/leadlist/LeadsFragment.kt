package com.banksathi.advisors.internal.leads.leadlist

import ApiRepository
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.banksathi.advisors.BankSathiLauncher
import com.banksathi.advisors.R
import com.banksathi.advisors.databinding.FragmentLeadsBinding
import com.banksathi.advisors.internal.helper.LeadStatusConstants.Companion.actionLeadStatus
import com.banksathi.advisors.internal.helper.LeadStatusConstants.Companion.expiredLeadStatus
import com.banksathi.advisors.internal.helper.LeadStatusConstants.Companion.pendingLeadStatus
import com.banksathi.advisors.internal.helper.LeadStatusConstants.Companion.successLeadStatus
import com.banksathi.advisors.internal.helper.RetrofitService
import com.banksathi.advisors.internal.leads.leadlist.models.LeadListingData

class LeadsFragment : Fragment() {

    private var _binding: FragmentLeadsBinding? = null

    private val binding
        get() = _binding!!

    private lateinit var viewModel: LeadsViewModel

    private val adapter = LeadsListAdapter(onQuerySubmitListener = {
        viewModel.getLeadsListing()
    })

    private var isLoadMore = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLeadsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val retrofitService = RetrofitService.getInstance()
        val mainRepository = ApiRepository(retrofitService)
        binding.leadsListView.adapter = adapter

        initScrollListener(binding)

        viewModel =
            ViewModelProvider(this, LeadsViewModelFactory(mainRepository))[
                LeadsViewModel::class.java]

        viewModel.selectedStatus.value = 0

        balanceViewStateObserver()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (viewModel.leadsResponse.value == null) {
            viewModel.getLeadsListing()
        }
        binding.actionBack.setOnClickListener {
            (activity as BankSathiLauncher).selectHomeTab()
        }
        binding.swipeContainer.setOnRefreshListener {
            binding.swipeContainer.isRefreshing = false
            viewModel.selectedStatus.value = 0
            viewModel.getLeadsListing()
        }
    }

    private fun balanceViewStateObserver() {

        viewModel.leadsResponse.observe(viewLifecycleOwner) {
            if (viewModel.currentPage == 1) {
                adapter.setLeadListing(it)
                if (it.leads?.data?.isEmpty() == true) {
                    binding.emptyDataView.itemView.visibility = View.VISIBLE
                } else {
                    binding.emptyDataView.itemView.visibility = View.GONE
                }
            } else {
                adapter.addMoreLeadListing(it)
            }

            bindHeaderData(it)
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) {
//            Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
            binding.emptyDataView.itemView.visibility = View.VISIBLE
        }

        viewModel.loading.observe(viewLifecycleOwner) {
            if (it) {
                binding.progressDialog.visibility = View.VISIBLE
            } else {
                binding.progressDialog.visibility = View.GONE
            }
        }

        viewModel.loadMore.observe(viewLifecycleOwner) {
            if (it) {
                binding.loadMoreProgress.visibility = View.VISIBLE
            } else {
                binding.loadMoreProgress.visibility = View.GONE
                isLoadMore = false
            }
        }

        viewModel.selectedStatus.observe(viewLifecycleOwner) {
            binding.totalLeadsView.background =
                activity?.let { it1 ->
                    AppCompatResources.getDrawable(it1, R.drawable.lead_card_border)
                }
            binding.successLeadsView.background =
                activity?.let { it1 ->
                    AppCompatResources.getDrawable(it1, R.drawable.lead_card_border)
                }
            binding.pendingLeadsView.background =
                activity?.let { it1 ->
                    AppCompatResources.getDrawable(it1, R.drawable.lead_card_border)
                }
            binding.actionLeadsView.background =
                activity?.let { it1 ->
                    AppCompatResources.getDrawable(it1, R.drawable.lead_card_border)
                }
            binding.rejectedLeadsView.background =
                activity?.let { it1 ->
                    AppCompatResources.getDrawable(it1, R.drawable.lead_card_border)
                }
            when (it) {
                0 ->
                    binding.totalLeadsView.background =
                        activity?.let { it1 ->
                            AppCompatResources.getDrawable(
                                it1,
                                R.drawable.lead_card_border_selected
                            )
                        }

                pendingLeadStatus ->
                    binding.pendingLeadsView.background =
                        activity?.let { it1 ->
                            AppCompatResources.getDrawable(
                                it1,
                                R.drawable.lead_card_border_selected
                            )
                        }

                successLeadStatus ->
                    binding.successLeadsView.background =
                        activity?.let { it1 ->
                            AppCompatResources.getDrawable(
                                it1,
                                R.drawable.lead_card_border_selected
                            )
                        }

                expiredLeadStatus ->
                    binding.rejectedLeadsView.background =
                        activity?.let { it1 ->
                            AppCompatResources.getDrawable(
                                it1,
                                R.drawable.lead_card_border_selected
                            )
                        }

                actionLeadStatus ->
                    binding.actionLeadsView.background =
                        activity?.let { it1 ->
                            AppCompatResources.getDrawable(
                                it1,
                                R.drawable.lead_card_border_selected
                            )
                        }

                else -> { // Note the block
                    print("x is neither 1 nor 2")
                }
            }
        }
    }

    private fun bindHeaderData(it: LeadListingData?) {
        binding.balanceView.visibility = View.VISIBLE
        binding.totalLeads.text = it?.totalLeads.toString()
        binding.successLeads.text = it?.completedLeads.toString()
        binding.pendingLeads.text = it?.inprocessLeads.toString()
        binding.rejectedLeads.text = it?.rejectedLeads.toString()
        binding.actionRequired.text = it?.actionrequiredLeads.toString()

        _binding?.totalLeadsView?.setOnClickListener {
            viewModel.selectedStatus.value = 0
            viewModel.getLeadsListing()
        }
        _binding?.successLeadsView?.setOnClickListener {
            viewModel.selectedStatus.value = successLeadStatus
            viewModel.getLeadsListing()
        }
        _binding?.pendingLeadsView?.setOnClickListener {
            viewModel.selectedStatus.value = pendingLeadStatus
            viewModel.getLeadsListing()
        }
        _binding?.actionLeadsView?.setOnClickListener {
            viewModel.selectedStatus.value = actionLeadStatus
            viewModel.getLeadsListing()
        }
        _binding?.rejectedLeadsView?.setOnClickListener {
            viewModel.selectedStatus.value = expiredLeadStatus
            viewModel.getLeadsListing()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initScrollListener(binding: FragmentLeadsBinding) {

        binding.nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, oldScrollY ->
            if (scrollY < oldScrollY) {
                isLoadMore = false
            }
            if (scrollY == 0) {
                isLoadMore = false
            }

            if (scrollY >= (v.getChildAt(0).measuredHeight - v.measuredHeight) + 100) {
                if (!isLoadMore) {
                    isLoadMore = true
                    val lastPage: Int? = viewModel.leadsResponse.value?.leads?.last_page
                    if (lastPage != null && lastPage > viewModel.currentPage) {
                        viewModel.currentPage++
                        viewModel.getLeadsLoadMore()
                    }
                }
                return@OnScrollChangeListener
            }
        })
    }
}
