package pl.sygncode.warehouse;


import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class ActionAddItem extends StorageAction {


    public ActionAddItem(Context context, int storageId) {
        super(context, storageId);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {

        Builder b = new Builder(context);
        b.setTitle("Dodaj przedmiot");
        b.setCancelable(false);

        final View v = LayoutInflater.from(context).inflate(R.layout.item_layout, null);
        b.setView(v);

        b.setPositiveButton("Dodaj", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                EditText teName = (EditText) v.findViewById(R.id.etName);
                EditText teCount = (EditText) v.findViewById(R.id.etCount);

                ContentValues values = new ContentValues();
                if (storageId != 0) {
                    values.put(Storage.SUPER_ID, storageId);
                }
                values.put(Storage.COUNT, Integer.valueOf(teCount.getText().toString()));
                values.put(Storage.FLAG, Storage.FLAG_ITEM);

                Uri storageUri = context.getContentResolver().insert(WarehouseContentProvider.STORAGE, values);

                storeItem(storageUri.getLastPathSegment(), teName.getText().toString());
                onAdd();

            }
        });
        b.setNegativeButton("Anuluj", null);
        b.show();

        return true;
    }

    protected void onAdd() {

    }

    private void storeItem(String storage, String name) {
        ContentValues values = new ContentValues();
        values.put(Item.NAME, name);
        values.put(Item.STORAGE_ID, storage);

        context.getContentResolver().insert(WarehouseContentProvider.ITEM, values);
    }

}
