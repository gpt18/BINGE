package com.gproject.binge.admin;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class adapter extends RecyclerView.Adapter<adapter.myViewHolder> {
    Context context;
    private List<model> itemList;


    public adapter(@NonNull List<model> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    public void setFilteredList(List<model> filteredList){
        this.itemList = filteredList;
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder,  int position) {

        holder.tvAdmin.setText(itemList.get(position).getAdmin());
        holder.tvMovieName.setText(itemList.get(position).getName());
        holder.tvMessage.setText(itemList.get(position).getMessage());
        holder.tvDate.setText(itemList.get(position).getDate());
        holder.tvLink.setText(itemList.get(position).getLink());
        holder.tvVid.setText(itemList.get(position).getVid());
        holder.tvViews.setText(itemList.get(position).getViews());

        if(Objects.equals(itemList.get(position).getZip(), "1")){
            holder.tvZip.setText("True");
        }


        if (itemList.get(position).getImg()==null){
            holder.imgMovie.setImageResource(R.mipmap.ic_logo1);

        }else {
            Glide.with(holder.imgMovie.getContext()).load(itemList.get(position).getImg()).into(holder.imgMovie);
        }

        holder.imgEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final DialogPlus dialogPlus = DialogPlus.newDialog(holder.tvMovieName.getContext())
                        .setContentHolder(new ViewHolder(R.layout.dialog_update))
                        .setExpanded(true,1350)
                        .create();

                View myView = dialogPlus.getHolderView();

                TextView itemId = myView.findViewById(R.id.itemId);
                TextInputEditText itMovieName = myView.findViewById(R.id.itMovieName);
                TextInputEditText itMessage = myView.findViewById(R.id.itMessage);
                TextInputEditText itMovieLink = myView.findViewById(R.id.itMovieLink);
                TextInputEditText itImg = myView.findViewById(R.id.itImg);
                TextInputEditText itVid = myView.findViewById(R.id.itVid);
                Button submit = myView.findViewById(R.id.addSubmit);
                Button delete = myView.findViewById(R.id.delete);
                ImageView more = myView.findViewById(R.id.more);
                CheckBox zip = myView.findViewById(R.id.zip);

                itMovieName.setText(itemList.get(position).getName());
                itMessage.setText(itemList.get(position).getMessage());
                itMovieLink.setText(itemList.get(position).getLink());
                itImg.setText(itemList.get(position).getImg());
                itVid.setText(itemList.get(position).getVid());
                itemId.setText(itemList.get(position).getId());

                if(Objects.equals(itemList.get(position).getZip(), "1")){
                    zip.setChecked(true);
                }

                dialogPlus.show();

                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String Zip = "0";
                        if(zip.isChecked()){
                            Zip = "1";
                        }

                        Map<String,Object> map = new HashMap<>();
                        map.put("name",itMovieName.getText().toString());
                        map.put("message",itMessage.getText().toString());
                        map.put("link",itMovieLink.getText().toString());
                        map.put("img", itImg.getText().toString());
                        map.put("vid", itVid.getText().toString());
                        map.put("zip", Zip);

                        String timeStamp = new SimpleDateFormat("dd MMM, yyyy • hh:mm a", Locale.getDefault()).format(new Date());
                        map.put("date", timeStamp);

                        FirebaseDatabase.getInstance().getReference().child("movies")
                                .child(itemList.get(position).getId()).updateChildren(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        dialogPlus.dismiss();
                                        Toast.makeText(myView.getContext(),"Updated successfully", Toast.LENGTH_SHORT ).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        dialogPlus.dismiss();
                                        Toast.makeText(myView.getContext(),"Could not update", Toast.LENGTH_SHORT ).show();
                                    }
                                });
                    }
                });

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.tvMovieName.getContext());
                        builder.setTitle("Delete Panel");
                        builder.setMessage("Alert! Data will be Lost. You want to delete this item. Are you sure?");

                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                FirebaseDatabase.getInstance().getReference().child("movies")
                                        .child(itemList.get(position).getId()).removeValue();

                                dialogPlus.dismiss();
                                Toast.makeText(myView.getContext(),"Deleted Successfully", Toast.LENGTH_SHORT ).show();

                            }
                        });

                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                        builder.show();
                    }
                });

                more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialogPlus.dismiss();

//**                        AlertDialog alertDialog = new AlertDialog.Builder(myView.getContext())
//                                .setTitle("Developer")
//                                .setMessage("BINGE+ App is Created by G. Prajapati.")
//                                .setPositiveButton("Thanks", new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        Toast.makeText(myView.getContext(), "Your Welcome", Toast.LENGTH_SHORT).show();
//                                    }
//                                })
//                                .show();


                    }
                });

            }
        });

        holder.tvLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String download_link  = itemList.get(position).getLink();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(download_link));
                v.getContext().startActivity(intent);
            }
        });

        holder.tvLink.setOnLongClickListener(new View.OnLongClickListener() {

            final ClipData[] myClip = new ClipData[1];
            final ClipboardManager clipboard = (ClipboardManager)
                    context.getSystemService(Context.CLIPBOARD_SERVICE);

            @Override
            public boolean onLongClick(View v) {
                myClip[0] = ClipData.newPlainText("text", itemList.get(position).getLink());
                clipboard.setPrimaryClip(myClip[0]);
                Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        holder.imgShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String body = "Watch Latest Movies and WebSeries on BINGE+ App for Free. Download Now! https://bit.ly/3P5PvJd "
                        + "\n\n📽️📽️📽️📽️📽️📽️📽️\n" +
                        itemList.get(position).getName()
                        + "\n\n🎬🎬🎬🎬🎬🎬🎬🎬🎬🎬\n" +
                        itemList.get(position).getMessage()
                        + "\n\n🍿🍿🍿🍿🍿🍿🍿🍿🍿🍿\n" +
                        itemList.get(position).getLink();
                String sub = "Sharing Link! Download BINGE+";
                intent.putExtra(Intent.EXTRA_SUBJECT,sub);
                intent.putExtra(Intent.EXTRA_TEXT,body);
                v.getContext().startActivity(Intent.createChooser(intent, "Share Using"));
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post,parent, false);
        return new myViewHolder(view);
    }

    public class myViewHolder extends RecyclerView.ViewHolder {

        TextView tvAdmin,tvMovieName, tvMessage, tvDate, tvLink, tvVid, tvViews, tvZip;
        ImageView imgMovie, imgShareBtn, imgEditBtn;


        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            imgMovie = itemView.findViewById(R.id.imgMovie);
            imgEditBtn = itemView.findViewById(R.id.imgEditBtn);
            tvAdmin = itemView.findViewById(R.id.tvAdmin);
            tvMovieName = itemView.findViewById(R.id.tvMovieName);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvDate = itemView.findViewById(R.id.tvDate);
            imgShareBtn = itemView.findViewById(R.id.imgShareBtn);
            tvLink = itemView.findViewById(R.id.tvLink);
            tvVid = itemView.findViewById(R.id.tvVid);
            tvViews = itemView.findViewById(R.id.tvViews);
            tvZip = itemView.findViewById(R.id.tvZip);
        }
    }
}