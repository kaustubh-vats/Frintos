package com.example.frintos.RecyclerAdapter;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.frintos.ChatActivity;
import com.example.frintos.Model.MyUserData;
import com.example.frintos.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.widget.Toast;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyOnViewHolder> {

    ArrayList<MyUserData> usersArrayList;
    Context context;

    public MyRecyclerAdapter(ArrayList<MyUserData> usersArrayList, Context context) {
        this.usersArrayList = usersArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyOnViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.user_card,parent,false);
        return new MyOnViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyOnViewHolder holder, int position) {
        MyUserData ud=usersArrayList.get(position);
        holder.textView.setText(ud.getName());
        RequestOptions options = new RequestOptions()
                .circleCrop()
                .placeholder(R.drawable.ic_account_circle_black_24dp)
                .error(R.drawable.ic_account_circle_black_24dp);
        Glide.with(context.getApplicationContext()).load(ud.getThumb()).apply(options).into(holder.imageView);
        if(ud.getOnline().equals("true"))
        {
            holder.textView1.setText("Active now");
            holder.imageView1.setVisibility(View.VISIBLE);
            ObjectAnimator hbeat=ObjectAnimator.ofPropertyValuesHolder(holder.imageView1, PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                    PropertyValuesHolder.ofFloat("scaleY", 1.2f));
            hbeat.setDuration(500);
            hbeat.setRepeatCount(ObjectAnimator.INFINITE);
            hbeat.setRepeatMode(ObjectAnimator.REVERSE);
            hbeat.start();
        }
        else if(ud.getOnline().equals("false"))
        {
            holder.textView1.setText(String.format("%s need to update the app",ud.getName()));
        }
        else
        {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            try {
                Date date1 = dateFormat.parse(ud.getOnline());
                long diff = date.getTime() - date1.getTime();
                String dayName =(String) android.text.format.DateFormat.format("EEEE", date1);
                float numOfDayPass = diff/86400000.0f;
                String lastsn;
                if (numOfDayPass < 1) {
                    long sec = diff / 1000;
                    long min = sec / 60;
                    long hour = min / 60;
                    sec=sec%60;
                    min=min%60;
                    if(hour==0)
                    {
                        if(min==0)
                        {
                            lastsn=sec+" seconds ago";
                        }
                        else
                        {
                            lastsn=min+" minutes ago";
                        }
                    }
                    else
                    {
                        lastsn=hour+" hours ago";
                    }
                }
                else if ((numOfDayPass >= 1) && (numOfDayPass < 7)) {
                    lastsn = " was on "+dayName ;
                }
                else if ((numOfDayPass >= 7) && (numOfDayPass < 30)) {
                    int weeks = (int) numOfDayPass / 7;
                    if(weeks > 1) {
                        lastsn = weeks + " weeks ago";
                    }else{
                        lastsn = weeks + " week ago";
                    }
                }
                else if((numOfDayPass >= 30) && (numOfDayPass < 365) ){
                    int months = (int) numOfDayPass/30;
                    if(months > 1) {
                        lastsn = months + " months ago";
                    }
                    else{
                        lastsn = months + " month ago";
                    }
                }
                else if(numOfDayPass >= 365){
                    int years = (int) numOfDayPass/365;

                    if(years > 1) {
                        lastsn = "few years ago";
                    }
                    else{
                        lastsn = "1 year ago";
                    }
                }
                else{
                    lastsn = ud.getOnline();
                }
                holder.textView1.setText(String.format("Last seen %s", lastsn));
            } catch (ParseException e) {
                holder.textView1.setText(String.format("Last seen at: %s", ud.getOnline()));
                e.printStackTrace();
            }
            holder.imageView1.setVisibility(View.INVISIBLE);
        }
        if(!ud.getStatus().equals(ud.getOnline()))
        {
            holder.textView1.setText(ud.getStatus());
        }
        holder.textView1.setSelected(true);
    }

    @Override
    public int getItemCount() {
        return usersArrayList.size();
    }

    class MyOnViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView,textView1;
        ImageView imageView,imageView1;
        CardView cardView;
        public MyOnViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            textView=itemView.findViewById(R.id.textView13);
            textView1=itemView.findViewById(R.id.textView14);
            imageView=itemView.findViewById(R.id.imageView5);
            cardView=itemView.findViewById(R.id.cardView);
            imageView1=itemView.findViewById(R.id.imageView8);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            MyUserData myUserData = usersArrayList.get(position);
            Intent intent=new Intent(context, ChatActivity.class);
            intent.putExtra("name",myUserData.getName());
            intent.putExtra("online",myUserData.getOnline());
            intent.putExtra("status",myUserData.getStatus());
            intent.putExtra("uid",myUserData.getUid());
            intent.putExtra("picture",myUserData.getPicture());
            intent.putExtra("thumb",myUserData.getThumb());
            context.startActivity(intent);
        }
    }
}
