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

public class TasksListAdapter extends RecyclerView.Adapter<TasksListAdapter.TasksViewHolder> {

    private final Context context;
    private final ArrayList<ToDoTaskModel> tasksArrayList;
    private final RecyclerViewItemButtonsClickListeners recyclerViewItemButtonsClickListeners;

    public TasksListAdapter(Context context, ArrayList<ToDoTaskModel> tasksArrayList, RecyclerViewItemButtonsClickListeners recyclerViewItemButtonsClickListeners) {
        this.tasksArrayList = tasksArrayList;
        this.context = context;
        this.recyclerViewItemButtonsClickListeners = recyclerViewItemButtonsClickListeners;
    }

    //********************************************************************************************************//
    //***************************************RecyclerView Override Methods************************************//

    @NonNull
    @Override
    public TasksListAdapter.TasksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.to_do_list_item, parent, false);
        final TasksViewHolder tasksViewHolder = new TasksViewHolder(view);

        int[] androidColors = view.getResources().getIntArray(R.array.android_colors);
        int randomColor = androidColors[new Random().nextInt(androidColors.length)];

        tasksViewHolder.cardViewAccordion.setCardBackgroundColor(randomColor);
        tasksViewHolder.downArrow.setOnClickListener(view1 -> {

            if(tasksViewHolder.cardViewAccordionLower.getVisibility() == View.GONE)
            {
                tasksViewHolder.cardViewAccordionLower.setVisibility(View.VISIBLE);
            }
            else
            {
                tasksViewHolder.cardViewAccordionLower.setVisibility(View.GONE);
            }

        });

        return tasksViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TasksListAdapter.TasksViewHolder holder, int position)
    {

        final String title = tasksArrayList.get(position).getTitle();
        final String description = tasksArrayList.get(position).getDescription();

        holder.titleTV.setText(title);

        if(!description.equals(""))
        {
            holder.descTV.setText(description);
        }

    }

    @Override
    public int getItemCount() {
        return tasksArrayList.size();
    }


    //********************************************************************************************************//
    //****************************************ViewHolder Class************************************************//


    public class TasksViewHolder extends RecyclerView.ViewHolder {

        CardView cardViewAccordion;
        TextView titleTV, descTV;
        RelativeLayout cardViewAccordionLower;
        ImageView downArrow, editBtn, deleteBtn, finishBtn;

        public TasksViewHolder(@NonNull View itemView)
        {
            super(itemView);

            //Creating the VH variables for the ListItem view.
            cardViewAccordion = itemView.findViewById(R.id.task_accordion_card_view);
            titleTV = itemView.findViewById(R.id.task_title_accordion_card_view);
            descTV = itemView.findViewById(R.id.description_accordion_card_view);
            cardViewAccordionLower = itemView.findViewById(R.id.relative_layout_accordion_card_view_lower);
            downArrow = itemView.findViewById(R.id.img_down_arrow_accordion_card_view);
            editBtn = itemView.findViewById(R.id.img_edit_task_accordion_card_view);
            deleteBtn = itemView.findViewById(R.id.img_delete_task_accordion_card_view);
            finishBtn = itemView.findViewById(R.id.img_finish_task_accordion_card_view);

            //Using the RecyclerViewItemButtonsClickListeners interface methods so that desired result is generated upon clicking the items/buttons.

            itemView.setOnClickListener(view -> {
                recyclerViewItemButtonsClickListeners.onItemClick(getAdapterPosition());
            });

            itemView.setOnClickListener(view -> {
                recyclerViewItemButtonsClickListeners.onLongItemClick(getAdapterPosition());
            });

            editBtn.setOnClickListener(view -> {
                recyclerViewItemButtonsClickListeners.onEditBtnClick(getAdapterPosition());
            });

            deleteBtn.setOnClickListener(view -> {
                recyclerViewItemButtonsClickListeners.onDeleteBtnClick(getAdapterPosition());
            });

            finishBtn.setOnClickListener(view -> {
                recyclerViewItemButtonsClickListeners.onFinishBtnClick(getAdapterPosition());
            });

        }
    }
}
