package com.example.sunkai.heritage.dialog

import android.content.Context
import android.view.View
import android.widget.TextView
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.dialog.base.BaseBottomDialog
import com.example.sunkai.heritage.entity.response.ProjectBasicInformation

class FragmentProjectContentDialog(context: Context, private val data: ProjectBasicInformation) : BaseBottomDialog(context) {
    init {
        setContentView(R.layout.fragment_project_content_dialog_layout)
        initView()
    }

    private fun initView() {
        val textView: TextView = view?.findViewById(R.id.fragmentProjectContent) ?: return
        textView.text = data.content
        findViewById<View>(R.id.close_button).setOnClickListener { dismiss() }
    }
}