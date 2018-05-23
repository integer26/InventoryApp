package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;
import com.example.android.inventoryapp.data.InventoryDbHelper;


public class MainActivity extends AppCompatActivity  implements
        LoaderManager.LoaderCallbacks<Cursor>{

    /** Identifier for the item data loader */
    private static final int ITEM_LOADER = 0;

    /** Adapter for the ListView */
    ItemCursorAdapter mCursorAdapter;


    /**
     * Database helper that will provide us access to the database
     */
    private InventoryDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_main );

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });



    // Find the ListView which will be populated with the item data
    ListView itemsListView = (ListView) findViewById(R.id.elementList);

    // Setup an Adapter to create a list item for each row of item data in the Cursor.
    // There is no item data yet (until the loader finishes) so pass in null for the Cursor.
    mCursorAdapter = new ItemCursorAdapter (this, null);
        itemsListView.setAdapter(mCursorAdapter);

    // Setup the item click listener
        itemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            // Create new intent to go to {@link EditorActivity}
            Intent intent = new Intent(MainActivity.this, EditorActivity.class);

            // Form the content URI that represents the specific pet that was clicked on,
            // by appending the "id" (passed as input to this method) onto the ItemURI.
            // if the pet with ID 2 was clicked on.
            Uri currentPetUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);

            // Set the URI on the data field of the intent
            intent.setData(currentPetUri);

            // Launch the {@link EditorActivity} to display the data for the current item.
            startActivity(intent);
        }
    });

    // Kick off the loader
    getLoaderManager().initLoader(ITEM_LOADER, null, this);
}


    /**
     * Helper method to insert hardcoded Item data into the database.
     * This because it is specified that I must not build an interface
     * for the app so I use this to write an item
     */
    private void insertItem() {

        ContentValues values = new ContentValues ();
        values.put ( InventoryEntry.COLUMN_PRODUCT_NAME, "TV" );
        values.put ( InventoryEntry.COLUMN_PRODUCT_PRICE, 199 );
        values.put ( InventoryEntry.COLUMN_PRODUCT_QUANTITY, 3 );
        values.put ( InventoryEntry.COLUMN_SUPPLIER_NAME, "SamZon International" );
        values.put ( InventoryEntry.COLUMN_SUPPLIER_PHONE, "+00 111.222.333.4" );

        // Insert a new row for Toto into the provider using the ContentResolver.
        // Use the {@link PetEntry#CONTENT_URI} to indicate that we want to insert
        // into the pets database table.
        // Receive the new content URI that will allow us to access TV's data in the future.
        Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);


    }

    /**
     * Helper method to delete all item in the database.
     */
    private void deleteAllInventory() {
        int rowsDeleted = getContentResolver().delete(InventoryEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from inventory database");
    }


    @Override
    // This method initialize the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action1_settings:
                insertItem ();
                return true;
            // Respond to a click on the "Delete all" menu option
            case R.id.action2_settings:
                deleteAllInventory ();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryEntry.COLUMN_PRODUCT_QUANTITY,
                InventoryEntry.COLUMN_SUPPLIER_NAME,
                InventoryEntry.COLUMN_SUPPLIER_PHONE
        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader (this,   // Parent activity context
                InventoryEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update {@link PetCursorAdapter} with this new cursor containing updated item data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }


}
