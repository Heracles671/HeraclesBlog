package com.example.imageprocess

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.imageprocess.ui.main.MainFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }

        fun a(data: String): String {
            return data
        }
        val b = fun (data: String): String {
            return data
        }
        val c = { data: String ->
            data
        }
        a("aa")
        b("bb")
        c("cc")

        val d = ::a
        d("dd")
    }
}