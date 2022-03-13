/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package org.example.library.feature.list.presentation

import dev.icerock.moko.mvvm.ResourceState
import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.dataTransform
import dev.icerock.moko.mvvm.livedata.errorTransform
import dev.icerock.moko.mvvm.livedata.map
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.paging.LambdaPagedListDataSource
import dev.icerock.moko.paging.Pagination
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.units.TableUnitItem
import org.example.library.feature.list.model.ListSource

class ListViewModel<T>(
    private val listSource: ListSource<T>,
    private val strings: Strings,
    private val unitsFactory: UnitsFactory<T>
) : ViewModel() {

    private val pagination: Pagination<T> = Pagination(
        parentScope = viewModelScope,
        dataSource = LambdaPagedListDataSource {
            val list = listSource.getList(
                page = it?.size?.div(20) ?: 1
            )
            it?.plus(list) ?: listSource.getList()
        },
        comparator = { a, b ->
            a.hashCode() - b.hashCode()
        },
        nextPageListener = { },
        refreshListener = { },
        initValue = emptyList()
    )

    val state: LiveData<ResourceState<List<TableUnitItem>, StringDesc>> = pagination.state
        .dataTransform {
            map { news ->
                news.map { unitsFactory.createTile(it) }
            }
        }
        .errorTransform {
            // new type inferrence require set types oO
            map { it.message?.desc() ?: strings.unknownError.desc() }
        }

    fun onCreated() {
        loadList()
    }

    fun onRetryPressed() {
        loadList()
    }

    fun onRefresh(completion: () -> Unit) {
        pagination.refresh()
        completion()
    }

    private fun loadList() {
        pagination.loadFirstPage()
    }

    interface UnitsFactory<T> {
        fun createTile(data: T): TableUnitItem
    }

    interface Strings {
        val unknownError: StringResource
    }
}
