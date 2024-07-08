import android.graphics.drawable.Drawable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class HomePagerAdapter(activity: FragmentActivity?) : FragmentStateAdapter(activity!!) {
    private val mFragmentList: MutableList<Fragment> = ArrayList()
    private val mFragmentTitleList: MutableList<String> = ArrayList()
    private val mFragmentIconsList: MutableList<Drawable> = ArrayList()

    fun getTabTitle(position : Int): String{
        return mFragmentTitleList[position]
    }

    fun getTabIcon(position : Int): Drawable{
        return mFragmentIconsList[position]
    }

    fun addFragment(fragment: Fragment, title: String, icon: Drawable) {
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
        mFragmentIconsList.add(icon)
    }

    override fun getItemCount(): Int {
        return mFragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return mFragmentList[position]
    }
}