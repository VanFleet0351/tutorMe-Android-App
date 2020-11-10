package com.example.tutorme

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.tutorme.databinding.ActivityRegistrationBinding
import com.example.tutorme.swipe_view.SwipeActivity

class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val email = intent.getStringExtra("user_email")

        binding.emailTxt.setText(email)

        binding.submitBtn.setOnClickListener { view: View ->
            val intent = Intent(this, SwipeActivity::class.java)
            startActivity(intent)
        }
    }
}
