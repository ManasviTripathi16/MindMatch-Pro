package com.manasvi.mindmatchpro

import android.animation.ArgbEvaluator
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.manasvi.mindmatchpro.models.BoardSize
import com.manasvi.mindmatchpro.models.MemoryCard
import com.manasvi.mindmatchpro.models.MemoryGame
import com.manasvi.mindmatchpro.utils.DEFAULT_ICONS

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: MemoryBoardAdapter
    private lateinit var clRoot : ConstraintLayout
    private lateinit var memoryGame: MemoryGame
    private lateinit var rvBoard: RecyclerView
    private lateinit var tvNumMoves: TextView
    private lateinit var tvNumPairs: TextView
    private var boardSize = BoardSize.EASY
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvBoard = findViewById(R.id.rvboard)
        tvNumMoves = findViewById(R.id.tvNumMoves)
        tvNumPairs = findViewById(R.id.tvNumPairs)

        tvNumPairs.setTextColor(ContextCompat.getColor(this,R.color.color_progress_none))

        memoryGame = MemoryGame(boardSize)
        clRoot = findViewById(R.id.clRoot)


        adapter = MemoryBoardAdapter(
            this,
            boardSize,
            memoryGame.cards,
            object : MemoryBoardAdapter.CardClickListener {
                override fun onCardClicked(position: Int) {
                    updateGameWithFlip(position)
                }
            })

        rvBoard.adapter =adapter
        rvBoard.setHasFixedSize(true)
        rvBoard.layoutManager = GridLayoutManager(this, boardSize.getWidth())
    }

    private fun updateGameWithFlip(position: Int) {

        // Error Handling
        // Won , Face Up already
        if(memoryGame.haveWonGame()){
            Snackbar.make(clRoot,"You already won!",Snackbar.LENGTH_LONG).show()
            return
        }
        if(memoryGame.isCardFaceUp(position)){
            Snackbar.make(clRoot,"Invalid move !",Snackbar.LENGTH_SHORT).show()
            return
        }

        // Actually flip over
        if(memoryGame.flipCard(position)){
            tvNumPairs.text = "Pairs: ${memoryGame.numPairsFound} / ${boardSize.getNumPairs()}"
            var color = ArgbEvaluator().evaluate(
                memoryGame.numPairsFound.toFloat()/boardSize.getNumPairs(),
                ContextCompat.getColor(this,R.color.color_progress_none),
                ContextCompat.getColor(this,R.color.color_progress_full)
            ) as Int

            tvNumPairs.setTextColor(color)
            if(memoryGame.haveWonGame()){
                Snackbar.make(clRoot,"You Won! Congratulations",Snackbar.LENGTH_LONG).show()
            }
        }

        tvNumMoves.text = "Moves : ${memoryGame.getNumMoves()}"

        adapter.notifyDataSetChanged()







    }
}