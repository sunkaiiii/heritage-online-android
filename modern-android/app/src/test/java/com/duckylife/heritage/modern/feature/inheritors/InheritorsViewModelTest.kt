package com.duckylife.heritage.modern.feature.inheritors

import com.duckylife.heritage.modern.core.data.FakeHeritageRepository
import com.duckylife.heritage.modern.core.testing.MainDispatcherRule
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test

class InheritorsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun exposesPagedInheritorsFlow() {
        val viewModel = InheritorsViewModel(
            repository = FakeHeritageRepository(),
        )

        assertNotNull(viewModel.inheritors)
    }
}
