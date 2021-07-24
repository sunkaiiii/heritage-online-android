package com.example.sunkai.heritage.fragment.baseFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class BaseViewBindingFragment<T>:BaseGlideFragment() {
    private var _binding:T? = null
    val binding get() = _binding!!

    abstract fun getBindingClass(inflater: LayoutInflater, container: ViewGroup?):T

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = getBindingClass(inflater,container)
        initView()
        return getBindingView()
    }

    abstract fun initView()

    abstract fun getBindingView(): View

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}