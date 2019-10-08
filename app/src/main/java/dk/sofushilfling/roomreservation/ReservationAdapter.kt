package dk.sofushilfling.roomreservation

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class ReservationAdapter(val myDataset: ArrayList<Reservation>) :
        RecyclerView.Adapter<ReservationAdapter.MyViewHolder>() {
    class MyViewHolder(val textView: TextView): RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val textView = LayoutInflater.from(parent.context).inflate(R.layout.my_text_view, parent, false) as TextView
        textView.setTextColor(ContextCompat.getColor(parent.context, android.R.color.black))
        return MyViewHolder(textView)
    }

    override fun getItemCount(): Int = myDataset.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val myData = myDataset[position]
        holder.textView.text = myData.toString()

        val borderId: Int
        if(FirebaseAuth.getInstance().currentUser != null && myData.userId != null && myData.userId == FirebaseAuth.getInstance().currentUser!!.uid){
            borderId = R.drawable.own_reservations_border
        }
        else{
            borderId = R.drawable.reservations_border
        }

        holder.textView.background =  ContextCompat.getDrawable(holder.textView.context, borderId)
    }
}