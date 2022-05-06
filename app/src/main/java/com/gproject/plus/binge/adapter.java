package com.gproject.plus.binge;

import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

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
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull model model) {

//        Glide.with(holder.imageView.getContext()).load(model.getImg()).into(holder.imageView);
        holder.tvAdmin.setText(model.getAdmin());
        holder.tvMovieName.setText(model.getName());
        holder.tvMessage.setText(model.getMessage());
        holder.tvDate.setText(model.getDate());
        holder.tvLink.setText(model.getButton());

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
                Toast.makeText(context, "Link Copied", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        holder.imgShareBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            String body = "Watch Latest Movies and WebSeries on BINGE+ App for Free. Download Now! "
                    + "\n\n➖➖➖➖➖➖➖➖➖➖➖➖\n" +
                    model.getName()
                    + "\n" +
                    model.getMessage()
                    + "\n\n➖➖➖➖➖➖➖➖➖➖➖➖\n" +
                    model.getLink();
            String sub = "Sharing Link! Download BINGE+";
            intent.putExtra(Intent.EXTRA_SUBJECT,sub);
            intent.putExtra(Intent.EXTRA_TEXT,body);
            v.getContext().startActivity(Intent.createChooser(intent, "Share Using"));
        });

    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post,parent, false);
        return new myViewHolder(view);
    }

    public class myViewHolder extends RecyclerView.ViewHolder {

        TextView tvAdmin,tvMovieName, tvMessage, tvDate, tvLink;
        ImageView  imgCopyBtn, imgShareBtn;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

//            imgCopyBtn = itemView.findViewById(R.id.imgCopyBtn);
            tvAdmin = itemView.findViewById(R.id.tvAdmin);
            tvMovieName = itemView.findViewById(R.id.tvMovieName);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvDate = itemView.findViewById(R.id.tvDate);
            imgShareBtn = itemView.findViewById(R.id.imgShareBtn);
            tvLink = itemView.findViewById(R.id.tvLink);
        }
    }
}
