package com.banksathi.advisors

import HomePagerAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.viewpager2.widget.ViewPager2
import com.banksathi.advisors.databinding.LauncherBanksathiBinding
import com.banksathi.advisors.internal.helper.AdvisorInfo
import com.banksathi.advisors.internal.helper.BanksathiBase
import com.banksathi.advisors.internal.leads.leadlist.LeadsFragment
import com.banksathi.advisors.internal.products.productList.ProductsFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class BankSathiLauncher : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    private lateinit var binding: LauncherBanksathiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        <!--TODO: UnComment dynamic values lines when building .aar-->
        val name: String = intent.getStringExtra(AdvisorInfo.advisorName) ?: ""
//        val name = "test"
        val email: String = intent.getStringExtra(AdvisorInfo.advisorEmail) ?: ""
//        val email = "test@bs.com"
        val mobile: String = intent.getStringExtra(AdvisorInfo.advisorMobile) ?: ""
//        val mobile = "9414468070"
        val code: String = intent.getStringExtra(AdvisorInfo.advisorCode) ?: ""
//        val code = "30201920"

        if (mobile.isNotEmpty() && code.isNotEmpty()) {
            BanksathiBase.getInstance().writeUserData(name, email, mobile, code)

            binding = LauncherBanksathiBinding.inflate(layoutInflater)
            setContentView(binding.root)

            tabLayout = binding.homeTabLayout
            viewPager = binding.homePager
            initiateViewPager()

        } else {
            setContentView(R.layout.error_launcher)
        }
    }

    fun selectHomeTab() {
        viewPager.currentItem = 0
    }

    fun updateContentView() {
        setContentView(R.layout.error_launcher)
    }

    private fun initiateViewPager() {
        val adapter = HomePagerAdapter(this)
        AppCompatResources.getDrawable(this, R.drawable.ic_home_navbar)
            ?.let { adapter.addFragment(ProductsFragment(), "Home", it) }
        AppCompatResources.getDrawable(this, R.drawable.ic_leads_navbar)
            ?.let { adapter.addFragment(LeadsFragment(), "Leads", it) }

        viewPager.adapter = adapter
        viewPager.currentItem = 0

        TabLayoutMediator(binding.homeTabLayout, binding.homePager) { _, _ ->
        }.attach()

        tabViewHandle()
    }

    @SuppressLint("InflateParams")
    private fun tabViewHandle() {
        val navIcons = intArrayOf(
            R.drawable.ic_home_navbar,
            R.drawable.ic_leads_navbar,
        )

        val navLabels = intArrayOf(
            R.string.home,
            R.string.leads,
        )

        val navIconsActive = intArrayOf(
            R.drawable.ic_home_navbar_selected,
            R.drawable.ic_leads_navbar_selected,
        )

        for (i in 0 until binding.homeTabLayout.tabCount) {
            val tab = LayoutInflater.from(this).inflate(R.layout.home_tab_view, null) as LinearLayout
            val tabLabel = tab.findViewById<View>(R.id.nav_label) as TextView
            val tabIcon = tab.findViewById<View>(R.id.nav_icon) as ImageView

            tabLabel.text = resources.getString(navLabels[i])
            if (i == 0) {
                tabLabel.setTextColor(AppCompatResources.getColorStateList(
                    this, R.color.white
                ))
                tabIcon.setImageResource(navIconsActive[i])
            } else {
                tabIcon.setImageResource(navIcons[i])
            }
            binding.homeTabLayout.getTabAt(i)?.setCustomView(tab)
        }

        binding.homeTabLayout.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    val tabView = tab.customView
                    val tabLabel = tabView!!.findViewById<View>(R.id.nav_label) as TextView
                    val tabIcon = tabView.findViewById<View>(R.id.nav_icon) as ImageView

                    tabLabel.setTextColor(AppCompatResources.getColorStateList(
                        this@BankSathiLauncher, R.color.white
                    ))
                    tabIcon.setImageResource(navIconsActive[tab.position])
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {
                    val tabView = tab.customView
                    val tabLabel = tabView!!.findViewById<View>(R.id.nav_label) as TextView
                    val tabIcon = tabView.findViewById<View>(R.id.nav_icon) as ImageView

                    tabLabel.setTextColor(AppCompatResources.getColorStateList(
                        this@BankSathiLauncher, android.R.color.darker_gray
                    ))
                    tabIcon.setImageResource(navIcons[tab.position])
                }

                override fun onTabReselected(tab: TabLayout.Tab) {
                }
            }
        )
    }
}