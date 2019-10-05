package dk.sofushilfling.roomreservation

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.BoringLayout
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
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

class CreateReservationActivity : Activity() {

    private lateinit var todaysReservations: ArrayList<Reservation>
    private var roomId: Int = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_reservation)
        setActionBar(toolbar_create_reservation as Toolbar)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        roomId = intent.getIntExtra("roomId", -1)
        if(roomId == -1)
            Log.e("TAG", "Error!!! no roomId was sent to CreateReservationActivity from SpecificRoomActivity")

        time_picker_from.setIs24HourView(true)
        time_picker_to.setIs24HourView(true)

        val timeListener = TimePicker.OnTimeChangedListener(function = {_, _, _ -> reservationDataChanged()})
        time_picker_from.setOnTimeChangedListener(timeListener)
        time_picker_to.setOnTimeChangedListener(timeListener)

        reservation_purpose.addTextChangedListener(object:TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                reservationDataChanged()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        })

        todaysReservations = intent.getParcelableArrayListExtra<Reservation>("reservations_today")

        create_button.setOnClickListener {
            if(isRoomAvalibleAtSpecifiedTime())
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
        val fromTime = time_picker_from.hour * 60 + time_picker_from.minute
        val toTime = time_picker_to.hour * 60 + time_picker_to.minute
        return  fromTime < toTime
    }

    private fun isTimeValid(): Boolean{
        return reservation_purpose.text.toString().trim().length >= 5
    }

    private fun postReservation(){

        val url = "";
        val fromTimeSec: Long = ((time_picker_from.hour * 60 + time_picker_from.minute) * 60).toLong()
        val toTimeSec: Long = ((time_picker_to.hour * 60 + time_picker_to.minute) * 60).toLong()
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val purpose = reservation_purpose.text.toString()
        val newRes: Reservation = Reservation(fromTimeSec, toTimeSec, userId, purpose, roomId)
        val jsonReservation: String = Gson().toJson(newRes)

        val JSON = "application/json; charset=utf-8".toMediaType()
        val client = OkHttpClient()

        val requestBody = jsonReservation.toRequestBody(JSON)
        val request = Request.Builder().url(url).post(requestBody).build()

        client.newCall(request).enqueue(object:Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if(!response.isSuccessful){

                }
            }
        })

    }

    private fun isRoomAvalibleAtSpecifiedTime(): Boolean{
        val fromTimeSec: Long = ((time_picker_from.hour * 60 + time_picker_from.minute) * 60).toLong()
        val toTimeSec: Long = ((time_picker_to.hour * 60 + time_picker_to.minute) * 60).toLong()
        for (reservation in todaysReservations){
            if(fromTimeSec < reservation.fromTime && toTimeSec < reservation.toTime ||
               reservation.toTime < fromTimeSec && reservation.toTime < toTimeSec)
            {
                return true
            }
        }
        return false
    }
}
