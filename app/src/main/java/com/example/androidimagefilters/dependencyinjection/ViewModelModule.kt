package com.example.androidimagefilters.dependencyinjection

import com.example.androidimagefilters.viewmodels.EditImageViewModel
import com.example.androidimagefilters.viewmodels.SavedImagesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { EditImageViewModel(editImageRepository = get()) }
    viewModel { SavedImagesViewModel(savedImagesRepository = get()) }
}