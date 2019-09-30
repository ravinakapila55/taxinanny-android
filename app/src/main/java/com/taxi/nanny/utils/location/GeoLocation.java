package com.taxi.nanny.utils.location;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.widget.Toast;
import com.taxi.nanny.R;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GeoLocation
{
    public static String getAddress(Context context, double lat, double lng)
    {
        Geocoder geocoder;
        String address="";
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat,lng, 1);
            if (addresses.size()>0)
            {
                address = addresses.get(0).getAddressLine(0);
            }
            else
            {
                address="";
                Toast.makeText(context, R.string.location_not_found, Toast.LENGTH_SHORT).show();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }


}
