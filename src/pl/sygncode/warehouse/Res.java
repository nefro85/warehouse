package pl.sygncode.warehouse;

import android.net.Uri;

public interface Res {

    String AUTHORITY = "pl.sygncode.warehouse.provider.warehouse";
    String CONTENT = "content://" + AUTHORITY + "/";


    int LOADER_STORAGE = 1;
}
