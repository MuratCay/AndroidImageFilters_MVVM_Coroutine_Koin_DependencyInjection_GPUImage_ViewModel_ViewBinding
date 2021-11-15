package com.example.androidimagefilters.activities.editimage

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.example.androidimagefilters.activities.filteredimage.FilteredImageActivity
import com.example.androidimagefilters.activities.main.MainActivity
import com.example.androidimagefilters.adapters.ImageFiltersAdapter
import com.example.androidimagefilters.data.ImageFilter
import com.example.androidimagefilters.databinding.ActivityEditImageBinding
import com.example.androidimagefilters.listeners.ImageFilterListener
import com.example.androidimagefilters.utilities.displayToast
import com.example.androidimagefilters.utilities.show
import com.example.androidimagefilters.viewmodels.EditImageViewModel
import jp.co.cyberagent.android.gpuimage.GPUImage
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditImageActivity : AppCompatActivity(), ImageFilterListener {

    companion object {
        const val KEY_FILTERED_IMAGE_URI = "filteredImageUri"
    }

    private val binding: ActivityEditImageBinding by lazy {
        ActivityEditImageBinding.inflate(
            layoutInflater
        )
    }
    private val viewModel: EditImageViewModel by viewModel()
    private lateinit var gpuImage: GPUImage

    //Image bitmaps
    private lateinit var originalBitmap: Bitmap
    private val filteredBitmap = MutableLiveData<Bitmap>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setListeners()
        setupObservers()
        prepareImagePreview()
    }

    private fun setupObservers() {
        viewModel.imagePreviewUiState.observe(this, {
            val dataState = it ?: return@observe
            binding.previewProgressBar.visibility =
                if (dataState.isLoading) View.VISIBLE else View.GONE
            dataState.bitmap?.let { bitmap ->
                // ilk kez filtrelenmiş resim = orijinal resim
                originalBitmap = bitmap
                filteredBitmap.value = bitmap

                with(originalBitmap) {
                    gpuImage.setImage(this)
                    binding.imagePreview.show()
                    viewModel.loadImageFilters(this)
                }
            } ?: kotlin.run {
                dataState.error?.let { error ->
                    displayToast(error)
                }
            }
        })

        filteredBitmap.observe(this, { bitmap ->
            binding.imagePreview.setImageBitmap(bitmap)
        })

        viewModel.saveFilteredImageUiState.observe(this, {
            val saveFilteredImageDataState = it ?: return@observe
            if (saveFilteredImageDataState.isLoading) {
                binding.imageSave.visibility = View.GONE
                binding.savingProgressBar.visibility = View.VISIBLE
            } else {
                binding.savingProgressBar.visibility = View.GONE
                binding.imageSave.visibility = View.VISIBLE
            }
            saveFilteredImageDataState.uri?.let { savedImageUri ->
                Intent(
                    applicationContext,
                    FilteredImageActivity::class.java
                ).also { filteredImageIntent ->
                    filteredImageIntent.putExtra(KEY_FILTERED_IMAGE_URI, savedImageUri)
                    startActivity(filteredImageIntent)
                }
            } ?: run {
                saveFilteredImageDataState.error.let { error ->
                    Toast.makeText(applicationContext, "$error", Toast.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.imageFiltersUiState.observe(this, {
            val imageFiltersDataState = it ?: return@observe
            binding.imageFiltersProgressBar.visibility =
                if (imageFiltersDataState.isLoading) View.VISIBLE else View.GONE
            imageFiltersDataState.imageFilters?.let { imageFilters ->
                ImageFiltersAdapter(imageFilters, this).also { adapter ->
                    binding.filtersRecyclerView.adapter = adapter
                }
            } ?: kotlin.run {
                imageFiltersDataState.error?.let { error ->
                    displayToast(error)
                }
            }
        })
    }


    private fun prepareImagePreview() {
        gpuImage = GPUImage(applicationContext)
        intent.getParcelableExtra<Uri>(MainActivity.KEY_IMAGE_URL)?.let { imageUri ->
            viewModel.prepareImagePreview(imageUri)
        }
    }

// prepareImagePreview() fonksiyonundan dolayı bu kod deprecated oldu.
//        private fun displayImagePreview() {
//            intent.getParcelableExtra<Uri>(MainActivity.KEY_IMAGE_URL)?.let { imageUri ->
//                val inputStream = contentResolver.openInputStream(imageUri)
//                val bitmap = BitmapFactory.decodeStream(inputStream)
//                binding.imagePreview.setImageBitmap(bitmap)
//                binding.imagePreview.visibility = View.VISIBLE
//            }
//        }

    private fun setListeners() {
        binding.imageBack.setOnClickListener {
            onBackPressed()
        }
        binding.imageSave.setOnClickListener {
            filteredBitmap.value?.let { bitmap ->
                viewModel.saveFilteredImage(bitmap)
            }
        }

        /**
         * uzun süre tıklandığında orijinal resmi göreceğiz.
         * bu sayede filterlenmiş resim le original resim arasında ki farkı görücez
         */
        binding.imagePreview.setOnLongClickListener {
            binding.imagePreview.setImageBitmap(originalBitmap)
            return@setOnLongClickListener false
        }
        binding.imagePreview.setOnClickListener {
            binding.imagePreview.setImageBitmap(filteredBitmap.value)
        }
    }

    override fun onFilterSelected(imageFilter: ImageFilter) {
        with(imageFilter) {
            with(gpuImage) {
                setFilter(filter)
                filteredBitmap.value = bitmapWithFilterApplied
            }
        }
    }
}