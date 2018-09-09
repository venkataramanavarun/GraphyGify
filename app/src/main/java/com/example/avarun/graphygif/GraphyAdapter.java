package com.example.avarun.graphygif;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.ArrayList;

/**
 * Created by A VARUN on 30-08-2018.
 */

public class GraphyAdapter extends RecyclerView.Adapter<GraphyAdapter.MyViewHolder> {

    private Context mContext;
    ArrayList<DTO> dtoArrayList;
    public static final String KEY_TITLE = "title";

    Pagination pagination;
    private String url;
    private String selectSub;

    public GraphyAdapter(ArrayList<DTO> dtoArrayList, Context mContext, Object mainActivityClass) {
        this.mContext = mContext;
        this.dtoArrayList = dtoArrayList;
        this.pagination=(Pagination)mainActivityClass;

    }

    
    @Override
    public GraphyAdapter.MyViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        Fresco.initialize(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder( GraphyAdapter.MyViewHolder holder, final int position) {


        if(dtoArrayList.size()-1==position){
            pagination.pagination();
        }


        

       // final Uri uri = Uri.parse(dtoArrayList.get(position).url);


        //holder.draweeView.setImageURI(uri);

        Glide.with(mContext).load(dtoArrayList.get(position).url).into(holder.gif_img);
        //holder.title_disc.setText(dtoArrayList.get(position).username);

        holder.gif_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, LoadBigImageActivity.class);
                intent.putExtra("url", dtoArrayList.get(position).url);
                mContext.startActivity(intent);


            }
        });


    }

    @Override
    public int getItemCount() {
        return dtoArrayList.size();
    }

    public void setSelectSub(String selectSub) {
        this.selectSub = selectSub;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView gif_img;
        TextView title_disc;

        public MyViewHolder(final View itemView) {

            super(itemView);
            //title_disc = itemView.findViewById(R.id.title_disc);
            gif_img = itemView.findViewById(R.id.gif_img);


        }
    }


}
