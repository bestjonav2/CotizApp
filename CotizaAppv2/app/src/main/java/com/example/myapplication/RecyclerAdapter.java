package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.Models.Cotization;

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
        holder.title.setText("Cotizacion #" + list.get(position).getId());
        holder.desc.setText(list.get(position).getDescription());
        holder.price.setText("$" + String.valueOf(list.get(position).getAverageCost()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyRecyclerHolder extends RecyclerView.ViewHolder {
        private ImageView imageview;
        private TextView title, desc, price;

        public MyRecyclerHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            desc = (TextView) itemView.findViewById(R.id.desc);
            price = (TextView) itemView.findViewById(R.id.price);
            //imageview2 = (ImageView) itemView.findViewById(R.id.imageView2);
        }
    }
}
