package com.example.android.inventoryapp;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

public class ItemCursorAdapter extends CursorAdapter{

    public ItemCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.element_item, parent, false);
    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current item can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        // Get the id of the current ListItem
        final int id = cursor.getInt ( cursor.getColumnIndex ( InventoryEntry._ID ) );

        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = view.findViewById ( R.id.item_name );
        TextView priceTextView = view.findViewById ( R.id.item_price );
        TextView quantityTextView = view.findViewById ( R.id.item_quantity );
        TextView suppNameTextView = view.findViewById ( R.id.item_supp_name );
        TextView suppPhoneTextView = view.findViewById ( R.id.item_supp_phone );


        // Find the columns of item attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
        int suppNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_NAME);
        int suppPhoneColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_PHONE);


        // Read the pet attributes from the Cursor for the current item
        String productName = cursor.getString(nameColumnIndex);
        int productPrice = cursor.getInt (priceColumnIndex);
        final int productQuantity = cursor.getInt ( quantityColumnIndex );
        String suppName = cursor.getString(suppNameColumnIndex);
        String suppPhone = cursor.getString (suppPhoneColumnIndex);


        // Get the order button view
        Button orderButton = view.findViewById ( R.id.orderButton );

        // Attach a listener to "Order" button to perform an update on the database
        orderButton.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                // Create  ContentResolver object to update the database
                ContentResolver resolver = context.getContentResolver ();

                // Create ContentValues to select the right "key" to "value" pair to update
                ContentValues values = new ContentValues ();

                // If the quantity of the products is more than 0, then we can reduce ot by one
                // We do not want any negative values
                if (productQuantity > 0) {

                    // Create a new uri for this product ( ListItem)
                    Uri CurrentProductUri = ContentUris.withAppendedId ( InventoryEntry.CONTENT_URI, id );

                    // Present a new variable to send the reduced quantity to database
                    int currentAvailableQuantity = productQuantity;
                    currentAvailableQuantity -= 1;

                    // Assign the new variable of the quantity as a contentValue,
                    // that will be updated into the database
                    values.put ( InventoryEntry.COLUMN_PRODUCT_QUANTITY, currentAvailableQuantity );

                    // Perform an update on the database
                    resolver.update (
                            CurrentProductUri,
                            values,
                            null,
                            null
                    );

                    // Notify all listener to Update the UI
                    // Now the quantity of this product is reduced on the UI
                    context.getContentResolver ().notifyChange ( CurrentProductUri, null );
                } else {
                    // Show a message to the UI to inform the user for the 0 quantity of this product
                    Toast.makeText ( v.getContext (), "This item is out of stock", Toast.LENGTH_SHORT ).show ();
                }
            }
        } );


        // Update the TextViews with the attributes for the current item
        nameTextView.setText(productName);
        priceTextView.setText(Integer.toString ( productPrice ));
        quantityTextView.setText (Integer.toString ( productQuantity) );
        suppNameTextView.setText ( suppName );
        suppPhoneTextView.setText ( suppPhone );

    }

}
