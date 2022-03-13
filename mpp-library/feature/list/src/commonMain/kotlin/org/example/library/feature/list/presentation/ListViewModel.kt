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
import dev.icerock.moko.paging.IdComparator
import dev.icerock.moko.paging.IdEntity
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
    private val unitsFactory: UnitsFactory<T>,
    private val pageSize: Int = 20,
) : ViewModel() {

    private val pagination: Pagination<Tile<T>> = Pagination(
        parentScope = viewModelScope,
        dataSource = LambdaPagedListDataSource { tiles ->
            val tileList: List<T> = if (tiles == null) {
                listSource.getList(1, pageSize)
            } else {
                listSource.getList(
                    page = (tiles.size / pageSize) + 1,
                    pageSize = pageSize
                )
            }

            val newTiles = tileList.mapIndexed { index, it ->
                Tile(
                    id = tiles?.size?.toLong()?.plus(index) ?: 0L,
                    data = it
                )
            }

            newTiles.plus(tiles ?: emptyList())
        },
        comparator = IdComparator(),
        nextPageListener = { },
        refreshListener = { },
        initValue = emptyList()
    )

    val state: LiveData<ResourceState<List<TableUnitItem>, StringDesc>> = pagination.state
        .dataTransform {
            map { news ->
                news.map { unitsFactory.createTile(it.data) }
            }
        }
        .errorTransform {
            // new type inferrence require set types oO
            map { it.message?.desc() ?: strings.unknownError.desc() }
        }

    fun onCreated() {
        pagination.loadFirstPage()
    }

    fun onRetryPressed() {
        pagination.loadFirstPage()
    }

    fun onRefresh(completion: () -> Unit) {
        pagination.refresh()
        completion()
    }

    fun onLoadNextPage() {
        pagination.loadNextPage()
    }

    data class Tile<T>(
        override val id: Long,
        val data: T
    ): IdEntity

    interface UnitsFactory<T> {
        fun createTile(data: T): TableUnitItem
    }

    interface Strings {
        val unknownError: StringResource
    }
}
