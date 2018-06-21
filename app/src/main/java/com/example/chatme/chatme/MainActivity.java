package com.example.chatme.chatme;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private List<Messages> messages = new ArrayList<>();
    private ListView lvMessages;
    private EditText txtUserComment;
    private Button btnSend;
    private CommentList commentAdapter;
    private String UserName = "";
    Context appContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = FirebaseDatabase.getInstance();
        // Firebase Auth
        this.mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null)
        {
            UserName = currentUser.getEmail();
        }

//        Intent myIntent = getIntent();
//        UserName = myIntent.getStringExtra("username");
//        if(UserName != null)
//        {
//            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//            SharedPreferences.Editor editor = prefs.edit();
//            editor.putString("PREF_USER", UserName);
//            editor.commit();
//        }
//        else
//        {
//            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//            UserName = prefs.getString("PREF_USER", "");
//        }

        this.appContext = this;

        myRef = database.getReference("messages");
        lvMessages = (ListView) findViewById(R.id.lvMessages);
        txtUserComment = (EditText) findViewById(R.id.txtUserComment);
        btnSend = (Button) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = txtUserComment.getText().toString();
                if(!comment.trim().equals("")) {
                    String id = myRef.push().getKey();
                    Messages msg = new Messages(UserName, comment);
                    myRef.child(id).setValue(msg);
                    txtUserComment.setText("");
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.topbuttons, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(appContext, LoginActivity.class);
                startActivity(i);
                return true;
            case R.id.action_account:
                Intent ac = new Intent(appContext, AccountActivity.class);
                startActivity(ac);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //attaching value event listener
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //clearing the previous messages list
                messages.clear();
                String lastUserFetched = "";
                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting message
                    Messages message = postSnapshot.getValue(Messages.class);
                    //adding message to the list
                    lastUserFetched = message.getName();
                    messages.add(message);
                }

                //creating adapter
                commentAdapter = new CommentList(MainActivity.this, messages);
                //attaching adapter to the listview
                lvMessages.setAdapter(commentAdapter);
                lvMessages.post(new Runnable() {
                    @Override
                    public void run() {
                        // Select the last row so it will scroll into view...
                        lvMessages.setSelection(commentAdapter.getCount() - 1);
                    }
                });

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(appContext);
                mBuilder.setSmallIcon(R.drawable.chat);
                mBuilder.setContentTitle("Chat Me!");
                mBuilder.setContentText("New message from "+lastUserFetched);

                Intent notificationIntent = new Intent(appContext, MainActivity.class);
                PendingIntent contentIntent = PendingIntent.getActivity(appContext, 0, notificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(contentIntent);

                // Add as notification
                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                manager.notify(0, mBuilder.build());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseAuth.getInstance().signOut();
    }
}
