package com.example.androidimagefilters.listeners

import com.example.androidimagefilters.data.ImageFilter

interface ImageFilterListener {
    fun onFilterSelected(imageFilter: ImageFilter)
}