package pl.sygncode.warehouse;


import android.content.Context;
import android.view.MenuItem;

public abstract class StorageAction implements MenuItem.OnMenuItemClickListener {


    protected final Context context;
    protected final int storageId;

    public StorageAction(Context context, int storageId) {
        this.context = context;
        this.storageId = storageId;
    }
}
