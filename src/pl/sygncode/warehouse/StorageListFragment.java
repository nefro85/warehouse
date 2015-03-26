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
import android.widget.SearchView;
import android.widget.SearchView.OnCloseListener;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
                            Storage.NAME,
                            Storage.FLAG,
                            Storage.COUNT,

                    }, new int[]{
                            R.id.tvName,
                            R.id.lbName,
                            R.id.tvCount
                    });
            setViewBinder(this);
        }

        @Override
        public boolean setViewValue(View view, Cursor cursor, int i) {

            if (view.getId() == R.id.tvName) {
                final TextView tv = (TextView) view;

                int superStorageId = cursor.getInt(cursor.getColumnIndexOrThrow(Storage.SUPER_ID));
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(Storage.ID));
                int flag = cursor.getInt(cursor.getColumnIndexOrThrow(Storage.FLAG));

                if ((flag & Storage.FLAG_ITEM) == Storage.FLAG_ITEM) {

                    Cursor c = getActivity().getContentResolver().query(WarehouseContentProvider.itemByStorage(id), Item.PROJ, null, null, null);
                    if (c.moveToFirst()) {
                        String name = c.getString(c.getColumnIndexOrThrow(Item.NAME));
                        tv.setText(name);
                    }
                    c.close();

                } else {

                    if (0 != superStorageId) {
                        List<String> names = new ArrayList<String>();
                        names.add(cursor.getString(cursor.getColumnIndexOrThrow(Storage.NAME)));
                        obtainName(names, superStorageId);


                        Collections.reverse(names);
                        String name = "";

                        for (String n : names) {
                            name += n + "/";
                        }

                        tv.setText(name);
                    }
                }

                return true;

            } else if (view.getId() == R.id.lbName) {

                int flag = cursor.getInt(cursor.getColumnIndexOrThrow(Storage.FLAG));

                TextView lbl = (TextView) view;
                if ((flag & Storage.FLAG_ITEM) == Storage.FLAG_ITEM) {
                    lbl.setText("Przedmiot:");
                } else {
                    lbl.setText("Magazyn:");
                }

                return true;
            }

            return false;
        }

        void obtainName(List<String> nameList, int id) {

            int superId = 0;
            Cursor c = getActivity().getContentResolver().query(storage(id), Storage.PROJ, null, null, null);
            if (c.moveToFirst()) {

                int cIdx = c.getColumnIndex(Storage.SUPER_ID);
                superId = c.getInt(cIdx);
                nameList.add(c.getString(c.getColumnIndexOrThrow(Storage.NAME)));

            }
            c.close();

            if (superId != 0) {
                obtainName(nameList, superId);
            }
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
        add.setIcon(android.R.drawable.ic_menu_add);
        add.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        add.setOnMenuItemClickListener(new ActionAddStorage(getActivity(), superStorageId) {
            @Override
            protected Uri onAdd() {
                Uri uri = super.onAdd();

                getLoaderManager().restartLoader(Res.LOADER_STORAGE, null, StorageListFragment.this);

                return uri;
            }
        });

        MenuItem addItem = menu.add("Dodaj Przedmiot");
        addItem.setIcon(android.R.drawable.ic_input_add);
        addItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        addItem.setOnMenuItemClickListener(new ActionAddItem(getActivity(), superStorageId) {
            @Override
            protected void onAdd() {
                super.onAdd();

                getLoaderManager().restartLoader(Res.LOADER_STORAGE, null, StorageListFragment.this);
            }
        });

        MenuItem item = menu.add("Szukaj");
        item.setIcon(android.R.drawable.ic_menu_search);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM
                | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        SearchView search = new SearchView(getActivity());
        search.setOnQueryTextListener(new OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        search.setOnCloseListener(new OnCloseListener() {
            @Override
            public boolean onClose() {
                return false;
            }
        });
        search.setIconifiedByDefault(true);
        item.setActionView(search);

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
