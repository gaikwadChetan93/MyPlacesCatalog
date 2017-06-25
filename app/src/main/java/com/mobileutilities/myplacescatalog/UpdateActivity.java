package com.mobileutilities.myplacescatalog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import bean.LocationDTO;
import database.LocationOperation;

public class UpdateActivity extends AppCompatActivity {

    private LocationOperation locationOperation;
    private LocationDTO locationDTO;
    private EditText mLocationName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        locationDTO = (LocationDTO) getIntent().getSerializableExtra("data");
        ((TextView)findViewById(R.id.location)).setText("Latitude : "+locationDTO.getmLat()
        +"\nLongitude : "+locationDTO.getmLong());
        ((TextView)findViewById(R.id.location_address)).setText(locationDTO.getAddress());
        mLocationName = (EditText)findViewById(R.id.edt_location_name);
        mLocationName.setText(locationDTO.getmLocationName());
    }

    public void deleteLocation(View view) {
        locationOperation = new LocationOperation(UpdateActivity.this);
        locationOperation.open();
        int result = locationOperation.deleteLocation(locationDTO.getmLocationID());
        locationOperation.close();
        if(result > 0){
            Toast.makeText(UpdateActivity.this,"location deleted",Toast.LENGTH_SHORT).show();
            finish();
        }else {
            Toast.makeText(UpdateActivity.this,"cannot delete location",Toast.LENGTH_SHORT).show();
        }
    }

    public void updateLocation(View view) {
        locationOperation = new LocationOperation(UpdateActivity.this);
        locationOperation.open();
        int result = locationOperation.update(mLocationName.getText().toString(),locationDTO);
        locationOperation.close();
        if(result > 0){
            Toast.makeText(UpdateActivity.this,"location updated",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(UpdateActivity.this,"cannot update location",Toast.LENGTH_SHORT).show();
        }
    }
}
