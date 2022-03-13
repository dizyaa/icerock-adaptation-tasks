/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package org.example.library.feature.config.di

import dev.icerock.moko.mvvm.dispatcher.EventsDispatcher
import dev.icerock.moko.permissions.PermissionsController
import org.example.library.feature.config.model.ConfigStore
import org.example.library.feature.config.presentation.ConfigViewModel

class ConfigFactory(
    private val configStore: ConfigStore,
    private val validations: ConfigViewModel.Validations,
    private val defaultToken: String,
    private val defaultLanguage: String,
    private val strings: ConfigViewModel.Strings,
) {
    fun createConfigViewModel(
        eventsDispatcher: EventsDispatcher<ConfigViewModel.EventsListener>,
        permissionsController: PermissionsController
    ) = ConfigViewModel(
        eventsDispatcher = eventsDispatcher,
        permissionsController = permissionsController,
        configStore = configStore,
        validations = validations,
        defaultToken = defaultToken,
        defaultLanguage = defaultLanguage,
        strings = strings
    )
}
