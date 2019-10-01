package dk.sofushilfling.roomreservation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.CalendarView
import android.widget.ListView
import android.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import kotlinx.android.synthetic.*

import kotlinx.android.synthetic.main.activity_specific_room.*
import okhttp3.*
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.reflect.typeOf

class SpecificRoomActivity : Activity() {
    private var reservations = ArrayList<Reservation>()
    private lateinit var reservationsAdapter: ArrayAdapter<Reservation>
    private lateinit var room: Room

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_specific_room)
        setActionBar(toolbar as Toolbar)

        room = intent.getSerializableExtra("ROOM") as Room
        title = room.name

        reservationsAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, reservations)
        val listView = listViewReservations as ListView
        listView.adapter = reservationsAdapter

        if(FirebaseAuth.getInstance().currentUser != null){
            fab.show()
            fab.setOnClickListener { view -> addNewReservation(view) }
        }
        else
            fab.hide()


        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth -> dateChanged(year, month, dayOfMonth) }
        val dateTime = java.time.LocalDate.now()
        dateChanged(dateTime.year, dateTime.monthValue - 1, dateTime.dayOfMonth)
    }

    private fun addNewReservation(view: View){
        val intent = Intent(this, CreateReservationActivity::class.java)
        startActivity(intent)
    }

    private fun dateChanged(year: Int, month: Int, dayOfMonth: Int){
        val cal = GregorianCalendar()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month)
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        cal.set(Calendar.MILLISECOND, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        val fromTime = cal.time
        cal.add(Calendar.DAY_OF_YEAR, 1)
        val toTime = cal.time
        getReservationsFromDate(room.id, fromTime.time / 1000, toTime.time / 1000)
    }

    private fun getReservationsFromDate(roomId: Int, fromTime: Long, toTime: Long) {
        val url = "http://anbo-roomreservationv3.azurewebsites.net/api/Reservations/room/$roomId/$fromTime/$toTime"
        Log.d("TAG", url)
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

                    runOnUiThread { reservationsAdapter.notifyDataSetChanged()}
                }
            }
        })

    }

}
