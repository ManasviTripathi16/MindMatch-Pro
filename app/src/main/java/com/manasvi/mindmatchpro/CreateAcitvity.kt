package com.manasvi.mindmatchpro

import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.manasvi.mindmatchpro.models.BoardSize
import com.manasvi.mindmatchpro.utils.EXTRA_BOARD_SIZE
import com.manasvi.mindmatchpro.utils.isPermissionGranted
import com.manasvi.mindmatchpro.utils.requestPermission
import java.io.ByteArrayOutputStream

class CreateAcitvity : AppCompatActivity() {

    companion object {
        private const val PICK_PHOTOS_CODE = 655
        private const val READ_EXTERNAL_PHOTO_CODE = 248
        private const val READ_PHOTOS_PERMISSION = android.Manifest.permission.READ_EXTERNAL_STORAGE
    }

    private lateinit var boardSize: BoardSize
    private var numImagesRequire = -1
    private lateinit var etGameName: EditText
    private lateinit var btnSave: Button
    private lateinit var rvImagePicker: RecyclerView
    private val chosenImagesUris = mutableListOf<Uri>()
    private lateinit var adapter: ImagePickerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_acitvity)

        rvImagePicker = findViewById(R.id.rvImagePicker)
        btnSave = findViewById(R.id.btnSave)
        etGameName = findViewById(R.id.etGameName)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        boardSize = intent.getSerializableExtra(EXTRA_BOARD_SIZE) as BoardSize
        numImagesRequire = boardSize.getNumPairs()
        supportActionBar?.title = "Choose pictures (0 / $numImagesRequire) "

        btnSave.setOnClickListener {
            saveDataToFirebase()
        }
        etGameName.filters = arrayOf(InputFilter.LengthFilter(14))
        etGameName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                btnSave.isEnabled = shouldEnableSaveButton()
            }

        })
        adapter = ImagePickerAdapter(this,
            chosenImagesUris,
            boardSize,
            object : ImagePickerAdapter.ImageClickListener {
                override fun onPlaceHolderClicked() {
                    if (isPermissionGranted(this@CreateAcitvity, READ_PHOTOS_PERMISSION)) {
                        launchIntentForPhotos()
                    } else {
                        requestPermission(
                            this@CreateAcitvity, READ_PHOTOS_PERMISSION, READ_EXTERNAL_PHOTO_CODE
                        )
                    }

                }

            })
        rvImagePicker.adapter = adapter
        rvImagePicker.setHasFixedSize(true)
        rvImagePicker.layoutManager = GridLayoutManager(this, boardSize.getWidth())


    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        if (requestCode == READ_EXTERNAL_PHOTO_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchIntentForPhotos()
            } else {
                Toast.makeText(
                    this,
                    "In order to create a custom game , access to photos is mandatory",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != PICK_PHOTOS_CODE || resultCode != Activity.RESULT_OK || data == null) {
            Log.w("YRYR", "DID NOT GET DATA BACK FROM THE LAUNCHED ACTIVITY")
            return
        }
        val selectedUri: Uri? = data.data
        val clipData: ClipData? = data.clipData

        if (clipData != null) {
            Log.w("CLIPDATA NOT NULL", "Clipdata numImages : ${clipData.itemCount} : $clipData")
            for (i in 0 until clipData.itemCount) {
                val clipItem = clipData.getItemAt(i)
                if (chosenImagesUris.size < numImagesRequire) {
                    chosenImagesUris.add(clipItem.uri)
                }

            }
        } else if (selectedUri != null) {
            Log.w("SELECTEDURIS", "data : $selectedUri")
            chosenImagesUris.add(selectedUri)
        }

        adapter.notifyDataSetChanged()
        supportActionBar?.title = "Choose pics ( ${chosenImagesUris.size}/$numImagesRequire )"

        btnSave.isEnabled = shouldEnableSaveButton()


    }

    private fun saveDataToFirebase() {
        Log.i("ERWY", "Storing to firebase")
        for ((index, phtotUri) in chosenImagesUris.withIndex()) {
            val imageByteArray = getImageByteArray(phtotUri)

        }
    }

    private fun getImageByteArray(phtotUri: Uri): ByteArray {
        val originalBitmap: Bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(contentResolver, phtotUri)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(contentResolver, phtotUri)
        }
        Log.i("YAYA", "Original width ${originalBitmap.width} and heigth ${originalBitmap.height} ")
        val scaledBitmap = BitmapScaler.scaleToFitHeight(originalBitmap, 250)
        Log.i("YAYA", "Scaled width ${scaledBitmap.width} and heigth ${scaledBitmap.height} ")
        val byteOutputStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 60, byteOutputStream)
        return byteOutputStream.toByteArray()
    }


    private fun shouldEnableSaveButton(): Boolean {

        if (chosenImagesUris.size != numImagesRequire) {
            return false
        }

        if (etGameName.text.isBlank() || etGameName.length() < 3) {
            return false
        }

        return true

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun launchIntentForPhotos() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(Intent.createChooser(intent, "Choose pictures"), PICK_PHOTOS_CODE)
    }

}