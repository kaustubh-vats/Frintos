package com.example.frintos.RecyclerAdapter;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.frintos.ChatActivity;
import com.example.frintos.Model.ChatModel;
import com.example.frintos.R;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder>{
    Context context;
    List<ChatModel> chatModelList;
    boolean turnAnimation;

    public ChatAdapter(Context context, List<ChatModel> chatModelList) {
        this.context = context;
        this.chatModelList = chatModelList;
        turnAnimation = true;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == 1)
        {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_layout_1,parent,false);
            return new ChatAdapter.MessageViewHolder(view, viewType);
        }
        else
        {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_layout,parent,false);
            return new ChatAdapter.MessageViewHolder(view, viewType);
        }
    }

    @Override
    public int getItemViewType(int position) {
        ChatModel chatModel = chatModelList.get(position);
        if(chatModel.getFrom()!=null && chatModel.getFrom().equals(chatModel.getMyuid()))
        {
            return 1;
        }
        else
        {
            return 0;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        ChatModel chatModel=chatModelList.get(position);
        holder.textView.setText(chatModel.getMessage());
        holder.textView.setLongClickable(true);
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(chatModel.getTimestamp());
        String myDate = calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE);
        holder.textView1.setText(myDate);
        if(holder.vType==0)
        {

            if(!chatModel.getThumb().equals("default"))
            {
                RequestOptions options = new RequestOptions()
                        .circleCrop()
                        .placeholder(R.drawable.ic_account_circle_black_24dp)
                        .error(R.drawable.ic_account_circle_black_24dp);
                Glide.with(context.getApplicationContext()).load(chatModel.getThumb()).apply(options).into(holder.imageView);
            }
            if(turnAnimation)
            {
                Animation animation= AnimationUtils.loadAnimation(context,R.anim.slideleft);
                animation.setDuration(500);
                holder.constraintLayout.startAnimation(animation);
            }
        }
        else if(turnAnimation)
        {
            Animation animation1=AnimationUtils.loadAnimation(context,R.anim.slideright_anim);
            animation1.setDuration(500);
            holder.constraintLayout.startAnimation(animation1);
        }
        if(position == 0){
            turnAnimation = false;
        }
    }

    @Override
    public int getItemCount() {
        return chatModelList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout constraintLayout;
        ImageView imageView;
        TextView textView, textView1;
        int vType;
        public MessageViewHolder(@NonNull final View itemView, int viewtype) {
            super(itemView);
            vType = viewtype;
            if(viewtype==1)
            {
                textView=itemView.findViewById(R.id.textView20);
                textView1=itemView.findViewById(R.id.textView21);
                constraintLayout=itemView.findViewById(R.id.constraintLayout90);
            }
            else{
                imageView=itemView.findViewById(R.id.imageView9);
                textView=itemView.findViewById(R.id.textView19);
                textView1=itemView.findViewById(R.id.textView24);
                constraintLayout=itemView.findViewById(R.id.constraintLayout10);
            }
            textView.setOnLongClickListener(v -> {
                ClipboardManager clipboardManager= (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Message",textView.getText().toString());
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(context, "Text has been copied", Toast.LENGTH_SHORT).show();
                return true;
            });
            textView.setOnClickListener(v -> {
                Calendar calendar=Calendar.getInstance();
                calendar.setTimeInMillis(chatModelList.get(getAdapterPosition()).getTimestamp());
                String[] months={"January","February","March","April","May","June","July","August","September","October","November","December"};
                String[] days={"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
                String[] am_pm={"AM","PM"};
                System.out.println(calendar.get(Calendar.DAY_OF_WEEK));
                String messageInfo = days[calendar.get(Calendar.DAY_OF_WEEK)-1]+", "+months[calendar.get(Calendar.MONTH)]+" "+calendar.get(Calendar.DAY_OF_MONTH)+" "+calendar.get(Calendar.YEAR)+"\n"+calendar.get(Calendar.HOUR)+":"+calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND)+" "+am_pm[calendar.get(Calendar.AM_PM)];
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(messageInfo);
                builder.setTitle("Message Info");
                builder.setIcon(AppCompatResources.getDrawable(context, R.drawable.logo));
                builder.setNeutralButton("Okay", (dialog, which) -> dialog.cancel());
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            });

        }


    }
}