package com.example.sunkai.heritage.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import com.example.sunkai.heritage.Activity.BaseActivity.BaseGlideActivity
import com.example.sunkai.heritage.Adapter.FolkRecyclerViewAdapter
import com.example.sunkai.heritage.ConnectWebService.HandleFolk
import com.example.sunkai.heritage.Data.FolkDataLite
import com.example.sunkai.heritage.Interface.OnItemClickListener
import com.example.sunkai.heritage.Interface.OnPageLoaded
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.BaseAsyncTask
import com.example.sunkai.heritage.tools.MakeToast.toast
import com.example.sunkai.heritage.value.ALL_FOLK_INFO_ACTIVITY
import com.example.sunkai.heritage.value.DATA
import com.example.sunkai.heritage.value.FROM
import kotlinx.android.synthetic.main.activity_all_folk_info.*
import java.util.*

/**
 * 用于展示所有民间信息的Activity
 */

class AllFolkInfoActivity : BaseGlideActivity(), View.OnClickListener, AdapterView.OnItemSelectedListener {
    internal lateinit var datas: List<FolkDataLite> //首次加载获取的初始数据，用于各种搜索条件归位的时候，数据的归位

    private lateinit var folkListviewAdapter: FolkRecyclerViewAdapter
    private lateinit var folk_search_btn: ImageView
    private lateinit var refreshLayout: androidx.swiperefreshlayout.widget.SwipeRefreshLayout
    internal var getDatas: MutableList<FolkDataLite> = ArrayList()//用于处理搜索的List，各种搜索的结果都会操作这个list

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_folk_info)
        initView()
        folk_location_spinner.onItemSelectedListener = this
        folk_heritages_spinner.onItemSelectedListener = this
        folkListviewAdapter = FolkRecyclerViewAdapter(this, arrayListOf(),glide)
        folk_show_recyclerview.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, 2)
        folk_show_recyclerview.setHasFixedSize(true)
        startLoadInformation()
    }


    private fun startLoadInformation() {
        folk_show_recyclerview.adapter=null
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


    private fun initView() {
        refreshLayout = findViewById(R.id.fragment_folk_swipe_refresh)
        refreshLayout.setOnRefreshListener {
            startLoadInformation()
        }
        folk_search_btn = findViewById(R.id.folk_searchbtn)
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

    private fun setSpinner() {
        val locationTreeSet = TreeSet<String>()
        val heritageDivideTreeSet = TreeSet<String>()
        for (data in datas) {
            locationTreeSet.add(data.category)
            heritageDivideTreeSet.add(data.divide)
        }
        //在整个adapter的第一个插入"请选择"，第0位是重置所有筛选的item
        val locationList = locationTreeSet.toMutableList()
        locationList.add(0, getString(R.string.please_choice))
        val heritageList = heritageDivideTreeSet.toMutableList()
        heritageList.add(0, getString(R.string.please_choice))
        folk_location_spinner.adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, locationList)
        folk_heritages_spinner.adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, heritageList)
    }

    private val onPageLoadListner = object : OnPageLoaded {
        override fun onPreLoad() {
            refreshLayout.isRefreshing = true
            setWidgetEnable(false)
        }

        override fun onPostLoad() {
            datas = folkListviewAdapter.getListDatas()
            getDatas = datas.toMutableList()
            refreshLayout.isRefreshing = false
            setSpinner()
            setWidgetEnable(true)
            folk_show_recyclerview.adapter=folkListviewAdapter
            folkListviewAdapter.setOnItemClickListener { view, position ->
                val data=folkListviewAdapter.getItem(position)
                val intent= Intent(this@AllFolkInfoActivity,FolkInformationActivity::class.java)
                intent.putExtra(DATA,data)
                intent.putExtra(FROM, ALL_FOLK_INFO_ACTIVITY)
                startActivity(intent)
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
        AllFolkInfoActivity.HandleSearch(edit, this).execute()
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
    (private var searInfo: String, folkActivity: AllFolkInfoActivity) : BaseAsyncTask<Void, Void, Int, AllFolkInfoActivity>(folkActivity) {
        private var searchData: List<FolkDataLite>? = null

        override fun doInBackground(vararg voids: Void): Int {
            searchData = HandleFolk.Search_Folk_Info(searInfo)
            return if (searchData == null) 0 else 1
        }

        override fun onPostExecute(integer: Int) {
            val folkActivity = weakRefrece.get()
            folkActivity?.let {
                if (integer == 1) {
                    folkActivity.getDatas = searchData!!.toMutableList()
                    folkActivity.SelectAdpterInformation()
                }
                folkActivity.setWidgetEnable(true)
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
        val view = currentFocus
        if (view != null) {
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }
}
