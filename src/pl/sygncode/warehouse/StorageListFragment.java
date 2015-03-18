package pl.sygncode.warehouse;


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
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;

public class StorageListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private Adapter adapter;

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
        setHasOptionsMenu(true);

        adapter = new Adapter(getActivity());
        setListAdapter(adapter);

        setListShown(false);

        getLoaderManager().initLoader(Res.LOADER_STORAGE, null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        MenuItem add = menu.add("Dodaj Magazyn");
        add.setOnMenuItemClickListener(new ActionAddStorage(getActivity()) {
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

        Uri uri = WarehouseContentProvider.storage(0);

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
