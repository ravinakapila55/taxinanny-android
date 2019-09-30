package com.taxi.nanny.utils.autoComplete;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import java.util.ArrayList;

public class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable
{
    public ArrayList<String> resultList;
    Context mContext;
    int mResource;

    public PlaceApi mPlaceAPI = new PlaceApi();

    public PlacesAutoCompleteAdapter(Context context, int resource)
    {
        super(context, resource);
        mContext = context;
        mResource = resource;
    }

    @Override
    public int getCount()
    {
        return resultList.size();
    }

    @Override
    public String getItem(int position)
    {
        return resultList.get(position);
    }

    @Override
    public Filter getFilter()
    {
        Filter filter = new Filter()
        {
            @Override
            protected FilterResults performFiltering(CharSequence constraint)
            {
                FilterResults filterResults = new FilterResults();
                if (constraint != null)
                {
                    resultList = mPlaceAPI.autocomplete(constraint.toString());
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results)
            {
                if (results != null && results.count > 0)
                {
                    notifyDataSetChanged();
                }
                else
                {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

}
