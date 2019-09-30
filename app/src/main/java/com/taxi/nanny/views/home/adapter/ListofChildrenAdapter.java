package com.taxi.nanny.views.home.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.taxi.nanny.R;
import com.taxi.nanny.model.RiderListModel;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ListofChildrenAdapter extends RecyclerView.Adapter<ListofChildrenAdapter.MyVieHolder>
{
    Context context;
    MyClickListener clickListener;
    List<RiderListModel> list;
    public  static    boolean flag;
    public static   ArrayList<String> listIds=new ArrayList<>();
//  public static   ArrayList<RiderListModel> listIds=new ArrayList<>();
    String type;
   boolean isSelectedAll=false;
   int layoutPosition;


    public ListofChildrenAdapter(Context context,List<RiderListModel> listofChildren,String type)
    {
        this.context = context;
        this.list=listofChildren;
        this.type=type;
    }

    public void selectAllRiders(){
        Log.e("onClickSelectAll","yes");
        isSelectedAll=true;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public MyVieHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view= LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.custom_child,viewGroup,false);
        return new ListofChildrenAdapter.MyVieHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyVieHolder myVieHolder, int position)
    {
        layoutPosition=position;

        myVieHolder.tvName.setText(list.get(position).getFirst_name()+" "+list.get(position).getLast_name());

        if (!list.get(position).getImage().equalsIgnoreCase(""))
        {
            Picasso.with(context).load(list.get(position).getImage()).
                    placeholder(context.getResources().getDrawable(R.drawable.skip_rider))
                    .into(myVieHolder.ivChild);
        }





      /*  if (!isSelectedAll)
        {
            myVieHolder.cbChild.setChecked(false);
        }
        else
        {
            myVieHolder.cbChild.setChecked(true);
        }*/

        if (list.get(position).isFlag())
        {
            myVieHolder.cbChild.setChecked(true);
        }
        else
        {
            myVieHolder.cbChild.setChecked(false);
        }

        if (type.equalsIgnoreCase("register"))
        {
            myVieHolder.cbChild.setVisibility(View.VISIBLE);


            myVieHolder.cbChild.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  /*  list.get(position).setFlag(list.get(position).isFlag()?false:true);
                    notifyDataSetChanged();*/
                    clickListener.onCheckClick(position,v,myVieHolder.cbChild);
                }
            });

         /*   myVieHolder.cbChild.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    list.get(position).setFlag(list.get(position).isFlag()?false:true);
                    notifyDataSetChanged();

                }
            });*/
        }
        else
        {
            myVieHolder.cbChild.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }

    public class MyVieHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.cbChild)
        CheckBox cbChild;

        @BindView(R.id.ivChild)
        ImageView ivChild;

        @BindView(R.id.tvName)
        TextView tvName;
        public MyVieHolder(@NonNull View itemView)
        {
            super(itemView);
            ButterKnife.bind(this,itemView);



         /*   cbChild.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    list.get(getAdapterPosition()).setFlag(list.get(getAdapterPosition()).isFlag()?false:true);
                    notifyDataSetChanged();

                    //clickListener.onCheckClick(getAdapterPosition(),buttonView,cbChild);
                }
            });*/
        }
    }
    public void onItemSelectedListener(MyClickListener myClickListener)
    {
        clickListener=myClickListener;
    }

    @Override
    public int getItemViewType(int position)
    {
        return layoutPosition;
    }

    @Override
    public long getItemId(int position)
    {
        return layoutPosition;
    }



    @Override
    public void setHasStableIds(boolean hasStableIds)
    {
        super.setHasStableIds(hasStableIds);
    }

    public interface MyClickListener
    {
        public void onCheckClick(int layoutPosition,View view,CheckBox checkBox);
    }
}
