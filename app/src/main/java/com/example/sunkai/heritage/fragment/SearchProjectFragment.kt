package com.example.sunkai.heritage.fragment

import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.adapter.ProjectInformationAdapter
import com.example.sunkai.heritage.adapter.ProjectSearchHistoryAdapter
import com.example.sunkai.heritage.database.entities.SearchHistory
import com.example.sunkai.heritage.dialog.SearchProjectDialog
import com.example.sunkai.heritage.entity.SearchProjectViewModel
import com.example.sunkai.heritage.entity.request.SearchRequest
import com.example.sunkai.heritage.entity.response.SearchCategoryResponse
import com.example.sunkai.heritage.fragment.baseFragment.BaseGlideFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_search_project.*
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchProjectFragment : BaseGlideFragment() {

    private val viewModel by lazy { ViewModelProvider(this).get(SearchProjectViewModel::class.java) }
    private val searchHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (msg.what == SEARCH) {
                saveSearchHistory()
            }
            if (msg.what == HISTORY) {
                this.removeMessages(SEARCH)
            }
            requestSearch(searchEditext.text?.toString())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search_project,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        searchButton.setOnClickListener { findNavController().popBackStack() }
        searchEditext.addTextChangedListener {
            searchHandler.removeMessages(SEARCH)
            if (it?.isEmpty() == true) {
                searchClearText.visibility = View.GONE
                //setHistoryDataToRecyclerView(historyData)
                return@addTextChangedListener
            }
            searchClearText.visibility = View.VISIBLE
            searchHandler.sendEmptyMessageDelayed(SEARCH, SEARCH_DELAY)
        }
        searchEditext.setOnEditorActionListener { v, actionId, _ ->
            searchHandler.removeMessages(SEARCH)
            if (actionId == EditorInfo.IME_ACTION_GO) {
                if (v.text.isEmpty())
                    return@setOnEditorActionListener false
                requestSearch(v.text.toString())
            }
            false
        }
        searchClearText.setOnClickListener {
            searchEditext.setText("")
        }

        viewModel.searchHistory.observe(viewLifecycleOwner, { historyData ->
            setHistoryDataToRecyclerView(historyData)
        })

        viewModel.searchResult.observe(viewLifecycleOwner, { result ->
            loadingBackground.visibility = View.GONE
            val adapter = ProjectInformationAdapter()
            adapter.addLoadStateListener {
                if (it.refresh is LoadState.Error) {
                    Toast.makeText(requireContext(), "Search Error", Toast.LENGTH_SHORT).show()
                }
            }
            lifecycleScope.launch {
                adapter.submitData(result)
            }
        })

        activitySearchAdvanceButton.setOnClickListener {
            loadingBackground.visibility = View.VISIBLE
            viewModel.searchCategory.observe(viewLifecycleOwner, { searchCategory ->
                loadingBackground.visibility = View.GONE
                showSearchDialog(searchCategory)
            })
        }
    }

    private fun setHistoryDataToRecyclerView(historyData: List<SearchHistory>) {
        val adapter = ProjectSearchHistoryAdapter(requireContext(), historyData, glide)
        adapter.setOnProjectSearchItemClickListener { _, searchHistory ->
            viewModel.removeSearchResult(searchHistory)
        }
        adapter.setOnItemClickListener { _, position ->
            searchEditext.setText(adapter.getItem(position).title ?: "")
            searchHandler.sendEmptyMessage(HISTORY)
        }
        searchRecyclerview.adapter = adapter
    }


    private fun requestSearch(queryString: String?) {
        if (queryString.isNullOrBlank()) {
            return
        }
        requestSearch(SearchRequest(queryString))
    }

    private fun requestSearch(searchReqeust: SearchRequest) {
        loadingBackground.visibility = View.VISIBLE
        viewModel.searchProject(searchReqeust)
    }

    private fun showSearchDialog(seachCategory: SearchCategoryResponse) {
        val dialog = SearchProjectDialog(seachCategory)
        dialog.setOnSearchButtonClickListener { searchReqeust -> requestSearch(searchReqeust) }
        dialog.show(requireActivity().supportFragmentManager, null)
    }


    private fun saveSearchHistory() {
        val searchHistory = SearchHistory()
        searchHistory.title = searchEditext.text.toString()
        viewModel.addSearchResult(searchHistory)
    }

    companion object {
        const val SEARCH = 1
        const val HISTORY = 2
        const val SEARCH_DELAY = 700L
    }

}
