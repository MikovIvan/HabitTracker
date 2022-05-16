package ru.mikov.habittracker.ui.main

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import ru.mikov.habittracker.R
import ru.mikov.habittracker.data.local.PrefManager
import ru.mikov.habittracker.databinding.ActivityMainBinding
import ru.mikov.habittracker.ui.base.Loading
import ru.mikov.habittracker.ui.base.Notify
import ru.mikov.habittracker.ui.extentions.gone
import ru.mikov.habittracker.ui.extentions.visible


class MainActivity : AppCompatActivity() {
    private val viewBinding: ActivityMainBinding by viewBinding()
    private val viewModel: MainViewModel by viewModels()
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(viewBinding.toolbar)

        initNavigation()
        initAvatar()
        observeViewModelData()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun renderLoading(loadingState: Loading) {
        with(viewBinding) {
            when (loadingState) {
                Loading.SHOW_LOADING -> {
                    progress.isVisible = true
                    window.setFlags(
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    )
                }
                Loading.HIDE_LOADING -> {
                    progress.isVisible = false
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }
            }
        }
    }

    fun renderNotification(notify: Notify) {
        val snackbar = Snackbar.make(viewBinding.drawerLayout, notify.message, Snackbar.LENGTH_LONG)

        when (notify) {
            is Notify.TextMessage -> {
            }

            is Notify.ErrorMessage -> {
                with(snackbar) {
                    setBackgroundTint(getColor(R.color.design_default_color_error))
                    setTextColor(getColor(android.R.color.white))
                    setActionTextColor(getColor(android.R.color.white))
                    setAction(notify.errLabel) {
                        notify.errHandler?.invoke()
                    }

                }
            }
        }

        snackbar.show()
    }

    private fun observeViewModelData() {
        viewModel.observeLoading(this) { renderLoading(it) }
        viewModel.observeNotifications(this) { renderNotification(it) }
        viewModel.observeConnection(this) { renderConnection(it) }
    }

    private fun renderConnection(isConnected: Boolean) {
        with(viewBinding) {
            when (isConnected) {
                true -> {
                    viewModel.synchronizeWithNetwork()
                    navHostFragment.visible()
                    statusButton.gone()
                }
                false -> {
                    //через suspend из бд
                    if (!PrefManager.isDbEmpty()) {
                        navHostFragment.visible()
                        statusButton.gone()
                    }
                }
            }
        }
    }

    private fun initNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph, viewBinding.drawerLayout)

        viewBinding.navView.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun initAvatar() {
        val header: View = viewBinding.navView.getHeaderView(0)
        val iv = header.findViewById(R.id.iv_avatar) as ImageView

        Glide.with(this)
            .load("https://images.theconversation.com/files/443350/original/file-20220131-15-1ndq1m6.jpg?ixlib=rb-1.1.0&rect=0%2C0%2C3354%2C2464&q=45&auto=format&w=926&fit=clip")
            .apply(RequestOptions.circleCropTransform())
            .override(200)
            .placeholder(R.drawable.ic_avatar)
            .error(R.drawable.ic_avatar)
            .into(iv)
    }
}