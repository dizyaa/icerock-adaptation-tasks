/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package org.example.library.feature.config.presentation

import dev.icerock.moko.fields.FormField
import dev.icerock.moko.fields.liveBlock
import dev.icerock.moko.fields.validate
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcher
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcherOwner
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.coroutines.launch
import org.example.library.feature.config.model.ConfigStore

class ConfigViewModel(
    override val eventsDispatcher: EventsDispatcher<EventsListener>,
    private val configStore: ConfigStore,
    val permissionsController: PermissionsController,
    validations: Validations,
    defaultToken: String,
    defaultLanguage: String
) : ViewModel(), EventsDispatcherOwner<ConfigViewModel.EventsListener> {

    val apiTokenField: FormField<String, StringDesc> =
        FormField(configStore.apiToken ?: defaultToken, liveBlock(validations::validateToken))
    val languageField: FormField<String, StringDesc> =
        FormField(configStore.language ?: defaultLanguage, liveBlock(validations::validateLanguage))

    private val fields = listOf(apiTokenField, languageField)

    fun onSubmitPressed() {
        checkPermissions()
    }

    private fun checkPermissions() {
        viewModelScope.launch {
            try {
                listOf(
                    Permission.GALLERY,
                    Permission.LOCATION,
                    Permission.STORAGE
                ).forEach {
                    permissionsController.providePermission(it)
                }

                checkValidateFields()
            } catch(deniedAlways: DeniedAlwaysException) {
                eventsDispatcher.dispatchEvent { showError("DeniedAlwaysException") }
            } catch(denied: DeniedException) {
                eventsDispatcher.dispatchEvent { showError("DeniedException") }
            }
        }
    }

    private fun checkValidateFields() {
        if (!fields.validate()) return

        configStore.apiToken = apiTokenField.value()
        configStore.language = languageField.value()

        eventsDispatcher.dispatchEvent { routeToNews() }
    }

    interface Validations {
        fun validateToken(value: String): StringDesc?
        fun validateLanguage(value: String): StringDesc?
    }

    interface EventsListener {
        fun routeToNews()
        fun showError(message: String)
    }
}
