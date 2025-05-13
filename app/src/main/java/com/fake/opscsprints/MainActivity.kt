package com.fake.opscsprints

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.fake.opscsprints.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Initialize UI components
        setupToolbar()
        setupDrawer()
        setupCalculatorViews()
        setupOperationButtons()
        setupBottomNavigation()
        setupFloatingActionButton()
        
        // Simple placeholder toast to show the app is working
        Toast.makeText(this, "App started successfully", Toast.LENGTH_LONG).show()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    
    private fun setupDrawer() {
        toggle = ActionBarDrawerToggle(
            this, 
            binding.drawerLayout, 
            R.string.navigation_drawer_open, 
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_profile -> {
                    Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show()
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_logout -> {
                    Toast.makeText(this, "Logout clicked", Toast.LENGTH_SHORT).show()
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                else -> false
            }
        }
    }
    
    private fun setupCalculatorViews() {
        // No need to findViewById with ViewBinding
    }
    
    private fun setupOperationButtons() {
        binding.addButton.setOnClickListener { performOperation("+") }
        binding.subtractButton.setOnClickListener { performOperation("-") }
        binding.multiplyButton.setOnClickListener { performOperation("×") }
        binding.divideButton.setOnClickListener { performOperation("÷") }
    }
    
    private fun performOperation(operator: String) {
        try {
            val num1 = binding.editTextText2.text.toString().toDouble()
            val num2 = binding.editTextText3.text.toString().toDouble()
            val result = when (operator) {
                "+" -> num1 + num2
                "-" -> num1 - num2
                "×" -> num1 * num2
                "÷" -> {
                    if (num2 == 0.0) {
                        Toast.makeText(this, "Cannot divide by zero", Toast.LENGTH_SHORT).show()
                        return
                    }
                    num1 / num2
                }
                else -> 0.0
            }
            
            binding.resultTextView.text = result.toString()
            binding.historyTextView.text = "Last operation: $num1 $operator $num2 = $result"
        } catch (e: Exception) {
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun setupBottomNavigation() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            var fragment: Fragment? = null
            
            when (item.itemId) {
                R.id.nav_feed -> {
                    fragment = FeedFragment()
                    supportActionBar?.title = "Feed"
                }
                R.id.nav_search -> {
                    fragment = SearchFragment()
                    supportActionBar?.title = "Search"
                }
                R.id.nav_profile -> {
                    fragment = ProfileFragment()
                    supportActionBar?.title = "Profile"
                }
            }
            
            if (fragment != null) {
                supportFragmentManager.beginTransaction()
                    .replace(binding.fragmentContainer.id, fragment)
                    .commit()
                return@setOnItemSelectedListener true
            }
            
            false
        }
        
        // Set default fragment
        binding.bottomNav.selectedItemId = R.id.nav_feed
    }
    
    private fun setupFloatingActionButton() {
        binding.fab.setOnClickListener {
            startActivity(Intent(this, PostActivity::class.java))
        }
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}