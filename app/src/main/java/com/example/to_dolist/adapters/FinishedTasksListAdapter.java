package com.example.to_dolist.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.to_dolist.R;
import com.example.to_dolist.interfaces.RecyclerViewItemButtonsClickListeners;
import com.example.to_dolist.models.ToDoTaskModel;

import java.util.ArrayList;
import java.util.Random;

public class FinishedTasksListAdapter extends RecyclerView.Adapter<FinishedTasksListAdapter.FinishedTasksViewHolder> {

    private final Context context;
    private final ArrayList<ToDoTaskModel> tasksArrayList;
    private final RecyclerViewItemButtonsClickListeners recyclerViewItemButtonsClickListeners;

    public FinishedTasksListAdapter(Context context, ArrayList<ToDoTaskModel> tasksArrayList, RecyclerViewItemButtonsClickListeners recyclerViewItemButtonsClickListeners) {
        this.tasksArrayList = tasksArrayList;
        this.context = context;
        this.recyclerViewItemButtonsClickListeners = recyclerViewItemButtonsClickListeners;
    }

    //********************************************************************************************************//
    //***************************************RecyclerView Override Methods************************************//

    @NonNull
    @Override
    public FinishedTasksListAdapter.FinishedTasksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.finished_to_do_list_item, parent, false);
        final FinishedTasksViewHolder finishedTasksViewHolder = new FinishedTasksViewHolder(view);

        int[] androidColors = view.getResources().getIntArray(R.array.android_colors);
        int randomColor = androidColors[new Random().nextInt(androidColors.length)];

        finishedTasksViewHolder.cardViewAccordion.setCardBackgroundColor(randomColor);
        finishedTasksViewHolder.downArrow.setOnClickListener(view1 -> {

            if (finishedTasksViewHolder.cardViewAccordionLower.getVisibility() == View.GONE) {
                finishedTasksViewHolder.cardViewAccordionLower.setVisibility(View.VISIBLE);
            } else {
                finishedTasksViewHolder.cardViewAccordionLower.setVisibility(View.GONE);
            }

        });

        return finishedTasksViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FinishedTasksListAdapter.FinishedTasksViewHolder holder, int position) {

        final String title = tasksArrayList.get(position).getTitle();
        final String description = tasksArrayList.get(position).getDescription();

        holder.titleTV.setText(title);

        if (!description.equals("")) {
            holder.descTV.setText(description);
        }

    }

    @Override
    public int getItemCount() {
        return tasksArrayList.size();
    }


    //********************************************************************************************************//
    //****************************************ViewHolder Class************************************************//


    public class FinishedTasksViewHolder extends RecyclerView.ViewHolder {

        CardView cardViewAccordion;
        TextView titleTV, descTV;
        RelativeLayout cardViewAccordionLower;
        ImageView downArrow, deleteBtn;

        public FinishedTasksViewHolder(@NonNull View itemView) {
            super(itemView);

            //Creating the VH variables for the ListItem view.
            cardViewAccordion = itemView.findViewById(R.id.task_accordion_card_view);
            titleTV = itemView.findViewById(R.id.task_title_accordion_card_view);
            descTV = itemView.findViewById(R.id.description_accordion_card_view);
            cardViewAccordionLower = itemView.findViewById(R.id.relative_layout_accordion_card_view_lower);
            downArrow = itemView.findViewById(R.id.img_down_arrow_accordion_card_view);
            deleteBtn = itemView.findViewById(R.id.img_delete_task_accordion_card_view);

            //Using the RecyclerViewItemButtonsClickListeners interface methods so that desired result is generated upon clicking the items/buttons.

            deleteBtn.setOnClickListener(view -> {
                recyclerViewItemButtonsClickListeners.onDeleteBtnClick(getAdapterPosition());
            });

        }
    }
}
