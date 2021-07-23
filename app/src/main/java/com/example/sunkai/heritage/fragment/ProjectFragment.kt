package com.example.sunkai.heritage.fragment

import android.graphics.BitmapFactory
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.adapter.ProjectInformationAdapter
import com.example.sunkai.heritage.dialog.FragmentProjectContentDialog
import com.example.sunkai.heritage.entity.ProjectPageViewModel
import com.example.sunkai.heritage.entity.response.ProjectBasicInformation
import com.example.sunkai.heritage.fragment.baseFragment.BaseGlideFragment
import com.example.sunkai.heritage.tools.*
import com.example.sunkai.heritage.value.DATA
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_project.*
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProjectFragment : BaseGlideFragment() {

    private lateinit var fragmentProjectSearchCard: View
    private lateinit var fragmentProjectAppBarLayout: AppBarLayout
    private lateinit var projectInformationList: RecyclerView
    private lateinit var projectDescLoading: View
    private lateinit var projectPageLayout: View
    private lateinit var projectPageTitle: TextView
    private lateinit var projectFragmentShowContent: View
    private lateinit var fragmentProjectToolbar: Toolbar

    private val projectViewModel by lazy { ViewModelProvider(this).get(ProjectPageViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_project, container, false)
        fragmentProjectSearchCard = view.findViewById(R.id.fragmentProjectSearchCard)
        fragmentProjectAppBarLayout = view.findViewById(R.id.fragmentProjectAppBarLayout)
        projectInformationList = view.findViewById(R.id.projectInformationList)
        projectDescLoading = view.findViewById(R.id.projectDescLoading)
        projectPageLayout = view.findViewById(R.id.projectPageLayout)
        projectPageTitle = view.findViewById(R.id.projectPageTitle)
        projectFragmentShowContent = view.findViewById(R.id.projectFragmentShowContent)
        fragmentProjectToolbar = view.findViewById(R.id.fragmentProjectToolbar)
        initview()
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
        projectInformationList.adapter = adapter
        projectInformationList.addOnScrollListener(onScrollDirectionHelper)
        fragmentProjectAppBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, _ ->
            fragmentProjectSearchCard.visibility =
                if (projectInformationList.y == 0f) View.VISIBLE else View.GONE
        })
        projectViewModel.projectList.observe(viewLifecycleOwner, {
            lifecycleScope.launch {
                adapter.submitData(it)
            }
        })

        return view
    }


    override fun changeSpecificViewTheme() {
        val background = fragmentProjectTopBackgroundRelativeLayout.background
        if (background is GradientDrawable) {
            background.setColor(getTransparentColor(getThemeColor()))
        }

        projectOverViewCardview.setCardBackgroundColor(getDarkThemeColor())
    }

    private fun initview() {
        fragmentProjectSearchCard.setOnClickListener {
            navigateToSearchPage()
        }
        fragmentProjectToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.search_menu -> {
                    navigateToSearchPage()
                }
            }

            true
        }
    }


    private fun navigateToSearchPage() {
        findNavController().navigate(R.id.project_list_to_search_fragment)
    }


    private val onScrollDirectionHelper = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val visibility = if (recyclerView.y == 0f) View.VISIBLE else View.GONE
            if (fragmentProjectSearchCard.visibility != visibility) {
                when (visibility) {
                    View.VISIBLE -> {
                        fragmentProjectSearchCard.startAnimation(
                            AnimationUtils.loadAnimation(
                                context,
                                R.anim.popup_enter
                            )
                        )
                    }
                    View.GONE -> {
                        fragmentProjectSearchCard.startAnimation(
                            AnimationUtils.loadAnimation(
                                context,
                                R.anim.popup_exit
                            )
                        )
                    }
                }
            }
            fragmentProjectSearchCard.visibility =
                if (recyclerView.y == 0f) View.VISIBLE else View.GONE
        }
    }


    private fun fillProjectBasicDataIntoView(projectInformation: ProjectBasicInformation) {
        projectDescLoading.visibility = View.GONE
        projectPageLayout.visibility = View.VISIBLE
        projectPageTitle.text = projectInformation.title
        projectFragmentShowContent.setOnClickListener {
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
                .inflate(R.layout.fragment_project_item_layout, ProjectDescLayout, false)
            itemLayout.findViewById<TextView>(R.id.FragmentProjectItemTitle).text = it.desc
            itemLayout.findViewById<TextView>(R.id.FragmentProjectItemContent).text = it.num
            val layoutParams = itemLayout.layoutParams
            if (layoutParams is LinearLayout.LayoutParams) {
                layoutParams.weight = 1.0f
                itemLayout.layoutParams = layoutParams
            }
            ProjectDescLayout.addView(itemLayout)
        }

        val bitmap = BitmapFactory.decodeResource(
            resources,
            R.drawable.fragment_project_top_background_image
        )
        fragmentProjectTopImage.setImageBitmap(bitmap.toBlurBitmap(context ?: return))
    }
}