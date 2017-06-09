package com.example.gio.kotlindemo

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.list_item.view.*

/**
 * Copyright by Gio.
 * Created on 6/9/2017.
 */

internal class ListItemAdapter(var items : List<Item>, var mContext : Context) : RecyclerView.Adapter<ListItemAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.list_item, parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val item = items[position]
        holder?.itemView?.tvId?.text = item.id.toString()
        holder?.itemView?.tvContentItem?.text = item.content
        holder?.itemView?.tvDescriptionItem?.text = item.description

        animate(holder)
//        holder.itemView.setOnClickListener { }
    }

    override fun getItemCount(): Int = items.size

    internal inner class ViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView)

    private fun animate(viewHolder: ViewHolder?) {
        val animAnticipateOvershoot = AnimationUtils.loadAnimation(mContext, R.anim.bounce_interpolator)
        viewHolder?.itemView?.animation = animAnticipateOvershoot
    }
}