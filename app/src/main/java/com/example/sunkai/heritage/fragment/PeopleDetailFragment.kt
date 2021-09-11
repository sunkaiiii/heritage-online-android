package com.example.sunkai.heritage.fragment

import android.content.Intent
import android.graphics.Color
import android.graphics.RenderEffect
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.sunkai.heritage.adapter.NewsDetailRecyclerViewAdapter
import com.example.sunkai.heritage.databinding.FragmentPeopleDetailBinding
import com.example.sunkai.heritage.entity.PeopleDetailViewModel
import com.example.sunkai.heritage.entity.response.NewsDetail
import com.example.sunkai.heritage.entity.response.NewsDetailRelativeNews
import com.example.sunkai.heritage.fragment.baseFragment.BaseViewBindingFragment
import com.example.sunkai.heritage.tools.Utils.dip2px
import com.example.sunkai.heritage.tools.loadImageFromServer
import com.example.sunkai.heritage.tools.toBlurBitmap
import com.example.sunkai.heritage.value.DATA
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PeopleDetailFragment : BaseViewBindingFragment<FragmentPeopleDetailBinding>() {
    private val viewModel by lazy { ViewModelProvider(this).get(PeopleDetailViewModel::class.java) }

    override fun getBindingClass(): Class<FragmentPeopleDetailBinding> =
        FragmentPeopleDetailBinding::class.java

    override fun initView() {
        viewModel.peopleDetail.observe(viewLifecycleOwner, {
            setDataToView(it)
        })
        val link = arguments?.getString(DATA)
        link?.let {
            binding.root.post {
                viewModel.setLink(it)
            }
        }
        binding.toolbarLayout.toolbar.setBackgroundColor(Color.TRANSPARENT)
    }

    private fun setDataToView(data: NewsDetail) {

        binding.peopleTitle.text =
            data.title.replace("\r", "").replace("\n", "").replace("\t", "")
        loadImage(data)
        initScrollBehaviour()


//        binding.newsDetailSubtitleLayout.removeAllViews()
//        data.subtitle?.let { list ->
//            list.forEach {
//                val textView = TextView(requireContext())
//                textView.text = it
//                val layoutParams = LinearLayout.LayoutParams(
//                    ViewGroup.LayoutParams.WRAP_CONTENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT
//                )
//                layoutParams.weight = 1f
//                binding.newsDetailSubtitleLayout.addView(textView)
//            }
//        }
        binding.peopleDetailAuthor.text = data.author
        val adapter =
            NewsDetailRecyclerViewAdapter(requireContext(), data.content, glide, data.relativeNews)
        adapter.setOnRelevantNewsClickListner(object :
            NewsDetailRecyclerViewAdapter.onRelevantNewsClick {
            override fun onClick(v: View, news: NewsDetailRelativeNews) {
                val intent = Intent()
                intent.putExtra(DATA, news.link)
                startActivity(intent)
            }

        })
        binding.peopleDetailList.adapter = adapter
    }

    private fun initScrollBehaviour() {
        val initialAlphaOfCollaborativeView = binding.peopleRoundedBackgroundView.alpha
        val minInformationContainerTranslationY = binding.toolbarLayout.toolbar.height + 8.dip2px()
        val initalInformationContainerTranslationY =
            binding.peopleDetailInformationContainer.translationY
        binding.peopleDetailCollaborativeLayout.setBounceBoundry(0, view?.height?.div(2) ?: 0)
        binding.peopleDetailCollaborativeLayout.setOnMoveAction { distance, offsetPercentage ->
            val currentAlpha =
                initialAlphaOfCollaborativeView - (initialAlphaOfCollaborativeView * offsetPercentage)
            binding.peopleRoundedBackgroundView.alpha = currentAlpha
            binding.peopleMainImageBlur.alpha = 1 * offsetPercentage
            binding.peopleDetailInformationContainer.translationY =
                initalInformationContainerTranslationY - ((initalInformationContainerTranslationY - minInformationContainerTranslationY) * offsetPercentage)
        }
    }

    private fun loadImage(data: NewsDetail) {
        data.compressImg?.let {
            glide.loadImageFromServer(it).into(binding.peopleMainImage)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                glide.loadImageFromServer(it).into(binding.peopleMainImageBlur)
                binding.peopleMainImageBlur.setRenderEffect(
                    RenderEffect.createBlurEffect(
                        50f, 50f,
                        Shader.TileMode.CLAMP
                    )
                )
            } else {
                glide.loadImageFromServer(it).addListener(object : RequestListener<Drawable> {
                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        val bitmapDrawable = resource as BitmapDrawable? ?: return false
                        binding.peopleMainImageBlur.post {
                            binding.peopleMainImageBlur.setImageBitmap(
                                bitmapDrawable.bitmap.toBlurBitmap(
                                    requireContext()
                                )
                            )
                        }
                        return true
                    }

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }
                }).submit()
            }

        }
    }
}