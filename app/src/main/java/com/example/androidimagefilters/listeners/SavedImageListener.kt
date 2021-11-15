package com.example.androidimagefilters.listeners

import java.io.File

interface SavedImageListener {
    fun onImageClicked(file: File)
}