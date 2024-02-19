package com.frintos.frintos.RecyclerAdapter;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.frintos.frintos.Model.ChatModel;
import com.frintos.frintos.R;

import java.util.Calendar;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder>{
    Context context;
    List<ChatModel> chatModelList;
    boolean turnAnimation;
    public static String[] months={"January","February","March","April","May","June","July","August","September","October","November","December"};

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
        String prevMessageSender = getMessageSender(position-1);
        String nextMessageSender = getMessageSender(position+1);
        String currMessageSender = chatModel.getFrom();
        boolean shouldShowImage = shouldShowImage(nextMessageSender, currMessageSender);
        String bubbleType = getBubbleType(prevMessageSender, nextMessageSender, currMessageSender);
        setBubbleType(bubbleType, holder.textView, holder.vType);
        String formattedDate = getTimeStampIfNeeded(position, chatModel.getTimestamp());
        if(formattedDate.equals("false")){
            holder.dateRelativeLayout.setVisibility(View.GONE);
        } else {
            holder.dateView.setText(formattedDate);
            holder.dateRelativeLayout.setVisibility(View.VISIBLE);
        }
        if(holder.vType==0)
        {
            if(!shouldShowImage){
                holder.imageView.setVisibility(View.INVISIBLE);
            }
            if(!chatModel.getThumb().equals("default") && shouldShowImage) {
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

    private void setBubbleType(String bubbleType, TextView textView, int vType) {
        if(vType == 0){
            if(bubbleType.equals("mid")){
                textView.setBackground(AppCompatResources.getDrawable(context, R.drawable.chat_bg_mid));
            } else if (bubbleType.equals("start")){
                textView.setBackground(AppCompatResources.getDrawable(context, R.drawable.chat_bg_start));
            }
        } else {
            if(bubbleType.equals("mid")){
                textView.setBackground(AppCompatResources.getDrawable(context, R.drawable.chat_bg_1_mid));
            } else if (bubbleType.equals("start")){
                textView.setBackground(AppCompatResources.getDrawable(context, R.drawable.chat_bg_1_start));
            }
        }
    }

    private String getBubbleType(String prevMessageSender, String nextMessageSender, String currMessageSender) {
        if(prevMessageSender.equals(currMessageSender) && nextMessageSender.equals(prevMessageSender)){
            return "mid";
        } else if(nextMessageSender.equals(currMessageSender)){
            return "start";
        } else {
            return "end";
        }
    }

    private boolean shouldShowImage(String nextMessageSender, String currMessageSender) {
        return !nextMessageSender.equals(currMessageSender);
    }

    private String getMessageSender(int i) {
        if(i >= getItemCount() || i < 0) return "";
        return chatModelList.get(i).getFrom();
    }

    private String getTimeStampIfNeeded(int position, long timestamp){
        if(position > 0) {
            ChatModel chatModel = chatModelList.get(position - 1);
            Calendar calendar = Calendar.getInstance();
            Calendar prevCalender = Calendar.getInstance();
            calendar.setTimeInMillis(timestamp);
            prevCalender.setTimeInMillis(chatModel.getTimestamp());
            int prevDay, prevMonth, prevYear, currDay, currMonth, currYear;
            prevDay = prevCalender.get(Calendar.DAY_OF_MONTH);
            prevMonth = prevCalender.get(Calendar.MONTH);
            prevYear = prevCalender.get(Calendar.YEAR);
            currDay = calendar.get(Calendar.DAY_OF_MONTH);
            currMonth = calendar.get(Calendar.MONTH);
            currYear = calendar.get(Calendar.YEAR);
            if (prevDay != currDay || prevMonth != currMonth || prevYear != currYear) {
                return getFormatedDateFromTS(timestamp);
            } else {
                return "false";
            }
        } else {
            return getFormatedDateFromTS(timestamp);
        }
    }

    private String getFormatedDateFromTS(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        return day + " " + months[month] + ", "+year;
    }

    @Override
    public int getItemCount() {
        return chatModelList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout constraintLayout;
        RelativeLayout dateRelativeLayout;
        ImageView imageView;
        TextView textView, textView1, dateView;
        int vType;
        public MessageViewHolder(@NonNull final View itemView, int viewtype) {
            super(itemView);
            vType = viewtype;
            if(viewtype==1)
            {
                textView=itemView.findViewById(R.id.textView20);
                textView1=itemView.findViewById(R.id.textView21);
                constraintLayout=itemView.findViewById(R.id.constraintLayout90);
                dateRelativeLayout=itemView.findViewById(R.id.dateRelativeLayout1);
                dateView=dateRelativeLayout.findViewById(R.id.textView311);
            }
            else{
                imageView=itemView.findViewById(R.id.imageView9);
                textView=itemView.findViewById(R.id.textView19);
                textView1=itemView.findViewById(R.id.textView24);
                constraintLayout=itemView.findViewById(R.id.constraintLayout10);
                dateRelativeLayout=itemView.findViewById(R.id.dateRelativeLayout);
                dateView=dateRelativeLayout.findViewById(R.id.textView31);
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
                calendar.setTimeInMillis(chatModelList.get(getBindingAdapterPosition()).getTimestamp());
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