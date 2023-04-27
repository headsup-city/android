package com.krish.headsup.ui.factory

import com.krish.headsup.viewmodel.VideoPostViewModel
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@EntryPoint
@InstallIn(FragmentComponent::class)
interface VideoPostViewModelFactoryEntryPoint {
    fun videoPostViewModelFactory(): VideoPostViewModel.Factory
}