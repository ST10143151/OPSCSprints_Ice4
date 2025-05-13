package com.fake.loginregistration

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var bottomNav: BottomNavigationView

    // LoginRegistration UI variables
    private lateinit var num1EditText: EditText
    private lateinit var num2EditText: EditText
    private lateinit var resultTextView: TextView
    private lateinit var operatorTextView: TextView
    private lateinit var historyTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set up Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        bottomNav = findViewById(R.id.bottomNav)

        // Set up the hamburger toggle for the drawer
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,  // defined in strings.xml
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Update header (if you have one) with the current user's info
        val headerView = navView.getHeaderView(0)
        val tvWelcome = headerView.findViewById<TextView>(R.id.tvWelcome)
        val currentUser = FirebaseAuth.getInstance().currentUser
        tvWelcome.text = if (currentUser != null) {
            "Welcome, ${currentUser.displayName ?: currentUser.email}"
        } else {
            "Welcome, Guest"
        }

        // Update menu items based on login state
        val menu = navView.menu
        if (currentUser != null) {
            menu.findItem(R.id.nav_login).isVisible = false
            menu.findItem(R.id.nav_register).isVisible = false
            menu.findItem(R.id.nav_profile).isVisible = true
        } else {
            menu.findItem(R.id.nav_login).isVisible = true
            menu.findItem(R.id.nav_register).isVisible = true
            menu.findItem(R.id.nav_profile).isVisible = false
        }

        // Handle navigation menu item clicks
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_login -> startActivity(Intent(this, LoginActivity::class.java))
                R.id.nav_register -> startActivity(Intent(this, RegistrationActivity::class.java))
                R.id.nav_profile -> startActivity(Intent(this, ProfileActivity::class.java))
                R.id.nav_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    // Reset to logged-out state and navigate to LoginActivity
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }


            }
            drawerLayout.closeDrawers()
            true
        }







        // Initialize LoginRegistration UI views
        num1EditText = findViewById(R.id.editTextText2)
        num2EditText = findViewById(R.id.editTextText3)
        resultTextView = findViewById(R.id.resultTextView)
        operatorTextView = findViewById(R.id.operatorTextView)
        historyTextView = findViewById(R.id.historyTextView)

        setupOperationButton(R.id.addButton, "+", ::add)
        setupOperationButton(R.id.subtractButton, "-", ::subtract)
        setupOperationButton(R.id.multiplyButton, "×", ::multiply)
        setupOperationButton(R.id.divideButton, "÷", ::divide)

        // Apply system window insets if needed
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            startActivity(Intent(this, PostActivity::class.java))
        }
        
        // Set up bottom navigation
        bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_feed -> loadFragment(FeedFragment())
                R.id.nav_search -> loadFragment(SearchFragment())
                R.id.nav_profile -> loadFragment(ProfileFragment())
                else -> false
            }
            true
        }
        
        // Set default fragment
        loadFragment(FeedFragment())

    }

    private fun setupOperationButton(buttonId: Int, operator: String, operation: (Double, Double) -> Double) {
        findViewById<Button>(buttonId).setOnClickListener {
            operatorTextView.text = operator
            calculateResult(operation, operator)
        }
    }

    private fun calculateResult(operation: (Double, Double) -> Double, operator: String) {
        val num1 = num1EditText.text.toString().toDoubleOrNull()
        val num2 = num2EditText.text.toString().toDoubleOrNull()

        if (num1 != null && num2 != null) {
            try {
                val result = operation(num1, num2)
                val formattedResult = if (result % 1 == 0.0) result.toInt().toString() else "%.2f".format(result)
                resultTextView.text = formattedResult
                updateHistory(num1, num2, operator, formattedResult)
            } catch (e: Exception) {
                resultTextView.text = "Error"
                historyTextView.text = "Error: ${e.message}"
            }
        } else {
            resultTextView.text = "Invalid input"
            historyTextView.text = "Please enter valid numbers"
        }
    }

    private fun updateHistory(num1: Double, num2: Double, operator: String, result: String) {
        historyTextView.text = "Last operation: $num1 $operator $num2 = $result"
    }

    private fun add(a: Double, b: Double) = a + b
    private fun subtract(a: Double, b: Double) = a - b
    private fun multiply(a: Double, b: Double) = a * b
    private fun divide(a: Double, b: Double): Double {
        if (b == 0.0) throw IllegalArgumentException("Cannot divide by zero")
        return a / b
    }
    
    private fun loadFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
        return true
    }
}
