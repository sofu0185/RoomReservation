package dk.sofushilfling.roomreservation;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toolbar;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends Activity
{
    ArrayList<Room> rooms = new ArrayList<>();
    ArrayAdapter<Room> roomArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolBar = findViewById(R.id.toolbar);
        setActionBar(toolBar);

        roomArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, rooms);
        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Log.d("TAG", "position:" + position + " " + rooms.get(position));
                Intent intent = new Intent(getBaseContext(), SpecificRoomActivity.class);
                Room room = (Room) parent.getItemAtPosition(position);
                intent.putExtra("ROOM", room);
                startActivity(intent);
            }
        };

        ListView listView = findViewById(R.id.roomListView);
        listView.setAdapter(roomArrayAdapter);
        listView.setOnItemClickListener(itemClickListener);
        getRooms();

    }

    private  void getRooms(){
        OkHttpClient client = new OkHttpClient();

        final Request request = new Request.Builder().url("http://anbo-roomreservationv3.azurewebsites.net/api/Rooms").build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e)
            {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException
            {
                if (!response.isSuccessful())
                    throw new IOException("Unexpected code " + response);
                else {
                    Gson gson = new Gson();
                    Room[] tempRooms = gson.fromJson(response.body().string(), Room[].class);
                    rooms.addAll(Arrays.asList(tempRooms));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run()
                        {
                            roomArrayAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }
}
