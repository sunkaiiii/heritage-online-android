package com.example.sunkai.heritage.fragment

import android.content.Intent
import android.graphics.RenderEffect
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.databinding.FragmentPeopleDetailBinding
import com.example.sunkai.heritage.entity.CollaborativeViewModelImpl
import com.example.sunkai.heritage.entity.PeopleDetailViewModel
import com.example.sunkai.heritage.entity.response.NewsDetail
import com.example.sunkai.heritage.entity.response.NewsDetailContent
import com.example.sunkai.heritage.fragment.baseFragment.BaseViewBindingFragment
import com.example.sunkai.heritage.tools.Utils
import com.example.sunkai.heritage.tools.buildUrl
import com.example.sunkai.heritage.tools.loadImageFromServer
import com.example.sunkai.heritage.tools.toBlurBitmap
import com.example.sunkai.heritage.value.DATA
import com.example.sunkai.heritage.value.TYPE_TEXT
import com.example.sunkai.heritage.views.CollaborativeBounceView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PeopleDetailFragment : BaseViewBindingFragment<FragmentPeopleDetailBinding>() {
    private var isFullyScrollToTheFirstItem: (() -> Boolean)? = null

    private val viewModel by lazy { ViewModelProvider(this).get(PeopleDetailViewModel::class.java) }

    private val collaborativeViewModel by lazy { ViewModelProvider(this).get(CollaborativeViewModelImpl::class.java) }

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
        binding.toolbarLayout.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.toolbarLayout.toolbar.setBackgroundColor(android.graphics.Color.TRANSPARENT)
    }

    private fun setDataToView(data: NewsDetail) {
        loadImage(data)
        initScrollBehaviour()
        binding.peopleDetailInformationContainer.setContent {
            PeopleDetailContentView(data)
        }
    }

    @Composable
    fun PeopleDetailContentView(peopleDetail: NewsDetail) {
        val listState = rememberLazyListState()
        isFullyScrollToTheFirstItem = { listState.isFullyScrolledToTheFirst() }
        val isTextLine = { content: NewsDetailContent -> content.type == TYPE_TEXT }
        LazyColumn(Modifier.fillMaxSize(), state = listState) {
            item {
                PeopleDetailTitleView(peopleDetail)
            }
            val detailList = peopleDetail.content
            items(detailList.size) { index ->
                val data = detailList[index]
                if (isTextLine(data)) {
                    val isLastImage = index - 1 >= 0 && !isTextLine(detailList[index - 1])
                    PeopleDetailTextLine(data, isLastImage)
                } else {
                    PeopleDetailImageView(data)
                }
            }
            val relevantNews = peopleDetail.relativeNews
            items(relevantNews.size) { index ->
                val news = relevantNews[index]
                Text(news.title)
            }
        }
    }

    @Composable
    fun PeopleDetailTextLine(content: NewsDetailContent, isLastOneImage: Boolean = false) {
        Text(
                content.content,
                color = Color(Utils.getColorResourceValue(R.color.news_detail_text_color)),
                fontSize = 16.sp,
                fontWeight = if (isLastOneImage) FontWeight.Bold else FontWeight.Normal
        )
    }

    @OptIn(ExperimentalCoilApi::class)
    @Composable
    fun PeopleDetailImageView(content: NewsDetailContent) {
        val url = content.compressImg ?: content.content
        Image(
                painter = rememberImagePainter(buildUrl(url), builder = {
                    placeholder(R.drawable.place_holder)
                }),
                contentDescription = null,
            Modifier
                .shadow(8.dp, RoundedCornerShape(27.dp))
                .clip(
                    RoundedCornerShape(27.dp)
                )
                .fillMaxWidth()
                .height(200.dp),
                contentScale = ContentScale.Crop
        )
    }

    @Composable
    fun PeopleDetailTitleView(data: NewsDetail) {
        val titleColor = if (isSystemInDarkTheme()) Color.White else Color.Black
        Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                    data.title,
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp,
                    modifier = Modifier.padding(start = 40.dp, end = 40.dp),
                    fontWeight = FontWeight.Bold,
                    color = titleColor
            )
            Text(data.author, fontSize = 16.sp, color = titleColor)
            Text(data.time ?: "", fontSize = 16.sp, color = titleColor)
            ClickableText(text = AnnotatedString(getString(R.string.source_from)+getString(R.string.source_url)), onClick = {
                val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(getString(R.string.source_url)+data.link))
                startActivity(intent)
            },style = TextStyle(color = titleColor))
        }
    }

    private fun initScrollBehaviour() {
        val initialAlphaOfCollaborativeView = binding.peopleRoundedBackgroundView.alpha
        binding.peopleDetailCollaborativeLayout.setBounceBoundry(0, view?.height?.div(2)
                ?: 0, collaborativeViewModel)
        binding.peopleDetailCollaborativeLayout.setTouchEventBlocker {
            return@setTouchEventBlocker binding.peopleDetailCollaborativeLayout.translationY != 0f
        }
        binding.peopleDetailCollaborativeLayout.setMoveEventBlocker { event, moveOrientation ->
            if (moveOrientation == CollaborativeBounceView.MoveOrientation.Up) {
                return@setMoveEventBlocker false
            }
            return@setMoveEventBlocker isFullyScrollToTheFirstItem?.let { !it() } ?: false
        }
        binding.peopleDetailCollaborativeLayout.setOnMoveAction { distance, offsetPercentage ->
            val currentAlpha =
                    initialAlphaOfCollaborativeView - (initialAlphaOfCollaborativeView * offsetPercentage)
            binding.peopleRoundedBackgroundView.alpha = currentAlpha
            binding.peopleMainImageBlur.alpha = 1 * offsetPercentage
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

    private fun LazyListState.isFullyScrolledToTheFirst(): Boolean {
        val firstItem = layoutInfo.visibleItemsInfo.firstOrNull()
        return firstItem?.index == 0 && firstItem.offset == 0
    }

}