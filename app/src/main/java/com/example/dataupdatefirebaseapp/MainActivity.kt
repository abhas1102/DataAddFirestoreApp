package com.example.dataupdatefirebaseapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.example.dataupdatefirebaseapp.databinding.ActivityMainBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {

    private val personCollectionRef = Firebase.firestore.collection("persons")
    lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            val firstName = binding.etFirstName.text.toString()
            val lastName = binding.etLastName.text.toString()
            val age = binding.etAge.text.toString().toInt()

            val person = Person(firstName,lastName, age)
            savePerson(person)
        }

        binding.button1.setOnClickListener {
            retrievePersons()
        }
    }

    private fun retrievePersons() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val querySnapshot = personCollectionRef.get().await()
            val sb = StringBuilder()
            for(document in querySnapshot.documents) {
                val person = document.toObject<Person>()
               // sb.append("$person\n")

                sb.append("${person?.firstName} ${person?.lastName} ${person?.age}\n")
            }
            withContext(Dispatchers.Main){
                binding.tvPersons.text = sb.toString()
            }

        }catch (e:java.lang.Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(this@MainActivity,e.message, Toast.LENGTH_LONG).show()

            }


        }
    }

    private fun savePerson(person: Person) = CoroutineScope(Dispatchers.IO).launch {
        try {
            personCollectionRef.add(person).await()
            withContext(Dispatchers.Main){
                Toast.makeText(this@MainActivity,"Successfully saved data", Toast.LENGTH_LONG).show()
            }

        }catch (e: Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(this@MainActivity,e.message, Toast.LENGTH_LONG).show()
            }
        }
    }
}