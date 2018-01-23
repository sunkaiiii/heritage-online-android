package com.example.sunkai.heritage.Fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
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
import com.example.sunkai.heritage.tools.BaseAsyncTask
import com.example.sunkai.heritage.tools.MakeToast.toast
import kotlinx.android.synthetic.main.fragment_folk.*
import java.util.*


/**
 * 民间页的类
 */
class FolkFragment : BaseLazyLoadFragment(), View.OnClickListener,AdapterView.OnItemSelectedListener {


    internal lateinit var datas: List<FolkDataLite> //首次加载获取的初始数据，用于各种搜索条件归位的时候，数据的归位

    private lateinit var folkListviewAdapter: FolkRecyclerViewAdapter
    private lateinit var folk_search_btn: ImageView
    private lateinit var refreshLayout: SwipeRefreshLayout
    internal var getDatas: MutableList<FolkDataLite> = ArrayList()//用于处理搜索的List，各种搜索的结果都会操作这个list


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_folk, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView(view)
        folk_location_spinner.onItemSelectedListener = this
        folk_heritages_spinner.onItemSelectedListener = this
        folkListviewAdapter = FolkRecyclerViewAdapter(activity!!)
        folk_show_recyclerview.layoutManager=GridLayoutManager(activity!!,2)
        folk_show_recyclerview.setHasFixedSize(true)
        folk_show_recyclerview.adapter = folkListviewAdapter
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
        return Datas.filter { it.category == locationString }
    }

    private fun filterHeritage(Datas: List<FolkDataLite>, heritageString: String): List<FolkDataLite> {
        return Datas.filter { it.divide == heritageString }
    }


    private fun initView(view: View) {
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
                    getDatas = datas.toMutableList()
                    SelectAdpterInformation()
                }
            }
        })

    }

    private fun setSpinner(){
        val locationTreeSet=TreeSet<String>()
        val heritageDivideTreeSet=TreeSet<String>()
        for (data in datas){
            locationTreeSet.add(data.category)
            heritageDivideTreeSet.add(data.divide)
        }
        //在整个adapter的第一个插入"请选择"，第0位是重置所有筛选的item
        val locationList=locationTreeSet.toMutableList()
        locationList.add(0,getString(R.string.please_choice))
        val heritageList=heritageDivideTreeSet.toMutableList()
        heritageList.add(0,getString(R.string.please_choice))
        folk_location_spinner.adapter=ArrayAdapter<String>(activity,android.R.layout.simple_spinner_dropdown_item,locationList)
        folk_heritages_spinner.adapter=ArrayAdapter<String>(activity,android.R.layout.simple_spinner_dropdown_item,heritageList)
    }

    private val onPageLoadListner:OnPageLoaded by lazy {
        object :OnPageLoaded{
            override fun onPreLoad() {
                refreshLayout.isRefreshing=true
                setWidgetEnable(false)
            }

            override fun onPostLoad() {
                datas=folkListviewAdapter.getListDatas()
                getDatas=datas.toMutableList()
                refreshLayout.isRefreshing=false
                setSpinner()
                setWidgetEnable(true)
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


    override fun onNothingSelected(parent: AdapterView<*>?) {}

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        SelectAdpterInformation()
    }


    internal class HandleSearch
    /**
     * 此类用于处理搜索的相关内容
     *
     * @param searInfo 搜索框的文本
     */
    (private var searInfo: String, folkFragment: FolkFragment) : BaseAsyncTask<Void, Void, Int,FolkFragment>(folkFragment) {
        private var searchData: List<FolkDataLite>? = null

        override fun doInBackground(vararg voids: Void): Int {
            searchData = HandleFolk.Search_Folk_Info(searInfo)
            return if (searchData == null) 0 else 1
        }

        override fun onPostExecute(integer: Int) {
            val folkFragment = weakRefrece.get()
            folkFragment?.let{
                if (integer == 1) {
                    folkFragment.getDatas = searchData!!.toMutableList()
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
