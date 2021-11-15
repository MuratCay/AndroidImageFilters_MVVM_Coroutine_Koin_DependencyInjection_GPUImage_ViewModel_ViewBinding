package com.example.androidimagefilters.activities.filteredimage

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.androidimagefilters.activities.editimage.EditImageActivity
import com.example.androidimagefilters.databinding.ActivityFilteredImageBinding

class FilteredImageActivity : AppCompatActivity() {
    private val binding: ActivityFilteredImageBinding by lazy {
        ActivityFilteredImageBinding.inflate(layoutInflater)
    }
    private lateinit var fileUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        displayFilteredImage()
        setListeners()
    }

    private fun displayFilteredImage() {
        intent.getParcelableExtra<Uri>(EditImageActivity.KEY_FILTERED_IMAGE_URI)?.let { imageUri ->
            fileUri = imageUri
            binding.imageFilteredImage.setImageURI(imageUri)
        }
    }

    private fun setListeners() {
        binding.fabShare.setOnClickListener {
            with(Intent(Intent.ACTION_SEND)) {
                putExtra(Intent.EXTRA_STREAM, fileUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                type = "image/*"
                startActivity(this)
            }
        }
        binding.imageBack.setOnClickListener {
            onBackPressed()
        }
    }
}
