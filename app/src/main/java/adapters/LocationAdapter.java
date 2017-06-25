package adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobileutilities.myplacescatalog.R;
import com.mobileutilities.myplacescatalog.UpdateActivity;

import java.util.List;
import java.util.Locale;

import bean.LocationDTO;
import database.LocationOperation;

public class LocationAdapter extends BaseAdapter{
    Context context;
      private static LayoutInflater inflater=null;
    List<LocationDTO> locationDTOs;
    public LocationAdapter(Activity mainActivity, List<LocationDTO> locationDTOs) {
        context=mainActivity;
        this.locationDTOs = locationDTOs;
         inflater = ( LayoutInflater )context.
                 getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return locationDTOs.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        TextView locationName;
        ImageView locationImg;
        TextView navigate;
        LinearLayout delete;
        LinearLayout rootLayout;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;       
             rowView = inflater.inflate(R.layout.row, null);
             holder.locationName =(TextView) rowView.findViewById(R.id.location_name);
             holder.locationImg =(ImageView) rowView.findViewById(R.id.location_image);
            holder.navigate =(TextView) rowView.findViewById(R.id.navigate);
        holder.delete =(LinearLayout) rowView.findViewById(R.id.location_delete);
        holder.rootLayout =(LinearLayout) rowView.findViewById(R.id.root);
        holder.locationName.setText(locationDTOs.get(position).getmLocationName());
         //holder.locationImg.setImageResource(locationDTOs.get(position).getmLocationImage());
        holder.locationImg.setImageResource(R.drawable.home);
        holder.navigate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)",
                        locationDTOs.get(position).getmLat(),
                        locationDTOs.get(position).getmLong(),
                        locationDTOs.get(position).getmLocationName());
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                context.startActivity(intent);
            }
        });
        holder.delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationOperation locationOperation = new LocationOperation(context);
                locationOperation.open();
                locationOperation.deleteLocation(locationDTOs.get(position).getmLocationID());
                locationOperation.close();
                locationDTOs.remove(position);
                notifyDataSetChanged();


            }
        });
        holder.rootLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UpdateActivity.class);
                intent.putExtra("data",locationDTOs.get(position));
                context.startActivity(intent);
            }
        });
        return rowView;
    }

} 