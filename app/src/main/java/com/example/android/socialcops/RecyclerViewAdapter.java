package com.example.android.socialcops;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

/**
 * Created by hp on 08-02-2019.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {

    private Context mcontext;
  private   ArrayList<RecyclerItem> mRecyclerList;

  public RecyclerViewAdapter(Context context,ArrayList<RecyclerItem> r)
  {mcontext=context;
  mRecyclerList=r;}

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View v= LayoutInflater.from(mcontext).inflate(R.layout.recycler_item,parent,false);
       return new RecyclerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
RecyclerItem currentItem=mRecyclerList.get(position);
String imageUrl=currentItem.getmImageUrl();
String title=currentItem.getmTitle();
String description=currentItem.getmDescription();

holder.mTextView1.setText(title);
holder.mTextView2.setText(description);
        Picasso.get().load(imageUrl).fit().centerInside().into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mRecyclerList.size();
    }

   public void filteredlist(ArrayList<RecyclerItem> filteredlist)
    {mRecyclerList=filteredlist;
    notifyDataSetChanged();}

    public class RecyclerViewHolder extends  RecyclerView.ViewHolder{

        public ImageView mImageView;
        public TextView mTextView1;
        public TextView mTextView2;
        public RecyclerViewHolder(View View) {
            super(View);
            mImageView=(ImageView)  View.findViewById(R.id.image_view);
            mTextView1=(TextView)  View.findViewById(R.id.title1);
            mTextView2=(TextView)  View.findViewById(R.id.description);
        }


    }
}
