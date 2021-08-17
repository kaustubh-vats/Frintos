package com.example.frintos.RecyclerAdapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.frintos.DisplayThisUser;
import com.example.frintos.Model.MyUserData;
import com.example.frintos.Model.usersData;
import com.example.frintos.R;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class AdapterClass extends RecyclerView.Adapter<AdapterClass.MyViewHolder> {

    ArrayList<MyUserData> usersArrayList;
    Context context;

    public AdapterClass(ArrayList<MyUserData> usersArrayList, Context context) {
        this.usersArrayList = usersArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.user_card,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MyUserData ud=usersArrayList.get(position);
        holder.textView.setText(ud.getName());
        holder.textView1.setText(ud.getStatus());
        RequestOptions options = new RequestOptions()
                .circleCrop()
                .placeholder(R.drawable.ic_account_circle_black_24dp)
                .error(R.drawable.ic_account_circle_black_24dp);
        Glide.with(context.getApplicationContext()).load(ud.getThumb()).apply(options).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return usersArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView,textView1;
        ImageView imageView;
        CardView cardView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            textView=itemView.findViewById(R.id.textView13);
            textView1=itemView.findViewById(R.id.textView14);
            imageView=itemView.findViewById(R.id.imageView5);
            cardView=itemView.findViewById(R.id.cardView);
        }

        @Override
        public void onClick(View v) {
            int pos=this.getAdapterPosition();
            String uid=usersArrayList.get(pos).getUid();
            Intent intent=new Intent(context,DisplayThisUser.class);
            intent.putExtra("uid",uid);
            context.startActivity(intent);
        }
    }
}
