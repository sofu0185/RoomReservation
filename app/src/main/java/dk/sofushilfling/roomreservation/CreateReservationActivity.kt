package dk.sofushilfling.roomreservation

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toolbar
import kotlinx.android.synthetic.main.activity_create_reservation.*

class CreateReservationActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_reservation)
        setActionBar(toolbar as Toolbar)
        time_picker_from.setIs24HourView(true)
        time_picker_to.setIs24HourView(true)
        time_picker_to
    }
}
