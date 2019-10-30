package dk.sofushilfling.roomreservation

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson

import kotlinx.android.synthetic.main.activity_specific_room.*
import okhttp3.*
import java.io.IOException
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

class SpecificRoomActivity : Activity() {
    private val CREATE_NEW_RESERVATION = 4001

    private var reservations = ArrayList<Reservation>()
    private lateinit var reservationsAdapter: ReservationAdapter
    private lateinit var room: Room
    private lateinit var selectedDate: LocalDate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_specific_room)
        setActionBar(toolbar_specific_room as Toolbar)

        room = intent.getSerializableExtra("ROOM") as Room
        title = room.name

        reservationsAdapter = ReservationAdapter(reservations)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewReservations).apply {
            setHasFixedSize(true)
            adapter = reservationsAdapter
        }
        recyclerView.layoutManager = LinearLayoutManager(this)

        val itemTouchHelper = ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            private val icon: Drawable? = ContextCompat.getDrawable(baseContext, R.drawable.ic_delete_sweep_black_24dp)
            private val background: ColorDrawable = ColorDrawable(ContextCompat.getColor(baseContext, android.R.color.holo_red_light))

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                deleteItem(viewHolder.adapterPosition)
            }

            override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                val swipeFlags: Int
                if(reservations[viewHolder.adapterPosition].userId == FirebaseAuth.getInstance().currentUser?.uid)
                    swipeFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                else
                    swipeFlags = 0
                return ItemTouchHelper.Callback.makeMovementFlags(0, swipeFlags)
            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)

        swipeContainer.setOnRefreshListener { dateChanged() }

        if(FirebaseAuth.getInstance().currentUser == null){
            fab.hide()
        }
        else{
            fab.show()
            fab.setOnClickListener { view ->  addNewReservation(view) }
        }

        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
            dateChanged()
        }

        selectedDate = LocalDate.now()
        calendarView.minDate = calendarView.date
        dateChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == CREATE_NEW_RESERVATION){
            dateChanged()
        }
    }

    private fun addNewReservation(view: View){
        val intent = Intent(this, CreateReservationActivity::class.java)
        intent.putParcelableArrayListExtra("reservations_today", reservations);
        intent.putExtra("roomId", room.id)
        intent.putExtra("selectedDate", selectedDate)
        startActivityForResult(intent, CREATE_NEW_RESERVATION)
    }

    private fun dateChanged(){
        Log.d("TAG", selectedDate.toString())
        val cal = GregorianCalendar()
        cal.set(Calendar.YEAR, selectedDate.year)
        cal.set(Calendar.MONTH, selectedDate.monthValue - 1)
        cal.set(Calendar.DAY_OF_MONTH, selectedDate.dayOfMonth)

        cal.set(Calendar.MILLISECOND, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        val fromTime = cal.time
        cal.add(Calendar.DAY_OF_YEAR, 1)
        val toTime = cal.time
        getReservationsFromDate(room.id, fromTime.time / 1000, toTime.time / 1000)
    }

    private fun deleteItem(position: Int) {
        val deletedReservation = reservations[position]
        reservations.removeAt(position)
        reservationsAdapter.notifyItemRemoved(position)

        val url = "http://anbo-roomreservationv3.azurewebsites.net/api/Reservations/" + deletedReservation.id
        val client = OkHttpClient()
        val request = Request.Builder().url(url).delete().build()

        client.newCall(request).enqueue(object:Callback {
            override fun onFailure(call: Call, e: IOException) { e.printStackTrace() }

            override fun onResponse(call: Call, response: Response) {

            }
        })
    }

    private fun getReservationsFromDate(roomId: Int, fromTime: Long, toTime: Long) {
        val url = "http://anbo-roomreservationv3.azurewebsites.net/api/Reservations/room/$roomId/$fromTime/$toTime"
        Log.d("TAG", "getting room $roomId reservations between $fromTime - $toTime")
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object:Callback {
            override fun onFailure(call: Call, e: IOException) { e.printStackTrace() }

            override fun onResponse(call: Call, response: Response) {
                if(!response.isSuccessful)
                    throw IOException("Unexpected code $response")
                else {
                    val gson = Gson()
                    val res = gson.fromJson(response.body?.string(), Array<Reservation>::class.java)
                    reservations.clear()
                    reservations.addAll(res)
                    reservations.sortBy { x -> x.fromTime }

                    runOnUiThread { reservationsAdapter.notifyDataSetChanged()}

                    swipeContainer.isRefreshing = false
                }
            }
        })
    }
}
