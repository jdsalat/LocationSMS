package com.locationsms.sms.adapters;

/**
 * Created by Javed.Salat on 6/4/2016.
 */

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.locationsms.sms.R;
import com.locationsms.sms.beans.ProfileBean;
import com.locationsms.sms.fragments.MessageFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by javed.salat on 31-05-2016.
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {
    private List<ProfileBean> profileBeanList;
    public static final int iD = 0;

    static OnClickItemListener onClickItemListener;

    public MessageAdapter(List<ProfileBean> profileBeans, MessageFragment messageFragment) {
        this.profileBeanList = profileBeans;
        onClickItemListener = (OnClickItemListener) messageFragment;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.item_recycler_message, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        ProfileBean contactInfo = profileBeanList.get(position);
        holder.tvSmsFrequency.setText(contactInfo.getSmsBean().getSmsFrequency()+"");
        holder.tvSmsDuration.setText(contactInfo.getSmsBean().getSmsDuration()+"");
        holder.tvProfileFirstNumber.setText(contactInfo.getSmsBean().getFirstNumber());
        holder.tvProfileSecondNumber.setText(contactInfo.getSmsBean().getSecondNumber());
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                setPosition(position);
                return false;
            }
        });

    }


    @Override
    public int getItemCount() {
        return profileBeanList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
        @BindView(R.id.tv_profile_first_number)
        TextView tvProfileFirstNumber;
        @BindView(R.id.tv_profile_second_number)
        TextView tvProfileSecondNumber;
        @BindView(R.id.tv_sms_frequency)
        TextView tvSmsFrequency;
        @BindView(R.id.tv_sms_duration)
        TextView tvSmsDuration;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnCreateContextMenuListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClick(getLayoutPosition());
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.setHeaderTitle("Select The Action");
            contextMenu.add(0, iD,
                    0, R.string.stop_message);

        }
    }

    private static void onItemClick(int position) {
        onClickItemListener.onItemClick(position);
    }

    public interface OnClickItemListener {
        void onItemClick(int position);
    }


    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

}