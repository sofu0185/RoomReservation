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
import android.widget.Toolbar
import kotlinx.android.synthetic.main.activity_create_reservation.*

class CreateReservationActivity : Activity() {

    private lateinit var todaysReservations: ArrayList<Reservation>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_reservation)
        setActionBar(toolbar_create_reservation as Toolbar)
        actionBar?.setDisplayHomeAsUpEnabled(true)

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
                val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
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

    private fun isRoomAvalibleAtSpecifiedTime(): Boolean{
        val fromTimeSec = (time_picker_from.hour * 60 + time_picker_from.minute) * 60
        val toTimeSec = (time_picker_to.hour * 60 + time_picker_to.minute) * 60
        for (reservation in todaysReservations){
            //if(reservation.fromTime >)
        }
        return true
    }
}
