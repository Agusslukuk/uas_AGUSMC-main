package com.example.uas_ali

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uas_ali.databinding.ActivityContentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage

class ContentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityContentBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var billiardList: MutableList<Billiard>
    private lateinit var billiardAdapter: BilliardAdapter
    private var mStorage: FirebaseStorage? = null
    private var mDatabaseRef: DatabaseReference? = null
    private var mDBListener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // Setup RecyclerView
        val billiardRecyclerView = binding.billiardRecyclerView
        billiardRecyclerView.setHasFixedSize(true)
        billiardRecyclerView.layoutManager = LinearLayoutManager(this@ContentActivity)

        billiardList = ArrayList()
        billiardAdapter = BilliardAdapter(this@ContentActivity, billiardList)
        billiardRecyclerView.adapter = billiardAdapter

        // Inisialisasi Firebase Storage dan Database Reference
        mStorage = FirebaseStorage.getInstance()
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("movie")

        // Menambahkan listener untuk membaca data dari database
        mDBListener = mDatabaseRef!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                billiardList.clear() // Membersihkan daftar sebelum menambahkan data baru
                for (postSnapshot in snapshot.children) {
                    val billiard = postSnapshot.getValue(Billiard::class.java)
                    if (billiard != null) {
                        billiardList.add(billiard)
                    }
                }
                billiardAdapter.notifyDataSetChanged() // Memberitahu adapter bahwa data telah berubah
                binding.myDataLoaderProgressBar.visibility = View.GONE // Menghilangkan progres bar setelah data dimuat
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ContentActivity, error.message, Toast.LENGTH_SHORT).show()
                binding.myDataLoaderProgressBar.visibility = View.GONE
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.mymenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add_item -> {
                // Membuka AddItemActivity untuk menambah item baru
                val intent = Intent(this, AddItemActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.logout -> {
                firebaseAuth.signOut()
                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
