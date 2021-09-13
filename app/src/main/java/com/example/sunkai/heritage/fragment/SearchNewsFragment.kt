package com.example.sunkai.heritage.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import com.example.sunkai.heritage.fragment.baseFragment.BaseGlideFragment

class SearchNewsFragment : BaseGlideFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                SearchNewsView()
            }
        }
    }

    @Composable
    fun SearchNewsView() {
        Column(
            Modifier
                .fillMaxSize()
                .padding(start = 30.dp, end = 30.dp)
        ) {
            SearchNewsTopBar(onBack = { findNavController().popBackStack() })
            SearchNewsEditTextBar()
        }
    }

    @Composable
    fun SearchNewsTopBar(onBack: () -> Unit) {
        TopAppBar(
            title = { Text("") },
            backgroundColor = Color.Transparent,
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                    )
                }
            }
        )
    }

    @Composable
    fun SearchNewsEditTextBar() {

        ConstraintLayout(Modifier.fillMaxWidth()) {
            val (editTextCard, filterButton) = createRefs()
            Card(
                Modifier
                    .size(40.dp)
                    .constrainAs(filterButton) {
                        end.linkTo(parent.end)
                    }) {

            }
            Card(
                Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .constrainAs(editTextCard) {
                        end.linkTo(filterButton.start, margin = 18.dp)
                        start.linkTo(parent.start)
                    }, shape = RoundedCornerShape(21.dp)
            ) {

            }
        }
    }

    @Preview
    @Composable
    fun SearchNewsTopBarPreview() {
        SearchNewsTopBar {

        }
    }

    @Preview
    @Composable
    fun SearchNewsEditTextBarPreview() {
        SearchNewsEditTextBar()
    }
}