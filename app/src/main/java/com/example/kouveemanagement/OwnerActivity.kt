package com.example.kouveemanagement

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.kouveemanagement.adapter.MenuRecyclerViewAdapter
import com.example.kouveemanagement.customer.CustomerManagementActivity
import com.example.kouveemanagement.employee.EmployeeManagementActivity
import com.example.kouveemanagement.model.Menu
import com.example.kouveemanagement.persistent.AppDatabase
import com.example.kouveemanagement.persistent.CurrentUser
import com.example.kouveemanagement.pet.PetManagementActivity
import com.example.kouveemanagement.product.ProductManagementActivity
import com.example.kouveemanagement.supplier.SupplierManagementActivity
import kotlinx.android.synthetic.main.activity_owner.*
import org.jetbrains.anko.startActivity

class OwnerActivity : AppCompatActivity() {

    private var menu: MutableList<Menu> = mutableListOf()

    companion object {
        var database: AppDatabase? = null
        var currentUser: CurrentUser? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        menuInitialization()

        database = Room.databaseBuilder(this, AppDatabase::class.java, "kouvee-db").build()

        setContentView(R.layout.activity_owner)
        setMenu()

        getCurrentUser()

        btn_logout.setOnClickListener {
            showLogoutConfirm()
        }
    }

    private fun menuInitialization(){
        val name = resources.getStringArray(R.array.owner_menu)
        val desc = resources.getStringArray(R.array.owner_desc)

        menu.clear()

        for (i in name.indices){
            menu.add(Menu(name[i], desc[i]))
        }
    }

    private fun setMenu(){
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = MenuRecyclerViewAdapter(menu) {
            when(it.name) {
                //DATA MASTER
                "Employee" -> startActivity<EmployeeManagementActivity>()
                "Customer" -> startActivity<CustomerManagementActivity>()
                "Pet Type and Size" -> startActivity<PetManagementActivity>()
                "Supplier" -> startActivity<SupplierManagementActivity>()
                "Product" -> startActivity<ProductManagementActivity>()
                "Service" -> startActivity<EmployeeManagementActivity>()
                "Customer Pet" -> startActivity<EmployeeManagementActivity>()
                //TRANSACTION
                "Product Order" -> startActivity<EmployeeManagementActivity>()
                "Product Transaction" -> startActivity<EmployeeManagementActivity>()
                "Service Transaction" -> startActivity<EmployeeManagementActivity>()
            }
            Toast.makeText(this, "Yeay!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCurrentUser(){
        val thread = Thread {
            currentUser = database?.currentUserDao()?.getCurrentuser()

            id.text = currentUser?.user_id
            name.text = currentUser?.user_name
            role.text = currentUser?.user_role
        }
        thread.start()
    }

    private fun showLogoutConfirm(){
        val confirm = AlertDialog.Builder(this)
            .setTitle("Confirmation")
            .setMessage("Are you sure to log out ?")

        confirm.setNegativeButton("NO") { _, _ ->
            Toast.makeText(this, "Stay here.", Toast.LENGTH_SHORT).show()
        }

        confirm.setPositiveButton("YES") { _, _ ->
            val thread = Thread {
                currentUser?.let { database?.currentUserDao()?.deleteCurrentUser(it) }
                database?.clearAllTables()
                startActivity<MainActivity>()
            }
            thread.start()
        }
        confirm.show()
    }

    override fun onBackPressed() {
        showLogoutConfirm()
    }


}
