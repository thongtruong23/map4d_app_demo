package com.truongthong.map4d.adapter


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.truongthong.map4d.R
import com.truongthong.map4d.model.nearby.ResultNearby
import com.truongthong.map4d.ui.CoffeeFragment

class CoffeeAdapter(
    var restaurantList: MutableList<ResultNearby>,
    var clickListener: CoffeeFragment
) :
    RecyclerView.Adapter<CoffeeAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name = itemView.findViewById(R.id.txt_name_place) as TextView
        var address = itemView.findViewById(R.id.txt_address_place) as TextView

        @SuppressLint("SetTextI18n")
        fun initialaze(restaurantList: ResultNearby, action: OnMapItemClickListener) {

            if (restaurantList.name.length > 30) {
                name.text = restaurantList.name.substring(0, 30) + "..."
            } else {
                name.text = restaurantList.name
            }
            if (restaurantList.address.length > 45) {
                address.text = restaurantList.address.substring(0, 35) + "...."
            } else {
                address.text = restaurantList.address
            }

            itemView.setOnClickListener {
                action.onItemClick(restaurantList, adapterPosition)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_nearby_place, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.initialaze(restaurantList.get(position), clickListener)

    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }

    fun setLocationData(nearbyLocation: MutableList<ResultNearby>?) {
        restaurantList = nearbyLocation!!
        notifyDataSetChanged()
    }


    //interface xu ly su kien onclick item
    interface OnMapItemClickListener {
        fun onItemClick(restaurantList: ResultNearby, position: Int)
    }

//    private val onItemClickListener: ((MapLocation) -> Unit)? = null
//
//    fun setOnItemClickListener(listener: (MapLocation) -> Unit) {
//        onItemClickListener
//    }


}