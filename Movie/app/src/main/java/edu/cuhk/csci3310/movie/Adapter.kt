package edu.cuhk.csci3310.movie

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import io.github.armcha.coloredshadow.ShadowImageView
import kotlinx.android.synthetic.main.item.view.*

class Adapter(private val itemList: List<Item>,private val itemClickListener: ItemClickListener) : RecyclerView.Adapter<Adapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item,parent,false)
        val holder = ViewHolder(itemView)

        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(holder.absoluteAdapterPosition)
        }

        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.title.text = currentItem.title
        holder.rating.max = 10
        holder.rating.rating = currentItem.rate /2
        holder.rating.stepSize = 0.2F
        holder.label.text = currentItem.label
        holder.minute.text = currentItem.minute
        holder.detail.text = currentItem.detail


        if (currentItem.rate ==0f){
            holder.rateText.text= "N/A"
        }else{
            holder.rateText.text= currentItem.rate.toString()
        }



        Glide.with(holder.image.context)
            .load(currentItem.postViewSrc)
            .placeholder(R.drawable.loading)
            .into(object :CustomViewTarget<ShadowImageView,Drawable>(holder.image) {

                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    holder.image.radiusOffset = 1f
                    holder.image.setImageDrawable(resource)
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    Log.d("list", "load fail  " + currentItem.postViewSrc)
                }

                override fun onResourceCleared(placeholder: Drawable?) {
                    Log.d("list", "onResourceCleared  " + currentItem.postViewSrc)
                    holder.image.setImageDrawable(placeholder)

                }
            })


    }

    override fun getItemCount() = itemList.size

    class ViewHolder(itemView :View): RecyclerView.ViewHolder(itemView){

        val title : TextView = itemView.itemTitle
        val image : ShadowImageView = itemView.itemImage
        val rating : RatingBar = itemView.itemRate
        val label : TextView = itemView.itemLabel
        val minute : TextView = itemView.itemTime
        val detail : TextView = itemView.itemDetail
        val rateText : TextView = itemView.itemRateText


    }

public interface ItemClickListener {
    fun onItemClick(position:Int)
}

}