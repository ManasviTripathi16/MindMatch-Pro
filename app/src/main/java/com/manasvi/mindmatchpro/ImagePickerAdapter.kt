package com.manasvi.mindmatchpro

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.manasvi.mindmatchpro.models.BoardSize
import java.lang.Integer.min

class ImagePickerAdapter(
    private val context: Context,
    private val imagesUris: List<Uri>,
    private val boardSize : BoardSize,
    private val imageClickListener: ImageClickListener
) : RecyclerView.Adapter<ImagePickerAdapter.ViewHolder>() {

    interface ImageClickListener {
        fun onPlaceHolderClicked()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view:View = LayoutInflater.from(context).inflate(R.layout.card_image,parent,false)
        val cardWidth = parent.width / boardSize.getWidth()
        val cardHeight = parent.height / boardSize.getHeight()
        val cardSizeLength = min(cardWidth,cardHeight)
        val layoutParams:ViewGroup.LayoutParams = view.findViewById<ImageView>(R.id.ivCustomImage).layoutParams
        layoutParams.width = cardSizeLength
        layoutParams.height = cardSizeLength

        return ViewHolder(view)


    }

    override fun getItemCount() = boardSize.getNumPairs()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(position<imagesUris.size){
            holder.bind(imagesUris[position])
        }else{
            holder.bind()
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val ivCustomImage = itemView.findViewById<ImageView>(R.id.ivCustomImage)
        fun bind(uri : Uri){
            ivCustomImage.setImageURI(uri)
            ivCustomImage.setOnClickListener(null)

        }
        fun bind(){

            ivCustomImage.setOnClickListener{
                imageClickListener.onPlaceHolderClicked()

            }

        }

    }

}

