package com.example.android.inventoryapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

    /**
     * Allows user to create a new pet or edit an existing one.
     */
    public class EditorActivity extends AppCompatActivity implements
            LoaderManager.LoaderCallbacks<Cursor> {

        /** Identifier for the pet data loader */
        private static final int EXISTING_ITEM_LOADER = 0;

        /** Content URI for the existing pet (null if it's a new pet) */
        private Uri mCurrentitemUri;

        /** EditText field to enter the pet's name */
        private EditText mNameEditText;

        /** EditText field to enter the pet's breed */
        private EditText mPriceEditText;

        /** EditText field to enter the pet's weight */
        private EditText mquantityEditText;

        /** EditText field to enter the pet's gender */
        private EditText mSuppNameEditText;

        /** EditText field to enter the pet's gender */
        private EditText mSuppPhoneEditText;


        /** Boolean flag that keeps track of whether the pet has been edited (true) or not (false) */
        private boolean mItemHasChanged = false;

        /**
         * OnTouchListener that listens for any user touches on a View, implying that they are modifying
         * the view, and we change the mPetHasChanged boolean to true.
         */
        private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mItemHasChanged = true;
                return false;
            }
        };

        @SuppressLint("ClickableViewAccessibility")
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_editor);

            // Examine the intent that was used to launch this activity,
            // in order to figure out if we're creating a new pet or editing an existing one.
            Intent intent = getIntent();
            mCurrentitemUri = intent.getData();

            // If the intent DOES NOT contain a pet content URI, then we know that we are
            // creating a new pet.
            if (mCurrentitemUri == null) {
                // This is a new pet, so change the app bar to say "Add a Pet"
                setTitle("New Item");

                // Invalidate the options menu, so the "Delete" menu option can be hidden.
                // (It doesn't make sense to delete a pet that hasn't been created yet.)
                invalidateOptionsMenu();
            } else {
                // Otherwise this is an existing pet, so change app bar to say "Edit Pet"
                setTitle("Edit Item");

                // Initialize a loader to read the pet data from the database
                // and display the current values in the editor
                getLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);
            }

            // Find all relevant views that we will need to read user input from
            mNameEditText = (EditText) findViewById(R.id.EditProdName);
            mPriceEditText = (EditText) findViewById(R.id.EditProdPrice);
            mquantityEditText = (EditText) findViewById(R.id.EditProdQuantity);
            mSuppNameEditText = (EditText) findViewById(R.id.EditSuppName);
            mSuppPhoneEditText = (EditText) findViewById(R.id.EditSuppPhone);

            // Setup OnTouchListeners on all the input fields, so we can determine if the user
            // has touched or modified them. This will let us know if there are unsaved changes
            // or not, if the user tries to leave the editor without saving.
            mNameEditText.setOnTouchListener(mTouchListener);
            mPriceEditText.setOnTouchListener(mTouchListener);
            mquantityEditText.setOnTouchListener(mTouchListener);
            mSuppNameEditText.setOnTouchListener(mTouchListener);
            mSuppPhoneEditText.setOnTouchListener(mTouchListener);

        }


        /**
         * Get user input from editor and save pet into database.
         */
        private void savePet() {
            // Read from input fields
            // Use trim to eliminate leading or trailing white space
            String nameString = mNameEditText.getText().toString().trim();
            String priceString = mPriceEditText.getText().toString().trim();
            String quantityString = mquantityEditText.getText().toString().trim();
            String SuppNameString = mSuppNameEditText.getText().toString().trim();
            String SuppPhoneString = mSuppPhoneEditText.getText().toString().trim();

            // Check if this is supposed to be a new pet
            // and check if all the fields in the editor are blank
            if (mCurrentitemUri == null &&
                    TextUtils.isEmpty(nameString) &&
                    TextUtils.isEmpty(priceString) &&
                    TextUtils.isEmpty(quantityString) &&
                    TextUtils.isEmpty(SuppNameString) &&
                    TextUtils.isEmpty(SuppPhoneString)) {
                // Since no fields were modified, we can return early without creating a new pet.
                // No need to create ContentValues and no need to do any ContentProvider operations.
                return;
            }

            // Create a ContentValues object where column names are the keys,
            // and pet attributes from the editor are the values.
            ContentValues values = new ContentValues();
            values.put(InventoryEntry.COLUMN_PRODUCT_NAME, nameString);
            values.put(InventoryEntry.COLUMN_PRODUCT_PRICE, priceString);
            values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, quantityString);
            values.put(InventoryEntry.COLUMN_SUPPLIER_NAME, SuppNameString);
            values.put(InventoryEntry.COLUMN_SUPPLIER_PHONE, SuppPhoneString);

            // Determine if this is a new or existing pet by checking if mCurrentPetUri is null or not
            if (mCurrentitemUri == null) {
                // This is a NEW pet, so insert a new pet into the provider,
                // returning the content URI for the new pet.
                Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

                // Show a toast message depending on whether or not the insertion was successful.
                if (newUri == null) {
                    // If the new content URI is null, then there was an error with insertion.
                    Toast.makeText(this, getString(R.string.editor_insert_item_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the insertion was successful and we can display a toast.
                    Toast.makeText(this, getString(R.string.editor_insert_item_successful),
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                // Otherwise this is an EXISTING pet, so update the pet with content URI: mCurrentPetUri
                // and pass in the new ContentValues. Pass in null for the selection and selection args
                // because mCurrentPetUri will already identify the correct row in the database that
                // we want to modify.
                int rowsAffected = getContentResolver().update(mCurrentitemUri, values, null, null);

                // Show a toast message depending on whether or not the update was successful.
                if (rowsAffected == 0) {
                    // If no rows were affected, then there was an error with the update.
                    Toast.makeText(this, getString(R.string.editor_update_item_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the update was successful and we can display a toast.
                    Toast.makeText(this, getString(R.string.editor_update_item_successful),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu options from the res/menu/menu_editor.xml file.
            // This adds menu items to the app bar.
            getMenuInflater().inflate(R.menu.action, menu);
            return true;
        }

        /**
         * This method is called after invalidateOptionsMenu(), so that the
         * menu can be updated (some menu items can be hidden or made visible).
         */
        @Override
        public boolean onPrepareOptionsMenu(Menu menu) {
            super.onPrepareOptionsMenu(menu);
            // If this is a new pet, hide the "Delete" menu item.
            if (mCurrentitemUri == null) {
                MenuItem menuItem = menu.findItem(R.id.action_delete);
                menuItem.setVisible(false);
            }
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // User clicked on a menu option in the app bar overflow menu
            switch (item.getItemId()) {
                // Respond to a click on the "Save" menu option
                case R.id.action_save:
                    // Save pet to database
                    savePet();
                    // Exit activity
                    finish();
                    return true;
                // Respond to a click on the "Delete" menu option
                case R.id.action_delete:
                    // Pop up confirmation dialog for deletion
                    showDeleteConfirmationDialog();
                    return true;
                // Respond to a click on the "Up" arrow button in the app bar
                case android.R.id.home:
                    // If the pet hasn't changed, continue with navigating up to parent activity
                    // which is the {@link CatalogActivity}.
                    if (!mItemHasChanged) {
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                        return true;
                    }

                    // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                    // Create a click listener to handle the user confirming that
                    // changes should be discarded.
                    DialogInterface.OnClickListener discardButtonClickListener =
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // User clicked "Discard" button, navigate to parent activity.
                                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                                }
                            };

                    // Show a dialog that notifies the user they have unsaved changes
                    showUnsavedChangesDialog(discardButtonClickListener);
                    return true;
            }
            return super.onOptionsItemSelected(item);
        }

        /**
         * This method is called when the back button is pressed.
         */
        @Override
        public void onBackPressed() {
            // If the pet hasn't changed, continue with handling back button press
            if (!mItemHasChanged) {
                super.onBackPressed();
                return;
            }

            // Otherwise if there are unsaved changes, setup a dialog to warn the user.
            // Create a click listener to handle the user confirming that changes should be discarded.
            DialogInterface.OnClickListener discardButtonClickListener =
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // User clicked "Discard" button, close the current activity.
                            finish();
                        }
                    };

            // Show dialog that there are unsaved changes
            showUnsavedChangesDialog(discardButtonClickListener);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
            // Since the editor shows all pet attributes, define a projection that contains
            // all columns from the pet table
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
                    mCurrentitemUri,         // Query the content URI for the current pet
                    projection,             // Columns to include in the resulting Cursor
                    null,                   // No selection clause
                    null,                   // No selection arguments
                    null);                  // Default sort order
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            // Bail early if the cursor is null or there is less than 1 row in the cursor
            if (cursor == null || cursor.getCount() < 1) {
                return;
            }

            // Proceed with moving to the first row of the cursor and reading data from it
            // (This should be the only row in the cursor)
            if (cursor.moveToFirst()) {
                // Find the columns of pet attributes that we're interested in
                int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
                int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
                int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
                int suppNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_NAME);
                int suppPhoneColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_PHONE);

                // Extract out the value from the Cursor for the given column index
                String name = cursor.getString(nameColumnIndex);
                float price = cursor.getFloat (priceColumnIndex);
                int quantity = cursor.getInt(quantityColumnIndex);
                String suppName = cursor.getString (suppNameColumnIndex);
                String suppPhone = cursor.getString (suppPhoneColumnIndex);;

                // Update the views on the screen with the values from the database
                mNameEditText.setText(name);
                mPriceEditText.setText(Float.toString ( price ));
                mquantityEditText.setText(quantity);
                mSuppNameEditText.setText(suppName);
                mSuppPhoneEditText.setText(suppPhone);

            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            // If the loader is invalidated, clear out all the data from the input fields.
            mNameEditText.setText("");
            mPriceEditText.setText("");
            mquantityEditText.setText("");
            mSuppNameEditText.setText("");
            mSuppPhoneEditText.setText("");
        }

        /**
         * Show a dialog that warns the user there are unsaved changes that will be lost
         * if they continue leaving the editor.
         *
         * @param discardButtonClickListener is the click listener for what to do when
         *                                   the user confirms they want to discard their changes
         */
        private void showUnsavedChangesDialog(
                DialogInterface.OnClickListener discardButtonClickListener) {
            // Create an AlertDialog.Builder and set the message, and click listeners
            // for the postivie and negative buttons on the dialog.
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.unsaved_changes_dialog_msg);
            builder.setPositiveButton(R.string.discard, discardButtonClickListener);
            builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked the "Keep editing" button, so dismiss the dialog
                    // and continue editing the pet.
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });

            // Create and show the AlertDialog
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        /**
         * Prompt the user to confirm that they want to delete this pet.
         */
        private void showDeleteConfirmationDialog() {
            // Create an AlertDialog.Builder and set the message, and click listeners
            // for the postivie and negative buttons on the dialog.
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.delete_dialog_msg);
            builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked the "Delete" button, so delete the pet.
                    deletePet();
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked the "Cancel" button, so dismiss the dialog
                    // and continue editing the pet.
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });

            // Create and show the AlertDialog
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        /**
         * Perform the deletion of the pet in the database.
         */
        private void deletePet() {
            // Only perform the delete if this is an existing pet.
            if (mCurrentitemUri != null) {
                // Call the ContentResolver to delete the pet at the given content URI.
                // Pass in null for the selection and selection args because the mCurrentPetUri
                // content URI already identifies the pet that we want.
                int rowsDeleted = getContentResolver().delete(mCurrentitemUri, null, null);

                // Show a toast message depending on whether or not the delete was successful.
                if (rowsDeleted == 0) {
                    // If no rows were deleted, then there was an error with the delete.
                    Toast.makeText(this, getString(R.string.editor_delete_item_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the delete was successful and we can display a toast.
                    Toast.makeText(this, getString(R.string.editor_delete_item_successful),
                            Toast.LENGTH_SHORT).show();
                }
            }

            // Close the activity
            finish();
        }

}
