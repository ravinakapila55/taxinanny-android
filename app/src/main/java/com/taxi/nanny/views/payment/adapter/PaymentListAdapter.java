package com.taxi.nanny.views.payment.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.taxi.nanny.R;
import com.taxi.nanny.model.CardList;
import com.taxi.nanny.views.booking.AddTip;
import com.taxi.nanny.views.booking.ChooseCard;
import com.taxi.nanny.views.booking.MakePayment;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PaymentListAdapter extends RecyclerView.Adapter<PaymentListAdapter.MyviewHolder>
{
    Context context;
    ArrayList<CardList> list;
    myclickListener listener;
    String type="";

    public PaymentListAdapter(Context context, ArrayList<CardList> list,String type)
    {
        this.context = context;
        this.list=list;
        this.type=type;
    }

    @NonNull
    @Override
    public MyviewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(context).inflate(R.layout.custom_cards,viewGroup,false);
        return new PaymentListAdapter.MyviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyviewHolder myviewHolder, int i)
    {

        if (i%2==0)
        {
            myviewHolder.constraint.setBackground(context.getResources().getDrawable(R.drawable.orange_back));
        }
        else
        {
            myviewHolder.constraint.setBackground(context.getResources().getDrawable(R.drawable.pink_back));
        }

        myviewHolder.tvExpiry.setText(list.get(i).getExpiry());
        myviewHolder.tvName.setText(list.get(i).getName());

        String number=new String(list.get(i).getNumber());
        Log.e("NUmber ",number.substring(12,number.length()));

        myviewHolder.tvCard.setText(number.substring(12,number.length()));

        if (type.equalsIgnoreCase("p"))
        {
            myviewHolder.ivTick.setVisibility(View.GONE);
            myviewHolder.ivDelete.setVisibility(View.VISIBLE);
            myviewHolder.ivDelete.setImageDrawable(context.getResources().getDrawable(R.drawable.bin));

           myviewHolder. ivDelete.setOnClickListener(new View.OnClickListener()
           {
                @Override
                public void onClick(View v)
                {
                    listener.onDeleteClick(i,v);
                }
            });
        }
        else
        {
            myviewHolder.ivDelete.setVisibility(View.GONE);
            myviewHolder.ivTick.setVisibility(View.VISIBLE);
            if (list.get(i).isFlag())
            {
                myviewHolder.ivTick.setImageDrawable(context.getResources().getDrawable(R.drawable.checked));
            }
            else {
                myviewHolder.ivTick.setImageDrawable(context.getResources().getDrawable(R.drawable.checkkkkk));
            }

            myviewHolder.ivTick.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
//                    listener.onTickClick(i,v);
                    selectCurrItem(i);
                }
            });
        }
    }

    private void selectCurrItem(int position)
    {
        int size = list.size();
        for (int i = 0; i < size; i++)
        {
            if (i == position){
                list.get(i).setFlag(true);

                if (type.equalsIgnoreCase("pay")) {
                    MakePayment.generatedToken = list.get(i).getStripe_token();
                    Log.e("onTickClick: ", MakePayment.generatedToken);

                    MakePayment.cardId = list.get(i).getCard_id();
                }
                   else if (type.equalsIgnoreCase("tip"))
                {
                    AddTip.generatedToken = list.get(i).getStripe_token();
                    Log.e( "onTickClick: " , AddTip.generatedToken);

                    AddTip.cardId = list.get(i).getCard_id();
                    Log.e( "onTickClick:CardId " ,AddTip.cardId);
                }

                else
               {
                    ChooseCard.generatedToken = list.get(i).getStripe_token();
                    Log.e( "onTickClick: " , ChooseCard.generatedToken);

                    ChooseCard.cardId = list.get(i).getCard_id();
                    Log.e( "onTickClick:CardId " ,ChooseCard.cardId);
                }
        }
            else
            {
                list.get(i).setFlag(false);
            }

            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyviewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.constraint)
        ConstraintLayout constraint;

        @BindView(R.id.tvName)
        TextView tvName;

        @BindView(R.id.ivDelete)
        ImageView ivDelete;

        @BindView(R.id.ivTick)
        ImageView ivTick;

        @BindView(R.id.tvExpiry)
        TextView tvExpiry;

        @BindView(R.id.tvCard)
        TextView tvCard;

        public MyviewHolder(@NonNull View itemView)
        {
            super(itemView);
            ButterKnife.bind(this,itemView);


        }
    }

    public void onItemSelectedListener(myclickListener myclickListener)
    {
        this.listener=myclickListener;
    }

    public interface myclickListener{
        public void onDeleteClick(int layoutPosition,View view);
        public void onTickClick(int layoutPosition,View view);
    }
}
