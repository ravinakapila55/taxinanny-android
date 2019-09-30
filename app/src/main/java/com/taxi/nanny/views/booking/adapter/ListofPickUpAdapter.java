package com.taxi.nanny.views.booking.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.taxi.nanny.R;
import com.taxi.nanny.model.RiderListModel;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ListofPickUpAdapter extends RecyclerView.Adapter<ListofPickUpAdapter.MyViewHolder>
{
    Context context;
    myClickListener listener;
    ArrayList<RiderListModel> list;
    ArrayList<String> priorityListPick=new ArrayList<>();
    ArrayList<String> priorityListDrop=new ArrayList<>();

    String pickValue;
    String dropValue;

    int valuesss;

    int itemPosition;

    public ListofPickUpAdapter(Context context,ArrayList<RiderListModel> listModels)
    {
        this.context = context;
        this.list=listModels;
        priorityListPick.clear();
        priorityListDrop.clear();

         pickValue="";
         dropValue="";

//         valuesss=list.size()+1;
         valuesss=list.size();

        if (list.size()==1)
        {
            pickValue="0";
            dropValue="0";
//            priorityListPick.add(String.valueOf(0));
            priorityListPick.add(String.valueOf(1));
//            priorityListDrop.add(String.valueOf(0));
            priorityListDrop.add(String.valueOf(1));
        }
        else if (list.size()>1)
        {
            for (int i = 0; i <list.size() ; i++)
            {
                if (list.get(0).getPickup().equalsIgnoreCase(list.get(i).getPickup()))
                {
                    if (list.get(0).getPickup().equalsIgnoreCase(""))
                    {
                        pickValue="1";
                    }
                    else
                    {
                        pickValue="0";
                    }
                }

                else
                {
                    pickValue="1";
                }

                if (list.get(0).getDropup().equalsIgnoreCase(list.get(i).getDropup()))
                {
                    if (list.get(0).getDropup().equalsIgnoreCase(""))
                    {
                        dropValue="1";
                    }
                    else
                    {
                        dropValue="0";
                    }
                }
                else
                {
                    dropValue="1";
                }
            }

            //to set list values

            if (pickValue.equalsIgnoreCase("0"))
            {
//                priorityListPick.add(String.valueOf(0));
                priorityListPick.add(String.valueOf(1));
            }
            else
            {
                for (int i = 0; i <valuesss ; i++)
                {
                    priorityListPick.add(String.valueOf(i+1));
//                    priorityListPick.add(String.valueOf(i));
                }
            }

            if (dropValue.equalsIgnoreCase("0"))
            {
//                priorityListDrop.add(String.valueOf(0));
                priorityListDrop.add(String.valueOf(1));
            }

            else
            {
              for (int i = 0; i <valuesss ; i++)
             {
             priorityListDrop.add(String.valueOf(i+1));
//             priorityListDrop.add(String.valueOf(i));
             }
            }
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view= LayoutInflater.from(context).inflate(R.layout.custom_pickup,viewGroup,false);
        return new ListofPickUpAdapter.MyViewHolder(view);
    }





    @Override
    public void setHasStableIds(boolean hasStableIds)
    {
        super.setHasStableIds(hasStableIds);
    }

    @Override
    public int getItemViewType(int position)
    {
        return position;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position)
    {

        itemPosition=position;
        holder.tvRiderName.setText(list.get(position).getFirst_name()+" "+list.get(position).getLast_name());

        Log.e("AdapterValueIN  ",list.get(position).isSignIn()+"");
        Log.e("AdapterValueOut  ",list.get(position).isSignOut()+"");


      /*  if (list.get(position).isSignIn())
        {
            holder.cbSignin.setChecked(true);
        }
        else {
            holder.cbSignin.setChecked(false);
        }

        if (list.get(position).isSignOut())
        {
            holder.cbSignOut.setChecked(true);
        }
        else
        {
            holder.cbSignOut.setChecked(false);
        }
*/
        if (!list.get(position).getImage().equalsIgnoreCase(""))
        {
            Picasso.with(context).load(list.get(position).getImage()).placeholder(context.getResources().getDrawable(R.drawable.skip_rider))
                    .into(holder.ivRider);
        }

        if (!list.get(position).getPickup().equalsIgnoreCase(""))
        {
            holder.tvPick.setTextColor(context.getResources().getColor(R.color.dark_gray));
            holder.tvPick.setText(list.get(position).getPickup());
        }
        else
        {
            holder.tvPick.setTextColor(context.getResources().getColor(R.color.light_gray));
            holder.tvPick.setText("Select Pick-Up Location");
        }

        if (!list.get(position).getDropup().equalsIgnoreCase(""))
        {
            holder.tvDest.setTextColor(context.getResources().getColor(R.color.dark_gray));
            holder.tvDest.setText(list.get(position).getDropup());
        }
        else
            {
                holder.tvDest.setTextColor(context.getResources().getColor(R.color.light_gray));
                holder.tvDest.setText("Select Drop-Off Location");
        }

        if (list.get(position).isViewSelected())
        {
            holder.viewHide.setVisibility(View.VISIBLE);
            holder.tvCount.setVisibility(View.VISIBLE);
            int value=position+1;
            holder.tvCount.setText(String.valueOf(value));
        }
        else
            {
            holder.viewHide.setVisibility(View.GONE);
            holder.tvCount.setVisibility(View.GONE);
        }

        holder.SpPick.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
            {


                    list.get(position).setPickPriority(Integer.parseInt(holder.SpPick.getSelectedItem().toString()));
                    notifyDataSetChanged();



                // listener.onPickItemSelected(getAdapterPosition(),view,SpPick,position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        }); holder.SpDest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
            {


                list.get(position).setDropPriority(Integer.parseInt(holder.SpDest.getSelectedItem().toString()));
                notifyDataSetChanged();



                // listener.onPickItemSelected(getAdapterPosition(),view,SpPick,position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });


        holder.etPick.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

                list.get(position).setPick_instructions(s.toString());
                notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });holder.etDrop.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

                list.get(position).setDrop_instructions(s.toString());
                notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        holder.cbSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSignInCheck(position,v,holder.cbSignin);
            }
        });

        holder.cbSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSignOutCheck(position,v,holder.cbSignOut);
            }
        });

    /*    holder.cbSignin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    list.get(position).setRider_sign_in("1");
                }
                else {
                    list.get(position).setRider_sign_in("0");

                }

                notifyDataSetChanged();
            }
        });

        holder.cbSignOut.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    list.get(position).setRider_sign_out("1");
                }
                else {
                    list.get(position).setRider_sign_out("0");
                }

                notifyDataSetChanged();
            }
        });*/
    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        @BindView(R.id.tvPick)
        TextView tvPick;

        @BindView(R.id.tvCount)
        TextView tvCount;

        @BindView(R.id.tvDest)
        TextView tvDest;

        @BindView(R.id.pickLabel)
        TextView pickLabel;

        @BindView(R.id.dropLabel)
        TextView dropLabel;

        @BindView(R.id.ivPick)
        ImageView ivPick;

        @BindView(R.id.ivDrop)
        ImageView ivDrop;

        @BindView(R.id.ivRider)
        CircleImageView ivRider;

        @BindView(R.id.tvRiderName)
        TextView tvRiderName;

        @BindView(R.id.viewHide)
        View viewHide;

        @BindView(R.id.main)
        ConstraintLayout main;

        @BindView(R.id.SpPick)
        Spinner SpPick;

        @BindView(R.id.SpDest)
        Spinner SpDest;

        @BindView(R.id.rlPriority)
        RelativeLayout rlPriority;

        @BindView(R.id.tvPriority)
        TextView tvPriority;

        @BindView(R.id.tvInstruct)
        TextView tvInstruct;

        @BindView(R.id.llInstructions)
        LinearLayout llInstructions;

        @BindView(R.id.etPick)
        EditText etPick;

        @BindView(R.id.etDrop)
        EditText etDrop;

        @BindView(R.id.cbSignin)
        CheckBox cbSignin;

        @BindView(R.id.cbSignOut)
        CheckBox cbSignOut;

        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            ButterKnife.bind(this,itemView);

            tvPick.setOnClickListener(this);
            tvDest.setOnClickListener(this);
            ivPick.setOnClickListener(this);
            ivDrop.setOnClickListener(this);
            main.setOnClickListener(this);


            tvPriority.setOnClickListener(this);
            tvInstruct.setOnClickListener(this);

            if (pickValue.equalsIgnoreCase("0"))
            {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,R.layout.gray_custom_spinner,
                        priorityListPick);
                SpPick.setAdapter(adapter);
                SpPick.setEnabled(false);
            }
            else {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,R.layout.custom_spinner_new,
                        priorityListPick);
                SpPick.setAdapter(adapter);
                SpPick.setEnabled(true);
            }


            if (dropValue.equalsIgnoreCase("0"))
            {

                ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(context,R.layout.gray_custom_spinner,
                        priorityListDrop);
                SpDest.setAdapter(adapter1);
                SpDest.setEnabled(false);
            }
            else {

                ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(context,R.layout.custom_spinner_new,
                        priorityListDrop);
                SpDest.setAdapter(adapter1);
                SpDest.setEnabled(true);
            }


           /* SpPick.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                {
                    Log.e("AdapterPositionPick ",getAdapterPosition()+"");
                    Log.e("ValueSelectedPick ",SpPick.getSelectedItem().toString());
                    Log.e("value<<<Pick ",parent.getItemAtPosition(position).toString());

                *//*    list.get(getAdapterPosition()).setPickPriority(Integer.parseInt(SpPick.getSelectedItem().toString()));

                    notifyDataSetChanged();*//*



                   // listener.onPickItemSelected(getAdapterPosition(),view,SpPick,position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent)
                {

                }
            });*/

