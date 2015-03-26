package pl.sygncode.warehouse;


import android.app.*;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.*;
import android.widget.SearchView.OnCloseListener;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.SimpleCursorAdapter.ViewBinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static pl.sygncode.warehouse.WarehouseContentProvider.*;

public class StorageListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {


    public static final String ARG_STORAGE_ID = "storageId";

    public static Fragment fragmentOf(int storageId) {

        Bundle bundle = new Bundle();
        bundle.putInt(ARG_STORAGE_ID, storageId);
        Fragment fragment = new StorageListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private static boolean isItem(Cursor cursor) {
        int flag = cursor.getInt(cursor.getColumnIndexOrThrow(Storage.FLAG));
        return (flag & Storage.FLAG_ITEM) == Storage.FLAG_ITEM;
    }


    private Adapter adapter;
    private int superStorageId;
    private String search;
    private Uri mainUri;

    private static class Adapter extends SimpleCursorAdapter implements ViewBinder {

        private final Activity activity;

        public Adapter(Activity context) {
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
            activity = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final View view = super.getView(position, convertView, parent);

            Cursor cursor = getCursor();
            boolean isItem = isItem(cursor);
            int typeIcon = isItem ? R.drawable.ic_item : R.drawable.ic_storage;
            ImageView type = (ImageView) view.findViewById(R.id.icType);
            type.setImageResource(typeIcon);

            int superStorageId = cursor.getInt(cursor.getColumnIndexOrThrow(Storage.SUPER_ID));

            TextView loc = (TextView) view.findViewById(R.id.tvLoc);
            loc.setText(isItem ? getStorageName(cursor, superStorageId) : digName(superStorageId, null));


            return view;
        }

        @Override
        public boolean setViewValue(View view, Cursor cursor, int i) {

            if (view.getId() == R.id.tvName) {
                final TextView tv = (TextView) view;

                int id = cursor.getInt(cursor.getColumnIndexOrThrow(Storage.ID));

                if (isItem(cursor)) {

                    Cursor c = activity.getContentResolver().query(WarehouseContentProvider.itemByStorage(id), Item.PROJ, null, null, null);
                    if (c.moveToFirst()) {
                        String name = c.getString(c.getColumnIndexOrThrow(Item.NAME));

                        tv.setText(name);
                    }
                    c.close();

                } else {

                    String name = cursor.getString(cursor.getColumnIndexOrThrow(Storage.NAME));
                    tv.setText(name);
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

        private String getStorageName(Cursor cursor, int storageId) {
            List<String> names = new ArrayList<String>();
            names.add(cursor.getString(cursor.getColumnIndexOrThrow(Storage.NAME)));
            String name = digName(storageId, names);
            return name;
        }

        private String digName(int storageId, List<String> names) {
            if (names == null) {
                names = new ArrayList<String>();
            }
            if (storageId != 0) {
                obtainName(names, storageId);
            }


            Collections.reverse(names);
            String name = "";

            for (Iterator<String> it = names.iterator(); it.hasNext(); ) {
                String n = it.next();
                if (n != null) {
                    name += n + (it.hasNext() ? "/" : "");
                }
            }
            return name;
        }

        void obtainName(List<String> nameList, int id) {

            int superId = 0;
            Cursor c = activity.getContentResolver().query(storage(id), Storage.PROJ, null, null, null);
            if (c.moveToFirst()) {

                int cIdx = c.getColumnIndex(Storage.SUPER_ID);
                superId = c.getInt(cIdx);
                String name = c.getString(c.getColumnIndexOrThrow(Storage.NAME));
                if (name != null) {
                    nameList.add(name);
                }
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

        if (!isItem(c)) {

            int columnIndex = c.getColumnIndex(Storage.ID);

            FragmentTransaction tx = getFragmentManager().beginTransaction();
            tx.replace(R.id.main_frame, fragmentOf(c.getInt(columnIndex)));
            tx.addToBackStack(null);
            tx.commit();
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        new ActionAddStorage(getActivity(), superStorageId) {
            @Override
            protected Uri onAdd() {
                Uri uri = super.onAdd();

                getLoaderManager().restartLoader(Res.LOADER_STORAGE, null, StorageListFragment.this);

                return uri;
            }
        }.attach(menu);

        if (superStorageId != 0) {
            new ActionAddItem(getActivity(), superStorageId) {
                @Override
                protected void onAdd() {
                    super.onAdd();

                    getLoaderManager().restartLoader(Res.LOADER_STORAGE, null, StorageListFragment.this);
                }
            }.attach(menu);
        }

        MenuItem item = menu.add("Szukaj");
        item.setIcon(android.R.drawable.ic_menu_search);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        SearchView searchView = new SearchView(getActivity());
        searchView.setOnQueryTextListener(new OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                search = s;
                getLoaderManager().restartLoader(Res.LOADER_STORAGE, null, StorageListFragment.this);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                search = s;
                getLoaderManager().restartLoader(Res.LOADER_STORAGE, null, StorageListFragment.this);
                return false;
            }
        });
        searchView.setOnCloseListener(new OnCloseListener() {
            @Override
            public boolean onClose() {
                search = null;
                getLoaderManager().restartLoader(Res.LOADER_STORAGE, null, StorageListFragment.this);
                return true;
            }
        });
        searchView.setIconifiedByDefault(true);
        item.setActionView(searchView);


        MenuItem delete = menu.add("Skasuj");
        delete.setIcon(android.R.drawable.ic_menu_delete);

        delete.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                AlertDialog.Builder b = new Builder(getActivity());
                b.setTitle("Skasuj");

                Adapter adapter = new Adapter(getActivity());
                final ContentResolver cr = getActivity().getContentResolver();
                final Cursor cursor = cr.query(mainUri, Storage.PROJ, null, null, null);
                adapter.swapCursor(cursor);

                b.setAdapter(adapter, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        long id = cursor.getLong(cursor.getColumnIndexOrThrow(Storage.ID));
                        cr.delete(storage(id), null, null);

                        getLoaderManager().restartLoader(Res.LOADER_STORAGE, null, StorageListFragment.this);
                    }
                });
                b.show();

                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        if (!TextUtils.isEmpty(search)) {
            mainUri = search(search);
        } else {

            mainUri = superStorageId == 0 ? storage(0) : storageChildren(superStorageId);
        }

        return new CursorLoader(getActivity(), mainUri, Storage.PROJ, null, null, null);
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
