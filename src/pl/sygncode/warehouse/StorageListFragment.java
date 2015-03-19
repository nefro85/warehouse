package pl.sygncode.warehouse;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;

import static pl.sygncode.warehouse.WarehouseContentProvider.storage;
import static pl.sygncode.warehouse.WarehouseContentProvider.storageChildren;

public class StorageListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {


    public static final String ARG_STORAGE_ID = "storageId";

    public static Fragment fragmentOf(int storageId) {

        Bundle bundle = new Bundle();
        bundle.putInt(ARG_STORAGE_ID, storageId);
        Fragment fragment = new StorageListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private Adapter adapter;
    private int superStorageId;

    private class Adapter extends SimpleCursorAdapter implements ViewBinder {

        public Adapter(Context context) {
            super(context, R.layout.storage_list_item, null,
                    new String[]{
                            Storage.NAME

                    }, new int[]{
                            R.id.tvName
                    });
            setViewBinder(this);
        }

        @Override
        public boolean setViewValue(View view, Cursor cursor, int i) {
            return false;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle args = getArguments();
        if (args != null && args.containsKey(ARG_STORAGE_ID)) {
            superStorageId = args.getInt(ARG_STORAGE_ID);
        }


        setHasOptionsMenu(true);

        adapter = new Adapter(getActivity());
        setListAdapter(adapter);

        setListShown(false);

        getLoaderManager().initLoader(Res.LOADER_STORAGE, null, this);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Cursor c = adapter.getCursor();
        int columnIndex = c.getColumnIndex(Storage.ID);

        FragmentTransaction tx = getFragmentManager().beginTransaction();
        tx.replace(R.id.main_frame, fragmentOf(c.getInt(columnIndex)));
        tx.addToBackStack(null);
        tx.commit();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        MenuItem add = menu.add("Dodaj Magazyn");
        add.setOnMenuItemClickListener(new ActionAddStorage(getActivity(), superStorageId) {
            @Override
            protected Uri onAdd() {
                Uri uri = super.onAdd();

                getLoaderManager().restartLoader(Res.LOADER_STORAGE, null, StorageListFragment.this);

                return uri;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        Uri uri = superStorageId == 0 ? storage(0) : storageChildren(superStorageId);

        return new CursorLoader(getActivity(), uri, Storage.PROJ, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.swapCursor(cursor);

        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
