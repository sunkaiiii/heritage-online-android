package com.example.sunkai.heritage.fragment

import android.graphics.BitmapFactory
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.adapter.ProjectInformationAdapter
import com.example.sunkai.heritage.databinding.FragmentProjectBinding
import com.example.sunkai.heritage.dialog.FragmentProjectContentDialog
import com.example.sunkai.heritage.entity.ProjectPageViewModel
import com.example.sunkai.heritage.entity.response.ProjectBasicInformation
import com.example.sunkai.heritage.fragment.baseFragment.BaseViewBindingFragment
import com.example.sunkai.heritage.tools.*
import com.example.sunkai.heritage.value.DATA
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProjectFragment : BaseViewBindingFragment<FragmentProjectBinding>() {

    private val projectViewModel by lazy { ViewModelProvider(requireActivity()).get(ProjectPageViewModel::class.java) }

    override fun getBindingClass(): Class<FragmentProjectBinding> =
        FragmentProjectBinding::class.java

    override fun changeSpecificViewTheme() {
        val background = binding.fragmentProjectTopBackgroundRelativeLayout.background
        if (background is GradientDrawable) {
            background.setColor(getTransparentColor(getThemeColor()))
        }

        binding.projectOverViewCardview.setCardBackgroundColor(getDarkThemeColor())
    }

    override fun initView() {
        binding.fragmentProjectSearchCard.setOnClickListener {
            navigateToSearchPage()
        }
        binding.fragmentProjectToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.search_menu -> {
                    navigateToSearchPage()
                }
            }

            true
        }
        projectViewModel.projectBasicInformation.observe(viewLifecycleOwner, {
            fillProjectBasicDataIntoView(it)
        })
        val adapter = ProjectInformationAdapter()
        adapter.setProjectItemClickListener { _, projectListInformation ->
            findNavController().navigate(
                R.id.project_list_to_project_detail,
                bundleOf(DATA to projectListInformation.link)
            )
        }
        binding.projectInformationList.adapter = adapter
        binding.projectInformationList.addOnScrollListener(onScrollDirectionHelper)
        binding.fragmentProjectAppBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, _ ->
            binding.fragmentProjectSearchCard.visibility =
                if (binding.projectInformationList.y == 0f) View.VISIBLE else View.GONE
        })
        projectViewModel.projectList.observe(viewLifecycleOwner, {
            lifecycleScope.launch {
                adapter.submitData(it)
            }
        })
    }


    private fun navigateToSearchPage() {
        findNavController().navigate(R.id.project_list_to_search_fragment)
    }


    private val onScrollDirectionHelper = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val visibility = if (recyclerView.y == 0f) View.VISIBLE else View.GONE
            if (binding.fragmentProjectSearchCard.visibility != visibility) {
                when (visibility) {
                    View.VISIBLE -> {
                        binding.fragmentProjectSearchCard.startAnimation(
                            AnimationUtils.loadAnimation(
                                context,
                                R.anim.popup_enter
                            )
                        )
                    }
                    View.GONE -> {
                        binding.fragmentProjectSearchCard.startAnimation(
                            AnimationUtils.loadAnimation(
                                context,
                                R.anim.popup_exit
                            )
                        )
                    }
                }
            }
            binding.fragmentProjectSearchCard.visibility =
                if (recyclerView.y == 0f) View.VISIBLE else View.GONE
        }
    }


    private fun fillProjectBasicDataIntoView(projectInformation: ProjectBasicInformation) {
        binding.projectDescLoading.visibility = View.GONE
        binding.projectPageLayout.visibility = View.VISIBLE
        binding.projectPageTitle.text = projectInformation.title
        binding.projectFragmentShowContent.setOnClickListener {
            val dialog = FragmentProjectContentDialog(
                context
                    ?: return@setOnClickListener, projectInformation
            )
            dialog.show()
            dialog.window?.setLayout(
                Utils.getScreenWidth(),
                (Utils.getScreenHeight() * 0.7).toInt()
            )
        }
        projectInformation.numItem.forEach {
            val itemLayout = LayoutInflater.from(context)
                .inflate(R.layout.fragment_project_item_layout, binding.projectDescLayout, false)
            itemLayout.findViewById<TextView>(R.id.FragmentProjectItemTitle).text = it.desc
            itemLayout.findViewById<TextView>(R.id.FragmentProjectItemContent).text = it.num
            val layoutParams = itemLayout.layoutParams
            if (layoutParams is LinearLayout.LayoutParams) {
                layoutParams.weight = 1.0f
                itemLayout.layoutParams = layoutParams
            }
            binding.projectDescLayout.addView(itemLayout)
        }

        val bitmap = BitmapFactory.decodeResource(
            resources,
            R.drawable.fragment_project_top_background_image
        )
        binding.fragmentProjectTopImage.setImageBitmap(bitmap.toBlurBitmap(context ?: return))
    }
}