package com.example.android.inventoryapp.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class InventoryContract {

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryapp";
    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse ( "content://" + CONTENT_AUTHORITY );
    /**
     * Possible path
     */
    public static final String PATH_ITEM = "items";

    private InventoryContract() {
    }

    public static final class InventoryEntry implements BaseColumns {


        /**
         * The content URI to access the items data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath ( BASE_CONTENT_URI, PATH_ITEM );


        public final static String _ID = BaseColumns._ID;

        /**
         * Name of database table for inventory
         */
        public final static String TABLE_NAME = "Inventory";

        public final static String COLUMN_PRODUCT_NAME = "name";

        public final static String COLUMN_PRODUCT_PRICE = "price";

        public final static String COLUMN_PRODUCT_QUANTITY = "quantity";

        public final static String COLUMN_SUPPLIER_NAME = "suppName";

        public final static String COLUMN_SUPPLIER_PHONE = "suppPhone";


    }
}
