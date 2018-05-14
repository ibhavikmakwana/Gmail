package com.ibhavikmakwana.gmail.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.ActionMode
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.ibhavikmakwana.gmail.R
import com.ibhavikmakwana.gmail.adapter.MessagesAdapter
import com.ibhavikmakwana.gmail.model.Message
import com.ibhavikmakwana.gmail.network.ApiClient
import com.ibhavikmakwana.gmail.network.ApiInterface
import com.ibhavikmakwana.gmail.utils.DividerItemDecoration
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener, MessagesAdapter.MessageAdapterListener {

    private val messages: MutableList<Message> = ArrayList()
    private lateinit var mAdapter: MessagesAdapter
    private var actionModeCallback: ActionModeCallback? = null
    private var actionMode: ActionMode? = null

    companion object {
        /**
         * call this method to launch the Main Activity
         */
        fun launchActivity(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        mAdapter = MessagesAdapter(this, messages, this)

        val mLayoutManager = LinearLayoutManager(applicationContext)
        recycler_view.layoutManager = mLayoutManager
        recycler_view.itemAnimator = DefaultItemAnimator()
        recycler_view.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        recycler_view.adapter = mAdapter

        actionModeCallback = ActionModeCallback()

        fab.setOnClickListener {
            //            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
            ComposeActivity.launchActivity(this@MainActivity, it)
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        swipe_refresh_layout.setOnRefreshListener(this)

        // show loader and fetch messages
        swipe_refresh_layout.post({ getInbox() })
    }

    /**
     * Fetches mail messages by making HTTP request
     * url: https://api.androidhive.info/json/inbox.json
     */
    private fun getInbox() {
        swipe_refresh_layout.isRefreshing = true

        val apiService = ApiClient.client!!.create(ApiInterface::class.java)

        val call = apiService.inbox
        call.enqueue(object : Callback<List<Message>> {
            override fun onResponse(call: Call<List<Message>>, response: Response<List<Message>>) {
                // clear the inbox
                messages.clear()
                // add all the messages
                // messages.addAll(response.body());
                // TODO - avoid looping
                // the loop was performed to add colors to each message
                for (message in response.body()!!) {
                    // generate a random color
                    message.color = getRandomMaterialColor("400")
                    messages.add(message)
                }
                mAdapter.notifyDataSetChanged()
                swipe_refresh_layout.isRefreshing = false
            }

            override fun onFailure(call: Call<List<Message>>, t: Throwable) {
                Toast.makeText(applicationContext, "Unable to fetch json: " + t.message, Toast.LENGTH_LONG).show()
                swipe_refresh_layout.isRefreshing = false
            }
        })
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_search -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_inbox -> {
                // Handle the camera action
            }
            R.id.nav_starred -> {

            }
            R.id.nav_sent -> {

            }
            R.id.nav_calendar -> {

            }
            R.id.nav_contacts -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    /**
     * chooses a random color from array.xml
     */
    private fun getRandomMaterialColor(typeColor: String): Int {
        var returnColor = Color.GRAY
        val arrayId = resources.getIdentifier("mdcolor_$typeColor", "array", packageName)

        if (arrayId != 0) {
            val colors = resources.obtainTypedArray(arrayId)
            val index = (Math.random() * colors.length()).toInt()
            returnColor = colors.getColor(index, Color.GRAY)
            colors.recycle()
        }
        return returnColor
    }

    /**
     * list item icon click listeners
     */
    override fun onIconClicked(position: Int) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(this.actionModeCallback!!)
        }
        toggleSelection(position)
    }

    /**
     * list item important icon click listeners
     */
    override fun onIconImportantClicked(position: Int) {
        // Star icon is clicked,
        // mark the message as important
        val message = messages[position]
        message.isImportant = !message.isImportant
        messages[position] = message
        mAdapter.notifyDataSetChanged()
    }

    /**
     * list item row click listeners
     */
    override fun onMessageRowClicked(position: Int) {
        // verify whether action mode is enabled or not
        // if enabled, change the row state to activated
        if (mAdapter.selectedItemCount > 0) {
            enableActionMode(position)
        } else {
            // read the message which removes bold from the row
            val message = messages[position]
            message.isRead = true
            messages[position] = message
            mAdapter.notifyDataSetChanged()

//            Toast.makeText(applicationContext, "Read: " + message.message, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * list item row long click listeners
     */
    override fun onRowLongClicked(position: Int) {
        // long press is performed, enable action mode
        enableActionMode(position)
    }

    /**
     * Swipe refresh callback
     */
    override fun onRefresh() {
        getInbox()
    }

    private inner class ActionModeCallback : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            mode.menuInflater.inflate(R.menu.menu_action_mode, menu)
            // disable swipe refresh if action mode is enabled
            swipe_refresh_layout.isEnabled = false
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.action_delete -> {
                    // delete all the selected messages
                    deleteMessages()
                    mode.finish()
                    true
                }

                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            mAdapter.clearSelections()
            swipe_refresh_layout.isEnabled = true
            actionMode = null
            recycler_view.post({
                mAdapter.resetAnimationIndex()
                // mAdapter.notifyDataSetChanged();
            })
        }

        // deleting the messages from recycler view
        private fun deleteMessages() {
            mAdapter.resetAnimationIndex()
            val selectedItemPositions = mAdapter.getSelectedItems()
            for (i in selectedItemPositions.indices.reversed()) {
                mAdapter.removeData(selectedItemPositions[i])
            }
            mAdapter.notifyDataSetChanged()
        }
    }

    private fun toggleSelection(position: Int) {
        mAdapter.toggleSelection(position)
        val count = mAdapter.selectedItemCount

        if (count == 0) {
            actionMode?.finish()
        } else {
            actionMode?.title = count.toString()
            actionMode?.invalidate()
        }
    }

    private fun enableActionMode(position: Int) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(this.actionModeCallback!!)
        }
        toggleSelection(position)
    }
}