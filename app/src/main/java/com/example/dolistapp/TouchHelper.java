package com.example.dolistapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dolistapp.Adapter.ToDoAdapter;

public class TouchHelper extends ItemTouchHelper.SimpleCallback {
    private ToDoAdapter adapter;
    private Drawable deleteIcon;
    private Drawable editIcon;
    private int iconSize;
    private float textSize;

    public TouchHelper(ToDoAdapter adapter) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter = adapter;
        this.deleteIcon = ContextCompat.getDrawable(adapter.getContext(), R.drawable.baseline_delete_24);
        this.editIcon = ContextCompat.getDrawable(adapter.getContext(), R.drawable.baseline_edit_24);
        this.iconSize = (int) adapter.getContext().getResources().getDimension(R.dimen.icon_size);
        this.textSize = adapter.getContext().getResources().getDimension(R.dimen.text_size);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        final int position = viewHolder.getAdapterPosition();
        if (direction == ItemTouchHelper.RIGHT) {
            showDeleteConfirmationDialog(position);
        } else {
            adapter.editTask(position);
        }
    }

    private void showDeleteConfirmationDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(adapter.getContext());
        builder.setMessage("Are you sure you want to delete this task?")
                .setTitle("Delete Task")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapter.deleteTask(position);
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapter.notifyItemChanged(position);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View itemView = viewHolder.itemView;
        Paint paint = new Paint();
        float iconMargin = (itemView.getHeight() - iconSize) / 2.0f;

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (dX > 0) { // Swiping to the right (delete)
                paint.setColor(Color.RED);
                c.drawRect(itemView.getLeft(), itemView.getTop(), dX, itemView.getBottom(), paint);
                deleteIcon.setBounds(
                        itemView.getLeft() + (int) iconMargin,
                        itemView.getTop() + (int) iconMargin,
                        itemView.getLeft() + (int) iconMargin + iconSize,
                        itemView.getBottom() - (int) iconMargin
                );
            } else if (dX < 0) { // Swiping to the left (edit)
                paint.setColor(ContextCompat.getColor(adapter.getContext(), R.color.dark_blue));
                c.drawRect(itemView.getRight() + dX, itemView.getTop(), itemView.getRight(), itemView.getBottom(), paint);
                editIcon.setBounds(
                        itemView.getRight() - (int) iconMargin - iconSize,
                        itemView.getTop() + (int) iconMargin,
                        itemView.getRight() - (int) iconMargin,
                        itemView.getBottom() - (int) iconMargin
                );
            }

            paint.setColor(Color.WHITE); // Set the paint color for text
            paint.setTextSize(textSize);
            String text = (dX > 0) ? "Delete" : "Edit";
            float textWidth = paint.measureText(text);

            // Draw the text centered vertically on the item
            float textX = (itemView.getRight() + itemView.getLeft() - textWidth) / 2.0f;
            float textY = itemView.getTop() + (itemView.getHeight() + textSize) / 2.0f;
            c.drawText(text, textX, textY, paint);

            if (dX > 0) {
                deleteIcon.draw(c);
            } else {
                editIcon.draw(c);
            }
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
