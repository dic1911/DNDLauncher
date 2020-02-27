package de.szalkowski.activitylauncher;

import android.content.Context;
import android.util.Log;

import moe.htk.dndlauncher.MainActivity;

public class AllTasksListAsyncProvider extends AsyncProvider<AllTasksListAdapter> {
    private AllTasksListAdapter adapter;
    private Context context;

    AllTasksListAsyncProvider(
            Context context,
            de.szalkowski.activitylauncher.AsyncProvider.Listener<AllTasksListAdapter> listener) {
        //super(context, listener, true);
        super(context, listener, false);
        this.adapter = new AllTasksListAdapter(context);
        this.context = context;
    }

    @Override
    protected AllTasksListAdapter run(Updater updater) {
        this.adapter.resolve(updater);

        if (MainActivity.gameOnly) {
            Log.d("DNDL-saved", String.valueOf(AllTasksListAdapter.pkgNames.size()));
            if (!this.context.getSharedPreferences("default", Context.MODE_PRIVATE).getBoolean("list_saved", false)) {
                Log.d("DNDL-saver", "Attempt to save pkg list");
                StringBuilder sb = new StringBuilder();
                for (String pkgname : AllTasksListAdapter.pkgNames) {
                    if (sb.length() != 0) sb.append(",");
                    sb.append(pkgname);
                }
                Log.d("DNDL-saver", sb.toString());
                this.context.getSharedPreferences("default", Context.MODE_PRIVATE).edit().putString("list", sb.toString()).apply();
                this.context.getSharedPreferences("default", Context.MODE_PRIVATE).edit().putBoolean("list_saved", true).commit();
                //this.context.getSharedPreferences("default", Context.MODE_PRIVATE).edit().commit();
            }
        }
        return this.adapter;
    }
}
