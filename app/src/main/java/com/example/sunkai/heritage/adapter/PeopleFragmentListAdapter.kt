package com.example.sunkai.heritage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.databinding.FragmentPeopleListItemBinding
import com.example.sunkai.heritage.databinding.FragmentPeopleListItemNoImageBinding
import com.example.sunkai.heritage.entity.response.NewsListResponse
import com.example.sunkai.heritage.tools.loadImageFromServer
import com.example.sunkai.heritage.views.tools.RectangleImageView

class PeopleFragmentListAdapter(
    private val glide: RequestManager,
    private var listener: PeopleClickListner? = null
) : PagingDataAdapter<NewsListResponse, PeopleFragmentListAdapter.Holder>(
    COMPARATOR
) {
    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<NewsListResponse>() {
            override fun areItemsTheSame(
                oldItem: NewsListResponse,
                newItem: NewsListResponse
            ): Boolean {
                return oldItem.link == newItem.link
            }

            override fun areContentsTheSame(
                oldItem: NewsListResponse,
                newItem: NewsListResponse
            ): Boolean {
                return oldItem == newItem
            }
        }

        private const val PEOPLE_ITEM_WITH_IMAGE = 0
        private const val PEOPLE_ITEM_WITHOUT_IMAGE = 1
    }

    class Holder(
        val rootView: View,
        val peopleTitle: TextView,
        val peopleSubtitle: TextView,
        val peopleImage: ImageView?,
        val peopleCollection: ImageView
    ) : RecyclerView.ViewHolder(rootView) {
        constructor(binding: FragmentPeopleListItemBinding) : this(
            binding.root,
            binding.peopleMainTitle,
            binding.peopleSubTitle,
            binding.peopleImage,
            binding.peopleCollect
        )

        constructor(binding: FragmentPeopleListItemNoImageBinding) : this(
            binding.root,
            binding.peopleMainTitle,
            binding.peopleSubTitle,
            null,
            binding.peopleCollect
        )
    }

    override fun getItemViewType(position: Int): Int {
        val data = getItem(position) ?: return PEOPLE_ITEM_WITHOUT_IMAGE
        return if (data.img == null) PEOPLE_ITEM_WITHOUT_IMAGE else PEOPLE_ITEM_WITH_IMAGE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        if (viewType == PEOPLE_ITEM_WITH_IMAGE) {
            return Holder(
                FragmentPeopleListItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            return Holder(
                FragmentPeopleListItemNoImageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val data = getItem(position) ?: return
        holder.itemView.setOnClickListener {
            this.listener?.onItemClick(it, data)
        }
        val titleContent = data.title.split("：")

        holder.peopleTitle.text = titleContent[0]
        if(titleContent.size > 1){
            holder.peopleSubtitle.text = titleContent[1]
        }
        holder.peopleImage?.let {
            val imageUrl = data.compressImg?:data.img ?:return@let
            glide.loadImageFromServer(imageUrl).into(it)
        }

    }

    fun setOnItemClickListener(listener: PeopleClickListner) {
        this.listener = listener
    }


    interface PeopleClickListner {
        fun onItemClick(itemView: View, item: NewsListResponse)
    }
}