package com.example.sunkai.heritage.Fragment

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*

import com.example.sunkai.heritage.Activity.JoinActivity
import com.example.sunkai.heritage.Adapter.FolkListviewAdapter
import com.example.sunkai.heritage.ConnectWebService.HandleFolk
import com.example.sunkai.heritage.Data.FolkData
import com.example.sunkai.heritage.R
import kotlinx.android.synthetic.main.fragment_folk.*

import java.io.ByteArrayOutputStream
import java.lang.ref.WeakReference
import java.util.ArrayList


/**
 * 民间页的类
 */
class FolkFragment : Fragment(), View.OnClickListener {
    internal lateinit var datas: List<FolkData>
    internal lateinit var folkListviewAdapter: FolkListviewAdapter
    private lateinit var folk_edit: EditText
    private lateinit var folk_heritages_spinner: Spinner
    private lateinit var folk_location_spinner: Spinner
    private lateinit var folk_show_listview: ListView
    private lateinit var folk_search_btn: ImageView
    private lateinit var ll_fragment_folk_top_options:LinearLayout
    lateinit var loadProgress: ProgressBar
    internal var getDatas: List<FolkData> = ArrayList()//用于处理搜索的List
    internal var changeData = false



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
        val intentFilter = IntentFilter()
        intentFilter.addAction("android.intent.action.adpterGetDataBroadCast")
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
        folkListviewAdapter = FolkListviewAdapter(activity!!, this@FolkFragment)
        folk_show_listview.adapter = folkListviewAdapter
        folk_show_listview.setOnItemClickListener { _, view1, position, _ ->
            val bundle = Bundle()
            val folkData = folkListviewAdapter.getItem(position) as FolkData
            val imageView = view1.findViewById<ImageView>(R.id.list_img)
            imageView.isDrawingCacheEnabled = true
            val drawable = imageView.drawable
            val bitmapDrawable = drawable as BitmapDrawable
            val bitmap = bitmapDrawable.bitmap
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            folkData.image = byteArrayOutputStream.toByteArray()
            bundle.putSerializable("activity", folkData)
            val intent = Intent(activity, JoinActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }
        return view
    }


    private fun SelectAdpterInformation() {
        /**
         * datas作为原始数据保证期不做任何改变
         * getDatas在搜索框为空的时候即使datas，不为空的时候即为搜索出来的结果
         * selectData负责进行第一轮筛选得出的结果
         * finalData对第二个spinner的内容与selectData中的内容进行比对，筛选出结果
         * 将finalData传递个adpter
         */

        var selectDatas: List<FolkData>
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
        if (isLoadData) {
            folkListviewAdapter.setNewDatas(selectDatas)
        }
    }


    private fun filterLoacation(Datas: List<FolkData>, locationString: String): List<FolkData> {
        return Datas.filter { it.location == locationString }
    }

    private fun filterHeritage(Datas: List<FolkData>, heritageString: String): List<FolkData> {
        return Datas.filter { it.divide == heritageString }
    }


    private fun initView(view: View) {
        ll_fragment_folk_top_options=view.findViewById(R.id.ll_fragment_folk_top_options)
        folk_edit = view.findViewById(R.id.folk_edit)
        folk_heritages_spinner = view.findViewById(R.id.folk_heritages_spinner)
        folk_location_spinner = view.findViewById(R.id.folk_location_spinner)
        folk_show_listview = view.findViewById(R.id.folk_show_listview)
        folk_search_btn = view.findViewById(R.id.folk_searchbtn)
        loadProgress = view.findViewById(R.id.folk_load_progress)
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

    override fun onClick(v: View) {
        when (v.id) {
            R.id.folk_searchbtn -> submit()
        }
    }

    private fun submit() {
        // validate
        val edit = folk_edit.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(edit)) {
            Toast.makeText(context, "输入的内容不能为空", Toast.LENGTH_SHORT).show()
            return
        }
        hideKeyboard()
        setWidgetEnable(false)
        HandleSearch(edit, this).execute()
    }

    fun setData(changeData: Boolean, datas: List<FolkData>) {
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
        var searchData: List<FolkData>? = null
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

    companion object {
        var isLoadData = false
    }
}
