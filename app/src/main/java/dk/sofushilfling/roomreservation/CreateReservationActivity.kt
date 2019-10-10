package dk.sofushilfling.roomreservation

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.TimePicker
import android.widget.Toast
import android.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_create_reservation.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.math.BigInteger
import java.security.MessageDigest
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

class CreateReservationActivity : Activity() {

    private lateinit var todaysReservations: ArrayList<Reservation>
    private lateinit var selectedDate: LocalDate
    private var roomId: Int = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_reservation)
        setActionBar(toolbar_create_reservation as Toolbar)
        actionBar?.setDisplayHomeAsUpEnabled(true)


        todaysReservations = intent.getParcelableArrayListExtra<Reservation>("reservations_today")
        selectedDate = intent.getSerializableExtra("selectedDate") as LocalDate
        roomId = intent.getIntExtra("roomId", -1)
        if(roomId == -1)
            Log.e("TAG", "Error!!! no roomId was sent to CreateReservationActivity from SpecificRoomActivity")

        time_picker_from.setIs24HourView(true)
        time_picker_to.setIs24HourView(true)

        val timeListener = TimePicker.OnTimeChangedListener {_, _, _ -> reservationDataChanged()}
        time_picker_from.setOnTimeChangedListener(timeListener)
        time_picker_to.setOnTimeChangedListener(timeListener)

        reservation_purpose.addTextChangedListener(object:TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                reservationDataChanged()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        })



        create_button.setOnClickListener {
            if(isRoomAvailableAtSpecifiedTime())
                postReservation()
            else
                Toast.makeText(this, "Room is already reserved at given time!", Toast.LENGTH_SHORT).show()

        }

        reservationDataChanged()
    }

    private fun reservationDataChanged(){
        create_button.isEnabled = isPurposeValid() && isTimeValid()
    }

    private fun isPurposeValid(): Boolean{
        return reservation_purpose.text.toString().trim().length >= 5
    }

    private fun isTimeValid(): Boolean{
        val fromTime = time_picker_from.hour * 60 + time_picker_from.minute
        val toTime = time_picker_to.hour * 60 + time_picker_to.minute
        return  fromTime < toTime

    }

    private fun postReservation(){

        val url = "http://anbo-roomreservationv3.azurewebsites.net/api/Reservations"

        val fromTime = getTimeInSeconds(time_picker_from.hour, time_picker_from.minute)
        val toTime = getTimeInSeconds(time_picker_to.hour, time_picker_to.minute)
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val purpose = reservation_purpose.text.toString()
        val newRes = Reservation(fromTime, toTime, userId, purpose, roomId)
        val jsonReservation: String = Gson().toJson(newRes)

        Log.d("TAG", jsonReservation)

        val JSON = "application/json; charset=utf-8".toMediaType()
        val client = OkHttpClient()

        val requestBody = jsonReservation.toRequestBody(JSON)
        val request = Request.Builder().url(url).post(requestBody).build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                Log.i("TAG", response.toString())
                if(!response.isSuccessful){
                    runOnUiThread {
                        Toast.makeText(baseContext, response.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
        setResult(RESULT_OK)
        finish()
    }

    private fun getTimeInSeconds(hour: Int, minute: Int): Long{
        val calendar = GregorianCalendar()
        calendar.set(Calendar.YEAR, selectedDate.year)
        calendar.set(Calendar.MONTH, selectedDate.monthValue - 1)
        calendar.set(Calendar.DAY_OF_MONTH, selectedDate.dayOfMonth)

        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        return calendar.timeInMillis / 1000
    }


    private fun isRoomAvailableAtSpecifiedTime(): Boolean{
        val fromTimeSec = getTimeInSeconds(time_picker_from.hour, time_picker_from.minute)
        val toTimeSec = getTimeInSeconds(time_picker_to.hour, time_picker_to.minute)
        for (reservation in todaysReservations){
            if(fromTimeSec > reservation.fromTime && fromTimeSec < reservation.toTime ||
               toTimeSec > reservation.fromTime && toTimeSec < reservation.toTime ||
               fromTimeSec < reservation.fromTime && toTimeSec > reservation.toTime)
            {
                return false
            }
        }
        return true
    }
}
