package com.example.sunkai.heritage.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.forEach
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.dialog.base.BaseDialogFragment
import com.example.sunkai.heritage.entity.request.SearchRequest
import com.example.sunkai.heritage.entity.response.SearchCategoryResponse
import com.google.android.material.button.MaterialButton
import java.lang.Exception

class SearchProjectDialog(searchCategoryResponse: SearchCategoryResponse) : BaseDialogFragment() {
    private val searchKeyList = searchCategoryResponse.searchCategories.values.toMutableSet()
    private val spinenrKeyToClassFiledMap:Map<String,String>
    private val spinnerData: MutableList<Spinner>
    private var onSearchButtonListener: OnSearchButtonClickListener? = null

    init {
        spinnerData = mutableListOf()
        val map= mutableMapOf<String,String>()
        searchCategoryResponse.searchCategories.forEach {
            map[it.value]=it.key
        }
        spinenrKeyToClassFiledMap=map
    }

    override fun getLayoutID(): Int {
        return R.layout.fragment_search_advanced_search_dialog_view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val addSearchConditionButton = view.findViewById<View>(R.id.addSearchCondition)
        val searhItemLayout = view.findViewById<LinearLayout>(R.id.advanced_search_item_layout)
        val searchButton = view.findViewById<MaterialButton>(R.id.searchButton)
        addSearchConditionButton.setOnClickListener {
            val itemView = LayoutInflater.from(context).inflate(R.layout.fragment_search_advanced_search_dialog_item, searhItemLayout, false)
            val removeButton = itemView.findViewById<View>(R.id.removeButton)
            val spinner: Spinner = itemView.findViewById(R.id.searchSpinner)
            spinner.adapter = ArrayAdapter(context
                ?: return@setOnClickListener, android.R.layout.simple_spinner_dropdown_item, searchKeyList.toList())
            var currentSelect = spinner.selectedItem.toString()
            searchKeyList.remove(currentSelect)
            if (searchKeyList.isEmpty()) {
                it.visibility = View.GONE
            }
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {
                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, i: Int, l: Long) {
                    searchKeyList.add(currentSelect)
                    currentSelect = spinner.adapter.getItem(i).toString()
                    searchKeyList.remove(currentSelect)
                    loadSpinnerAdapter()
                }
            }
            spinnerData.add(spinner)
            removeButton.setOnClickListener {
                spinnerData.remove(spinner)
                searchKeyList.add(spinner.selectedItem.toString())
                addSearchConditionButton.visibility = View.VISIBLE
                searhItemLayout.removeView(itemView)
                loadSpinnerAdapter()
            }
            searhItemLayout.addView(itemView)
            loadSpinnerAdapter()
        }
        searchButton.setOnClickListener {
            val searchRequest = SearchRequest()
            var hasData = false
            try {
                searhItemLayout.forEach {
                    val spinner = it.findViewById<Spinner>(R.id.searchSpinner)
                    val edittext = it.findViewById<EditText>(R.id.searchText)
                    if (edittext.text.toString().isNotEmpty()) {
                        hasData = true
                        val filed = searchRequest::class.java.getDeclaredField(spinenrKeyToClassFiledMap[spinner.selectedItem.toString()] ?: error("not null"))
                        filed.isAccessible = true
                        filed.set(searchRequest, edittext.text.toString())
                    }
                }
                if (hasData) {
                    onSearchButtonListener?.onButtonClick(searchRequest)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                dismiss()
            }
        }
        addSearchConditionButton.performClick()
    }

    interface OnSearchButtonClickListener {
        fun onButtonClick(searchReqeust: SearchRequest)
    }

    fun setOnSearchButtonClickListener(listener: OnSearchButtonClickListener) {
        this.onSearchButtonListener = listener
    }

    fun setOnSearchButtonClickListener(action:((SearchRequest)->Unit)){
        setOnSearchButtonClickListener(object:OnSearchButtonClickListener{
            override fun onButtonClick(searchReqeust: SearchRequest) {
                action(searchReqeust)
            }

        })
    }

    private fun loadSpinnerAdapter() {
        spinnerData.forEach {
            val spinner = it
            val list = searchKeyList.toMutableList()
            list.add(0, spinner.selectedItem.toString())
            val adapter = ArrayAdapter(context
                ?: return@forEach, android.R.layout.simple_spinner_dropdown_item, list)
            spinner.adapter = adapter
        }
    }
}