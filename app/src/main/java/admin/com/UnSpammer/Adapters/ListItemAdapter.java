package admin.com.UnSpammer.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import admin.com.UnSpammer.R;
import admin.com.UnSpammer.DataBaseHelpers.ListsContract;

public class ListItemAdapter extends CursorRecyclerViewAdapter<ListItemAdapter.ViewHolder> {

    private Cursor mCursor;
    private Context mContext;

    public ListItemAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        mContext = context;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        TextView textViewName = viewHolder.tvName;
        TextView textViewNumber = viewHolder.tvNumber;
        View viewDivider = viewHolder.divider;

        mCursor = cursor;
        mCursor.moveToFirst();
        mCursor.moveToPosition(viewHolder.getAdapterPosition());

        String number = mCursor.getString(mCursor.getColumnIndex(ListsContract.BlackListEntry.COLUMN_NUMBER));
        String name = mCursor.getString(mCursor.getColumnIndex(ListsContract.BlackListEntry.COLUMN_NAME));

        textViewName.setText(name);
        textViewName.setContentDescription(mContext.getResources().getString(R.string.row_item_name_content_desc_prefix)+ name);
        textViewNumber.setText(number);
        textViewNumber.setContentDescription(mContext.getResources().getString(R.string.row_item_number_content_desc_prefix)+ number);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View rowItemView = inflater.inflate(R.layout.row_item, parent, false);

        return new ViewHolder(rowItemView);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tvName;
        public TextView tvNumber;
        public View divider;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.item_name);
            tvNumber = (TextView) itemView.findViewById(R.id.item_number);
            divider = itemView.findViewById(R.id.item_divider);
        }
    }
    // Fucntion to remove all the contacts that are added
    public void remove(int position) {
        Cursor c = getCursor();
        if (c.getCount() == 1) {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.all_numbers_deleted), Toast.LENGTH_LONG).show();
        }
        c.moveToPosition(position);
        String number = c.getString(c.getColumnIndex(ListsContract.BlackListEntry.COLUMN_NUMBER));
        String delete[] = new String[1];
        delete[0] = number;
        mContext.getContentResolver().delete(ListsContract.BlackListEntry.CONTENT_URI,
                ListsContract.BlackListEntry.COLUMN_NUMBER + " = ?",
                delete);
        notifyItemRemoved(position);
    }
}
