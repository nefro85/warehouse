package pl.sygncode.warehouse;


import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.EditText;

public class ActionAddStorage implements OnMenuItemClickListener, OnClickListener {

    private EditText etName;
    private final Context context;
    private final int superStorageId;

    public ActionAddStorage(Context context, int superStorageId) {
        this.context = context;
        this.superStorageId = superStorageId;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == DialogInterface.BUTTON_POSITIVE) {
            onAdd();
        }
    }

    protected Uri onAdd() {
        ContentValues values = new ContentValues();
        values.put(Storage.NAME, etName.getText().toString());
        if (superStorageId != 0) {
            values.put(Storage.SUPER_ID, superStorageId);
        }

        return context.getContentResolver().insert(WarehouseContentProvider.STORAGE, values);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        AlertDialog.Builder bld = new AlertDialog.Builder(context);

        bld.setTitle("Dodaj Nowy Magazyn");
        etName = new EditText(context);
        bld.setView(etName);
        bld.setPositiveButton(android.R.string.ok, this);
        bld.setNegativeButton(android.R.string.cancel, null);

        bld.show();
        return true;
    }
}
