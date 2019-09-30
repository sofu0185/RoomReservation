package dk.sofushilfling.roomreservation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class UserActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        final TextView userEmailTextView = findViewById(R.id.user_email);
        final Button signoutButton = findViewById(R.id.sign_out_button);

        userEmailTextView.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        signoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                FirebaseAuth.getInstance().signOut();
                finish();
            }
        });
    }
}
