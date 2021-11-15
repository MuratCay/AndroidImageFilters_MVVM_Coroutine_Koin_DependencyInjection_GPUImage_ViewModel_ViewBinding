package com.example.androidimagefilters.dependencyinjection

import com.example.androidimagefilters.repositories.EditImageRepository
import com.example.androidimagefilters.repositories.EditImageRepositoryImpl
import com.example.androidimagefilters.repositories.SavedImagesRepository
import com.example.androidimagefilters.repositories.SavedImagesRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    factory<EditImageRepository> { EditImageRepositoryImpl(androidContext()) }
    factory<SavedImagesRepository> { SavedImagesRepositoryImpl(androidContext()) }
}