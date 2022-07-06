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
import java.util.Locale;
import java.util.Map;

public class adapter extends FirebaseRecyclerAdapter<model, adapter.myViewHolder> {
    Context context;

    public adapter(@NonNull FirebaseRecyclerOptions<model> options, Context context) {
        super(options);
        this.context = context;
    }

    public adapter(@NonNull FirebaseRecyclerOptions<model> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, final int position, @NonNull model model) {

//        Glide.with(holder.imageView.getContext()).load(model.getImg()).into(holder.imageView);
        holder.tvAdmin.setText(model.getAdmin());
        holder.tvMovieName.setText(model.getName());
        holder.tvMessage.setText(model.getMessage());
        holder.tvDate.setText(model.getDate());
        holder.tvLink.setText(model.getLink());
        holder.tvVid.setText(model.getVid());
        holder.tvViews.setText(model.getViews());


        if (model.getImg()==null){
            holder.imgMovie.setImageResource(R.mipmap.ic_logo1);

        }else {
            Glide.with(holder.imgMovie.getContext()).load(model.getImg()).into(holder.imgMovie);
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

                itMovieName.setText(model.getName());
                itMessage.setText(model.getMessage());
                itMovieLink.setText(model.getLink());
                itImg.setText(model.getImg());
                itVid.setText(model.getVid());

                FirebaseDatabase.getInstance().getReference("movies")
                        .child(getRef(holder.getLayoutPosition()).getKey())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String id = "Item Id: "+snapshot.getKey();
                        itemId.setText(id);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                dialogPlus.show();

                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Map<String,Object> map = new HashMap<>();
                        map.put("name",itMovieName.getText().toString());
                        map.put("message",itMessage.getText().toString());
                        map.put("link",itMovieLink.getText().toString());
                        map.put("img", itImg.getText().toString());
                        map.put("vid", itVid.getText().toString());

                        String timeStamp = new SimpleDateFormat("dd MMM, yyyy • hh:mm a", Locale.getDefault()).format(new Date());
                        map.put("date", timeStamp);

                        FirebaseDatabase.getInstance().getReference().child("movies")
                                .child(getRef(holder.getLayoutPosition()).getKey()).updateChildren(map)
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
                                        .child(getRef(holder.getLayoutPosition()).getKey()).removeValue();

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

                        AlertDialog alertDialog = new AlertDialog.Builder(myView.getContext())
                                .setTitle("Developer")
                                .setMessage("BINGE+ App is Created by G. Prajapati.")
                                .setPositiveButton("Thanks", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(myView.getContext(), "Your Welcome", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .show();


                    }
                });

            }
        });

        holder.tvLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String download_link  = model.getLink();
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
                myClip[0] = ClipData.newPlainText("text", model.getLink());
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
                        model.getName()
                        + "\n\n🎬🎬🎬🎬🎬🎬🎬🎬🎬🎬\n" +
                        model.getMessage()
                        + "\n\n🍿🍿🍿🍿🍿🍿🍿🍿🍿🍿\n" +
                        model.getLink();
                String sub = "Sharing Link! Download BINGE+";
                intent.putExtra(Intent.EXTRA_SUBJECT,sub);
                intent.putExtra(Intent.EXTRA_TEXT,body);
                v.getContext().startActivity(Intent.createChooser(intent, "Share Using"));
            }
        });

    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post,parent, false);
        return new myViewHolder(view);
    }

    public class myViewHolder extends RecyclerView.ViewHolder {

        TextView tvAdmin,tvMovieName, tvMessage, tvDate, tvLink, tvVid, tvViews;
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
        }
    }
}