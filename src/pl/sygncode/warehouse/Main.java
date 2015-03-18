package pl.sygncode.warehouse;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
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

}
