/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package org.example.app.view

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import dev.icerock.moko.mvvm.MvvmActivity
import dev.icerock.moko.mvvm.createViewModelFactory
import org.example.app.BR
import org.example.app.R
import org.example.app.databinding.ActivityNewsBinding
import org.example.library.domain.entity.News
import org.example.library.feature.list.di.ListFactory
import org.example.library.feature.list.presentation.ListViewModel
import javax.inject.Inject

// MvvmActivity for simplify creation of MVVM screen with https://github.com/icerockdev/moko-mvvm
@AndroidEntryPoint
class NewsActivity : MvvmActivity<ActivityNewsBinding, ListViewModel<News>>() {
    override val layoutId: Int = R.layout.activity_news

    @Suppress("UNCHECKED_CAST")
    override val viewModelClass = ListViewModel::class.java as Class<ListViewModel<News>>
    override val viewModelVariableId: Int = BR.viewModel

    @Inject
    lateinit var factory: ListFactory<News>

    // createViewModelFactory is extension from https://github.com/icerockdev/moko-mvvm
    // ViewModel not recreating at configuration changes
    override fun viewModelFactory(): ViewModelProvider.Factory = createViewModelFactory {
        factory.createListViewModel().apply { onCreated() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(binding.refreshLayout) {
            setOnRefreshListener {
                viewModel.onRefresh { isRefreshing = false }
            }
        }

        binding.recyclerView.addOnChildAttachStateChangeListener(
            object : RecyclerView.OnChildAttachStateChangeListener{
                override fun onChildViewDetachedFromWindow(view: View) { }

                override fun onChildViewAttachedToWindow(view: View) {
                    val count = viewModel.state.value.dataValue()?.size ?: return
                    val position = binding.recyclerView.getChildAdapterPosition(view)

                    if (position == count - 1) {
                        viewModel.onLoadNextPage()
                    }
                }

            }
        )
    }
}