/*
            SpDest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Log.e("AdapterPositionDrop ",getAdapterPosition()+"");
                    Log.e("ValueSelectedDrop ",SpDest.getSelectedItem().toString());

                    listener.onDropItemSelected(getAdapterPosition(),view,SpDest,position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
*/

        }

        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.tvPick:

                    listener.onPickLocation(getAdapterPosition(),v);

                    break;

                case R.id.tvDest:
                    listener.onDRopLocation(getAdapterPosition(),v);

                    break;

                case R.id.ivPick:

                    listener.onPickLocation(getAdapterPosition(),v);


                    break;

                case R.id.ivDrop:
                    listener.onDRopLocation(getAdapterPosition(),v);
                    break;

                    case R.id.main:
                    listener.onItemClick(getAdapterPosition(),v,viewHide,tvCount);
                    break;


                case R.id.tvPriority:
                    tvPriority.setTextColor(context.getResources().getColor(R.color.colorGreen));
                    tvInstruct.setTextColor(context.getResources().getColor(R.color.dark_gray));
                    rlPriority.setVisibility(View.VISIBLE);
                    llInstructions.setVisibility(View.GONE);

                    break;

                    case R.id.tvInstruct:
                    tvInstruct.setTextColor(context.getResources().getColor(R.color.colorGreen));
                    tvPriority.setTextColor(context.getResources().getColor(R.color.dark_gray));
                    rlPriority.setVisibility(View.GONE);
                    llInstructions.setVisibility(View.VISIBLE);

                    break;
            }
        }
    }

    public void onItemSelectedListener(myClickListener clickListener)
    {
        listener=clickListener;
    }

    public interface myClickListener
    {
         void onItemClick(int layoutPosition,View view,View selected,TextView  tv);
         void onPickLocation(int layoutPosition,View view);
         void onDRopLocation(int layoutPosition,View view);
         void onPickItemSelected(int layoutPosition,View view,Spinner spPick,int SpinnerPosition);
         void onDropItemSelected(int layoutPosition,View view,Spinner spDrop,int SpinnerPosition);
         void onSignInCheck(int layoutPosition,View view,CheckBox checkSignIn);
         void onSignOutCheck(int layoutPosition,View view,CheckBox checkSignOut);
    }
}
