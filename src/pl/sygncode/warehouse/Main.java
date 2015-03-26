package pl.sygncode.warehouse;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

public class Main extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        nav(new StorageListFragment());
    }


    protected void nav(Fragment fragment) {
        FragmentTransaction tx = getFragmentManager().beginTransaction();

        tx.replace(R.id.main_frame, fragment);

        tx.commit();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {

            Builder b = new Builder(this);
            b.setMessage("Chcesz wyjść z aplikacji?");
            b.setPositiveButton(android.R.string.yes, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Main.super.onBackPressed();
                }
            });
            b.setNegativeButton(android.R.string.no, null);
            b.show();
        } else {

            super.onBackPressed();
        }

    }
}
