package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.Models.Cotization;

import java.io.InputStream;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyRecyclerHolder> {
    private LayoutInflater inflater;
    private List<Cotization> list;

    public RecyclerAdapter(Context context, List<Cotization> list) {
        inflater = LayoutInflater.from(context);
        this.list = list;
    }

    @Override
    public MyRecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyRecyclerHolder(inflater.inflate(R.layout.items_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(MyRecyclerHolder holder, int position) {
        holder.title.setText("Cotizacion #" + (position + 1));
        holder.desc.setText(list.get(position).getDescription());
        holder.price.setText("$" + String.valueOf(list.get(position).getAverageCost()));
        new DownloadImageTask(holder.imageview).execute(list.get(position).getUrl());
        holder.vol.setText("Volumen: " + list.get(position).getFigureVolume());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyRecyclerHolder extends RecyclerView.ViewHolder {
        private ImageView imageview;
        private TextView title, desc, price, vol;

        public MyRecyclerHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            desc = (TextView) itemView.findViewById(R.id.desc);
            price = (TextView) itemView.findViewById(R.id.price);
            imageview = (ImageView) itemView.findViewById(R.id.image);
            vol = (TextView) itemView.findViewById(R.id.volume);
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
