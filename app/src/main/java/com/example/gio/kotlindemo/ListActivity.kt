package com.example.gio.kotlindemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_list.*

class ListActivity : AppCompatActivity() {
    private var mItems : MutableList<Item> = mutableListOf()
    private var mAdapter : ListItemAdapter? = null
    private var layoutManager : RecyclerView.LayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        for (i in 0..20) {
            mItems.add(i, Item(i, "Item i", "Description i"))
        }

        layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        mAdapter = ListItemAdapter(mItems, this)
        rvItems.layoutManager = layoutManager
        rvItems.adapter = mAdapter
    }
}
