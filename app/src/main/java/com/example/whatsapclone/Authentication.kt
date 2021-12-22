package com.example.whatsapclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.whatsapclone.activity.createAccount
import com.example.whatsapclone.activity.loginActivity
import kotlinx.android.synthetic.main.activity_authentication.*

class Authentication : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        auth_login.setOnClickListener {
            startActivity(Intent(this, loginActivity::class.java))
        }
        auth_signup.setOnClickListener {
            startActivity(Intent(this, createAccount::class.java))
        }
    }
}