package com.example.lenovo.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.example.lenovo.myapplication.adapter.RequestAdapter;
import com.example.lenovo.myapplication.controller.Repository;
import com.example.lenovo.myapplication.model.Request;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.security.ProviderInstaller;

public class MainActivity extends AppCompatActivity {

    private ListView requestsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        requestsListView = (ListView)findViewById(R.id.request_list_view);
        final Repository repository = new Repository();
        RequestAdapter adapter = new RequestAdapter(this, repository.getRequests());
        requestsListView.setAdapter(adapter);

        final Context context = this;
        requestsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Request selectedRequest= repository.getRequests().get(position);

                Intent detailIntent = new Intent(context, RequestDetail.class);
                detailIntent.putExtra("type", selectedRequest.getType());
                detailIntent.putExtra("requestedFor", selectedRequest.getRequestedFor());
                detailIntent.putExtra("requestedFrom", selectedRequest.getRequestedFrom());
                detailIntent.putExtra("status", selectedRequest.getStatus());
                startActivityForResult(detailIntent, 0);
            }
        });
    }

    public void sendMail(View view){
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto","ioana_bumbar@yahoo.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println(requestCode + " " + resultCode);
        switch(requestCode) {
            case (0) : {
                if (resultCode == Activity.RESULT_OK) {
                    String name = data.getStringExtra("name");
                    String status = data.getStringExtra("message");
                    System.out.println(name + ": " + status);
                    Repository repository = new Repository();
                    repository.setStatus(status, name);
                    System.out.println(repository.getRequests().get(1).getType() + ", " + repository.getRequests().get(1).getRequestedFor() + ", " + repository.getRequests().get(1).getStatus());
                    RequestAdapter adapter = new RequestAdapter(this, repository.getRequests());
                    requestsListView.setAdapter(adapter);
                }
                break;
            }
        }
    }

    private void updateAndroidSecurityProvider(Activity callingActivity) {
        try {
            ProviderInstaller.installIfNeeded(this);
        } catch (GooglePlayServicesRepairableException e) {
            // Thrown when Google Play Services is not installed, up-to-date, or enabled
            // Show dialog to allow users to install, update, or otherwise enable Google Play services.
            GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), callingActivity, 0);
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e("SecurityException", "Google Play Services not available.");
        }
    }
}
