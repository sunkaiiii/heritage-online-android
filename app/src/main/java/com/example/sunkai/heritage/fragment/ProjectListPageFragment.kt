package com.example.sunkai.heritage.fragment

import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.adapter.ProjectInformationAdapter
import com.example.sunkai.heritage.databinding.FragmentProjectListPageBinding
import com.example.sunkai.heritage.entity.ProjectListViewModel
import com.example.sunkai.heritage.fragment.baseFragment.BaseViewBindingFragment
import com.example.sunkai.heritage.tools.takeBlurScreenShot
import com.example.sunkai.heritage.value.DATA
import kotlinx.coroutines.launch

class ProjectListPageFragment : BaseViewBindingFragment<FragmentProjectListPageBinding>() {
    private val viewModel by lazy { ViewModelProvider(requireActivity()).get(ProjectListViewModel::class.java) }
    override fun getBindingClass(): Class<FragmentProjectListPageBinding> = FragmentProjectListPageBinding::class.java

    override fun initView() {
        val adapter = ProjectInformationAdapter()
        adapter.setProjectItemClickListener { _, projectListInformation ->
            findNavController().navigate(
                    R.id.project_list_to_project_detail,
                    bundleOf(DATA to projectListInformation.link)
            )
        }
        binding.projectInformationList.adapter = adapter

//        binding.blurBackground.setOnClickListener {
//            showSearchView()
//        }

//        binding.projectListSearchView.setOnClickListener {
//            showDetailView()
//            }

        viewModel.projectList.observe(viewLifecycleOwner, {
            lifecycleScope.launch {
                adapter.submitData(it)
            }
        })
    }

    fun showDetailView(){
//        binding.projectListSearchView.visibility = View.GONE
//        binding.projectListSearchDetailView.visibility = View.VISIBLE
//        binding.projectListSearchDetailView.setContent {
//            ProjectListSearchProjectDetailDialog()
//        }
        requireView().takeBlurScreenShot(requireActivity(), { bitmap ->
            binding.blurBackground.visibility = View.VISIBLE
            binding.blurBackground.setImageBitmap(bitmap)
            binding.blurBackground.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in))
        })
        binding.blurBackground.visibility = View.VISIBLE
//        TransitionManager.beginDelayedTransition(binding.projectListSearchView.parent as ViewGroup)
    }

//    fun showSearchView(){
//        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out)
//        animation.setAnimationListener(object : Animation.AnimationListener {
//            override fun onAnimationStart(p0: Animation?) {}
//
//            override fun onAnimationEnd(p0: Animation?) {
//                binding.blurBackground.visibility = View.GONE
//                binding.blurBackground.setImageBitmap(null)
//            }
//
//            override fun onAnimationRepeat(p0: Animation?) {}
//
//        })
//        binding.blurBackground.startAnimation(animation)
//        binding.projectListSearchView.visibility = View.VISIBLE
//        binding.projectListSearchDetailView.visibility = View.GONE
//        val transition = AutoTransition()
//        transition.duration = 200
//        TransitionManager.beginDelayedTransition(binding.projectListSearchView.parent as ViewGroup, transition)
//    }

//    @Composable
//    fun ProjectListSearchProjectDetailDialog() {
//        val projectTypeDataState = viewModel.allProjectType.observeAsState()
//        val searchProjectViewData = projectTypeDataState.value
//        if (searchProjectViewData != null) {
//            ProjectListSearchProjectDetailView(searchProjectViewData){
//                viewModel.searchProject(keywords = it)
//                showSearchView()
//            }
//        } else {
//            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                CircularProgressIndicator()
//            }
//        }
//    }
}