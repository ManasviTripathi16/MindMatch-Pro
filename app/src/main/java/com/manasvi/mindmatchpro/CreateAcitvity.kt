package com.manasvi.mindmatchpro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.manasvi.mindmatchpro.models.BoardSize
import com.manasvi.mindmatchpro.utils.EXTRA_BOARD_SIZE

class CreateAcitvity : AppCompatActivity() {
    private lateinit var boardSize:BoardSize
    private var numImagesRequire = -1

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_acitvity)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        boardSize = intent.getSerializableExtra(EXTRA_BOARD_SIZE) as BoardSize
        numImagesRequire = boardSize.getNumPairs()
        supportActionBar?.title = "Choose pictures (0 / $numImagesRequire) "

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==android.R.id.home){
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}