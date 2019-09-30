package com.taxi.nanny.views.login_section.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.taxi.nanny.R;
import com.taxi.nanny.model.SelectVehicleTypeBeans;
import com.taxi.nanny.model.UploadDocumentBeans;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UploadDocumentAdapter extends RecyclerView.Adapter<UploadDocumentAdapter.UploadDocumentHolder> {


    ArrayList<UploadDocumentBeans> title ;
    Activity activity;
    String type;

    myClickListener myClickListener;

    Context context;


    public static int selectedType=0;
    public UploadDocumentAdapter(ArrayList<UploadDocumentBeans> title, Activity activity,String type,Context context){
       this. title=title;
        this.activity=activity;
        this.type=type;
        this.context=context;
    }



    @NonNull
    @Override
    public UploadDocumentHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.upload_document_view,viewGroup,
                false);
        return new UploadDocumentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UploadDocumentHolder uploadDocumentHolder, int i)
    {
        uploadDocumentHolder.txt_title.setText("Step "+(i+1)+": "+title.get(i).getTitle());
              uploadDocumentHolder.recycler_view.setVisibility(View.VISIBLE);
          /*      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    uploadDocumentHolder.txt_title.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,uploadDocumentHolder.drawableDropDownClose,null);
                }*/
        uploadDocumentHolder.recycler_view.setAdapter(new UploadTypeDocumentAdapter(title.get(i).getUploadDocumentTypes(),context));

        if (type.equalsIgnoreCase("1"))
        {
            uploadDocumentHolder.txt_title.setTextColor(activity.getResources().getColor(R.color.black));
        }
        else
        {
            uploadDocumentHolder.txt_title.setTextColor(activity.getResources().getColor(R.color.colorGreen));
        }

    /*
        uploadDocumentHolder.txt_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                selectedType=i+1;
                Log.e("AdapterPosition ,",i+"");
                Log.e("selectedType ,",selectedType+"");


                if(uploadDocumentHolder.recycler_view.getVisibility()==View.GONE) {
                    uploadDocumentHolder.recycler_view.setVisibility(View.VISIBLE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        uploadDocumentHolder.txt_title.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,uploadDocumentHolder.drawableDropDownClose,null);
                    }
                }else{
                    uploadDocumentHolder.recycler_view.setVisibility(View.GONE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        uploadDocumentHolder.txt_title.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,uploadDocumentHolder.drawableDropDownOpen,null);
                    }
                }
            }
        });*/



    }

    @Override
    public int getItemCount()
    {
        return title.size();
    }

    class UploadDocumentHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.txt_title)
        TextView txt_title;
        @BindView(R.id.recycler_view)
        RecyclerView recycler_view;
        Drawable drawableDropDownClose;
        Drawable drawableDropDownOpen;
        public UploadDocumentHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            recycler_view.setLayoutManager(new LinearLayoutManager(itemView.getContext(),LinearLayoutManager.VERTICAL,false));
            drawableDropDownClose = activity.getResources().getDrawable(R.drawable.drop_down_close);
            drawableDropDownOpen= activity.getResources().getDrawable(R.drawable.drop_down);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myClickListener.onItemCLick(getAdapterPosition(),v);
                }
            });

        }
    }

    public interface myClickListener{
        public void onItemCLick(int layoutPosition,View view);
    }

    public void onItemSelectedListener( myClickListener listener)
    {
        myClickListener=listener;
    }

}
