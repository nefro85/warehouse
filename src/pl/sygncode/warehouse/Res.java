package pl.sygncode.warehouse;

import android.net.Uri;

public interface Res {

    String ROW_ID = "_ID";


    String AUTHORITY = "pl.sygncode.warehouse.provider.warehouse";
    String CONTENT = "content://" + AUTHORITY + "/";
    Uri ITEM = Uri.parse(CONTENT + "item");
    Uri STORAGE = Uri.parse(CONTENT + "storage");

    int LOADER_STORAGE = 1;
}
