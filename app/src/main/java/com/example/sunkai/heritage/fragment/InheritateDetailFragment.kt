package com.example.sunkai.heritage.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.entity.InheritateDetailViewModel
import com.example.sunkai.heritage.entity.response.InheritateDetailResponse
import com.example.sunkai.heritage.fragment.baseFragment.BaseGlideFragment
import com.example.sunkai.heritage.value.DATA
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_inheritate_detail.*

@AndroidEntryPoint
class InheritateDetailFragment : BaseGlideFragment() {
    private val viewModel by lazy { ViewModelProvider(this).get(InheritateDetailViewModel::class.java) }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_inheritate_detail, container, false)
        init(view)
        return view
    }

    private fun init(view: View) {
        view.findViewById<Toolbar>(R.id.toolbar).title = getString(R.string.inheritors)
        viewModel.inheritateDetail.observe(viewLifecycleOwner, {
            setDataToView(it)
        })
        val link = arguments?.getString(DATA) ?: return
        viewModel.setInheritateDetailLink(link)
    }

    private fun setDataToView(inheritateData: InheritateDetailResponse) {
        progressBar.visibility = View.GONE
        containerView.visibility = View.VISIBLE
        inheritateTitle.text = inheritateData.title
        inheritateDetailDesc.text = inheritateData.text
        inheritateDetailTopGridLayout.setData(inheritateData.desc)
        inheritateData.inheritate?.let {
            if (it.isNotEmpty()) {
                inheritateOthersView.visibility = View.VISIBLE
                inheritateOthersView.setData(it)
            }
        }

    }

}
