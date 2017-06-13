package com.example.gio.kotlindemo.activities

import com.example.gio.kotlindemo.R
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Copyright by Gio.
 * Created on 5/25/2017.
 */

class MainActivity: android.app.Activity() {
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnOk.text = "List"
        btnOk.setOnClickListener {
            val intent = android.content.Intent(this, ListActivity::class.java)
            startActivity(intent)
        }

        btnGravity.text = "Gravity"
        btnGravity.setOnClickListener {
            val intent = android.content.Intent(this, GravityActivity::class.java)
            startActivity(intent)
        }

        btnMapProject.text = "Map"
        btnMapProject.setOnClickListener {
            val intent = android.content.Intent(this, MapActivity::class.java)
            startActivity(intent)
        }
    }
}
