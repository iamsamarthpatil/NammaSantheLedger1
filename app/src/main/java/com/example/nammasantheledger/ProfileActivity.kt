package com.example.nammasantheledger

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var shopName: EditText
    private lateinit var ownerName: EditText
    private lateinit var ownerPhone: EditText
    private lateinit var ownerAddress: EditText
    private lateinit var saveBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profile)

        shopName = findViewById(R.id.shopName)
        ownerName = findViewById(R.id.ownerName)
        ownerPhone = findViewById(R.id.ownerPhone)
        ownerAddress = findViewById(R.id.ownerAddress)
        saveBtn = findViewById(R.id.saveProfileBtn)

        val prefs = getSharedPreferences(
            "ProfilePrefs",
            Context.MODE_PRIVATE
        )

        shopName.setText(
            prefs.getString("shopName", "")
        )

        ownerName.setText(
            prefs.getString("ownerName", "")
        )

        ownerPhone.setText(
            prefs.getString("ownerPhone", "")
        )

        ownerAddress.setText(
            prefs.getString("ownerAddress", "")
        )

        saveBtn.setOnClickListener {

            prefs.edit()
                .putString(
                    "shopName",
                    shopName.text.toString()
                )
                .putString(
                    "ownerName",
                    ownerName.text.toString()
                )
                .putString(
                    "ownerPhone",
                    ownerPhone.text.toString()
                )
                .putString(
                    "ownerAddress",
                    ownerAddress.text.toString()
                )
                .apply()

            Toast.makeText(
                this,
                "Profile Saved Successfully",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}