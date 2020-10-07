package com.application.quizap

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.application.quizap.topics.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity : AppCompatActivity() {
    lateinit var drawer: DrawerLayout
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        setSupportActionBar(topBar)
        auth=FirebaseAuth.getInstance()
        var user=auth.currentUser
        var mFirebaseDatabase=FirebaseDatabase.getInstance().getReference("users")
        var textHeader=navigationView.getHeaderView(0).findViewById<TextView>(R.id.textHeader)
        var textName=navigationView.getHeaderView(0).findViewById<TextView>(R.id.textName)
        var imageUser=navigationView.getHeaderView(0).findViewById<ImageView>(R.id.imageUser)
        drawer=findViewById(R.id.drawer_layout)
        toggle =object : ActionBarDrawerToggle(this,drawer,topBar,R.string.navigation_drawer_open,R.string.navigation_drawer_close){
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                mFirebaseDatabase.child(user!!.uid).addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var nUser = snapshot.getValue(User::class.java)
                        if (nUser != null) {
                            textHeader.text = nUser.email
                            textName.text = nUser.name
                            Picasso.get().load(nUser.photoUrl).into(imageUser)
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                    }
                })
            }
            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
            }
        }
        toggle.isDrawerIndicatorEnabled=true
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        navigationView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.signOut->{
                    auth.signOut()
                    finish()
                    startActivity(Intent(this,MainActivity::class.java))
                }
                R.id.deleteProfile-> {
                    MaterialAlertDialogBuilder(this).setTitle("Are you sure you want to delete your profile ?")
                        .setPositiveButton("Yes"){_,_->
                            user?.delete()
                            finish()
                            startActivity(Intent(this,MainActivity::class.java))
                        }.setNegativeButton("No"){_,_->}
                        .show()
                }
                R.id.updatePassword->{
                    val view = layoutInflater.inflate(R.layout.change_password, null)
                    MaterialAlertDialogBuilder(this).setTitle("Enter Email to Change Password")
                        .setView(view)
                        .setPositiveButton("Reset") { _, _ ->
                            var changePassword = view.findViewById<EditText>(R.id.change_password)
                            if (changePassword != null) {
                                if (changePassword.length()<6) {
                                    changePassword.error = "* Password should be min 6 characters"
                                }else {
                                    auth.currentUser?.updatePassword(changePassword.text.toString())
                                }
                            }
                        }
                        .setNegativeButton("Cancel") { _, _ -> }
                        .show()
                }
                else -> {}
            }
            true
        }
        //drawer over
        //games
        cardVideoGames.setOnClickListener {
            startActivity(Intent(this, VgameActivity::class.java))
        }
        cardBoardGames.setOnClickListener {
            startActivity(Intent(this,BgameActivity::class.java))
        }
        cardSports.setOnClickListener {
            startActivity(Intent(this,SportsActivity::class.java))
        }
        //education
        cardGeography.setOnClickListener {
            startActivity(Intent(this, GeographyActivity::class.java))
        }
        cardMaths.setOnClickListener {
            startActivity(Intent(this,MathsActivity::class.java))
        }
        cardHistory.setOnClickListener {
            startActivity(Intent(this,HistoryActivity::class.java))
        }
        //cartoon
        cardCartoon.setOnClickListener {
            startActivity(Intent(this,CartoonActivity::class.java))
        }
        cardAnime.setOnClickListener {
            startActivity(Intent(this,AnimeActivity::class.java))
        }
        //science
        cardVehicle.setOnClickListener {
            startActivity(Intent(this,VehicleActivity::class.java))
        }
        cardGadget.setOnClickListener {
            startActivity(Intent(this,GadgetActivity::class.java))
        }
        cardNature.setOnClickListener {
            startActivity(Intent(this,NatureActivity::class.java))
        }
        //entertainment
        cardFilm.setOnClickListener {
            startActivity(Intent(this,FilmActivity::class.java))
        }
        cardMusic.setOnClickListener {
            startActivity(Intent(this,MusicActivity::class.java))
        }
        cardTv.setOnClickListener {
            startActivity(Intent(this,TelevisionActivity::class.java))
        }
        cardComic.setOnClickListener {
            startActivity(Intent(this,ComicActivity::class.java))
        }
        //politics
        cardGk.setOnClickListener {
            startActivity(Intent(this,GkActivity::class.java))
        }
        cardPolitics.setOnClickListener {
            startActivity(Intent(this,PoliticsActivity::class.java))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home ->
                drawer.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        MaterialAlertDialogBuilder(this).setTitle("Do you want to quit app?")
            .setPositiveButton("Yes"){_,_-> super.onBackPressed() }
            .setNegativeButton("No"){_,_->}
            .show()
    }
}