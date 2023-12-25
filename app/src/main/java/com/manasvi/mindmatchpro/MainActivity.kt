package com.manasvi.mindmatchpro

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.manasvi.mindmatchpro.models.BoardSize
import com.manasvi.mindmatchpro.utils.DEFAULT_ICONS

class MainActivity : AppCompatActivity() {

    private lateinit var rvBoard: RecyclerView
    private lateinit var tvNumMoves: TextView
    private lateinit var tvNumPairs: TextView

    private var boardSize = BoardSize.HARD
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvBoard = findViewById(R.id.rvboard)
        tvNumMoves = findViewById(R.id.tvNumMoves)
        tvNumPairs = findViewById(R.id.tvNumPairs)

        val chosenImage: List<Int> = DEFAULT_ICONS.shuffled().take(boardSize.getNumPairs())
        val randomizedImages : List<Int> = (chosenImage+chosenImage).shuffled()

        rvBoard.adapter = MemoryBoardAdapter(this, boardSize,randomizedImages)
        rvBoard.setHasFixedSize(true)
        rvBoard.layoutManager = GridLayoutManager(this, boardSize.getWidth())

    }
}