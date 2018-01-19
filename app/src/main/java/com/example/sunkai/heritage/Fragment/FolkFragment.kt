package com.example.sunkai.heritage.Fragment

import android.content.Context
import android.content.IntentFilter
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.example.sunkai.heritage.Adapter.FolkRecyclerViewAdapter
import com.example.sunkai.heritage.ConnectWebService.HandleFolk
import com.example.sunkai.heritage.Data.FolkDataLite
import com.example.sunkai.heritage.Interface.OnPageLoaded
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.MakeToast.toast
import java.lang.ref.WeakReference
import java.util.*


/**
 * 民间页的类
 */
class FolkFragment : BaseLazyLoadFragment(), View.OnClickListener {
    internal lateinit var datas: List<FolkDataLite>
    private lateinit var folkListviewAdapter: FolkRecyclerViewAdapter
    private lateinit var folk_edit: EditText
    private lateinit var folk_heritages_spinner: Spinner
    private lateinit var folk_location_spinner: Spinner
    private lateinit var folk_show_recyclerview: RecyclerView
    private lateinit var folk_search_btn: ImageView
    private lateinit var ll_fragment_folk_top_options:LinearLayout
    private lateinit var refreshLayout: SwipeRefreshLayout
    internal var getDatas: List<FolkDataLite> = ArrayList()//用于处理搜索的List
    private var changeData = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_folk, container, false)
        initView(view)
        /**
         * 当预约发生改变的时候，通知个人中心我的预约重新加载我的预约
         */
        folk_location_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                SelectAdpterInformation()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
        folk_heritages_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                SelectAdpterInformation()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
        folkListviewAdapter = FolkRecyclerViewAdapter(activity!!)
        folk_show_recyclerview.layoutManager=GridLayoutManager(activity!!,2)
        folk_show_recyclerview.setHasFixedSize(true)
        folk_show_recyclerview.adapter = folkListviewAdapter
        return view
    }

    override fun startLoadInformation() {
        folkListviewAdapter.setOnPageLoadListener(onPageLoadListner)
        folkListviewAdapter.startGetInformation()
    }



    private fun SelectAdpterInformation() {
        /**
         * datas作为原始数据保证期不做任何改变
         * getDatas在搜索框为空的时候即使datas，不为空的时候即为搜索出来的结果
         * selectData负责进行第一轮筛选得出的结果
         * finalData对第二个spinner的内容与selectData中的内容进行比对，筛选出结果
         * 将finalData传递个adpter
         */

        var selectDatas: List<FolkDataLite>
        selectDatas = if (folk_location_spinner.selectedItemPosition == 0) {
            getDatas
        } else {
            val selectLocation = folk_location_spinner.selectedItem as String
            filterLoacation(getDatas, selectLocation)
        }
        if (folk_heritages_spinner.selectedItemPosition != 0) {
            val selectHeritage = folk_heritages_spinner.selectedItem as String
            selectDatas = filterHeritage(selectDatas, selectHeritage)
        }
        folkListviewAdapter.setNewDatas(selectDatas)
    }


    private fun filterLoacation(Datas: List<FolkDataLite>, locationString: String): List<FolkDataLite> {
        return Datas.filter { it.divide == locationString }
    }

    private fun filterHeritage(Datas: List<FolkDataLite>, heritageString: String): List<FolkDataLite> {
        return Datas.filter { it.divide == heritageString }
    }


    private fun initView(view: View) {
        ll_fragment_folk_top_options=view.findViewById(R.id.ll_fragment_folk_top_options)
        folk_edit = view.findViewById(R.id.folk_edit)
        folk_heritages_spinner = view.findViewById(R.id.folk_heritages_spinner)
        folk_location_spinner = view.findViewById(R.id.folk_location_spinner)
        folk_show_recyclerview = view.findViewById(R.id.folk_show_recyclerview)
        refreshLayout=view.findViewById(R.id.fragment_folk_swipe_refresh)
        refreshLayout.setOnRefreshListener { startLoadInformation() }
        folk_search_btn = view.findViewById(R.id.folk_searchbtn)
        folk_search_btn.setOnClickListener(this)
        folk_edit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                /*
                 * 当搜索框被清空的时候，自动的将getData的内容清空并还原为datas的数据
                 * 刷新list使得list继续显示全部的内容
                 */
                if (TextUtils.isEmpty(folk_edit.text)) {
                    getDatas = datas
                    SelectAdpterInformation()
                }
            }
        })

    }

    private val onPageLoadListner:OnPageLoaded by lazy {
        object :OnPageLoaded{
            override fun onPreLoad() {
                refreshLayout.isRefreshing=true
            }

            override fun onPostLoad() {
                datas=folkListviewAdapter.getListDatas()
                refreshLayout.isRefreshing=false
            }

        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.folk_searchbtn -> submit()
        }
    }

    private fun submit() {
        // validate
        val edit = folk_edit.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(edit)) {
            toast("输入的内容不能为空")
            return
        }
        hideKeyboard()
        setWidgetEnable(false)
        HandleSearch(edit, this).execute()
    }

    fun setData(changeData: Boolean, datas: List<FolkDataLite>) {
        this.changeData = changeData
        this.datas = datas
        getDatas = datas
    }

    internal class HandleSearch
    /**
     * 此类用于处理搜索的相关内容
     *
     * @param searInfo 搜索框的文本
     */
    (var searInfo: String, folkFragment: FolkFragment) : AsyncTask<Void, Void, Int>() {
        var searchData: List<FolkDataLite>? = null
        var folkFragmentWeakReference: WeakReference<FolkFragment>

        init {
            folkFragmentWeakReference = WeakReference(folkFragment)
        }

        override fun doInBackground(vararg voids: Void): Int? {
            searchData = HandleFolk.Search_Folk_Info(searInfo)
            return if (searchData == null) 0 else 1
        }

        override fun onPostExecute(integer: Int?) {
            val folkFragment = folkFragmentWeakReference.get()
            if (folkFragment != null) {
                if (integer == 1) {
                    folkFragment.getDatas = searchData!!
                    folkFragment.SelectAdpterInformation()
                }
                folkFragment.setWidgetEnable(true)
            }
        }
    }

    fun setWidgetEnable(enable: Boolean) {
        folk_heritages_spinner.isEnabled = enable
        folk_location_spinner.isEnabled = enable
        folk_search_btn.isEnabled = enable
        folk_edit.isEnabled = enable
    }


    //隐藏键盘
    private fun hideKeyboard() {
        val view = activity?.currentFocus
        if (view != null) {
            (activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }
}
