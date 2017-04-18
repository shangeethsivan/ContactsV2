package com.shangeeth.contactsclonev2.adapters;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shangeeth.contactsclonev2.R;
import com.shangeeth.contactsclonev2.jdo.PrimaryContactsJDO;
import com.shangeeth.contactsclonev2.util.RoundedTransformation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class CustomRecyclerViewAdapter extends RecyclerView.Adapter<CustomRecyclerViewAdapter.ViewHolder> {


    ArrayList<PrimaryContactsJDO> contactsJDOs;
    private Context mContext;

    public CustomRecyclerViewAdapter(Context mContext, ArrayList<PrimaryContactsJDO> contactsJDOs) {
        this.mContext = mContext;
        this.contactsJDOs = contactsJDOs;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.rec_view_item, null);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.mContactNameTV.setText(contactsJDOs.get(position).getDisplayName());

        Picasso.with(mContext)
                .load(contactsJDOs.get(position).getPhotoUri())
                .placeholder(R.drawable.contact_placeholder)
                .resize(100,100)
                .transform(new RoundedTransformation(100,1))
                .into(holder.mContactImageTV);
    }


    @Override
    public int getItemCount() {
        return contactsJDOs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView mContactNameTV;
        ImageView mContactImageTV;

        public ViewHolder(View itemView) {

            super(itemView);

            mContactNameTV = (TextView) itemView.findViewById(R.id.contact_name);
            mContactImageTV = (ImageView) itemView.findViewById(R.id.user_image);
        }


    }
}
