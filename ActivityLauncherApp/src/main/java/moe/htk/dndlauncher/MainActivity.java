package moe.htk.dndlauncher;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.SearchView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import org.thirdparty.LauncherIconCreator;

import de.szalkowski.activitylauncher.AllTasksListFragment;
import de.szalkowski.activitylauncher.DisclaimerDialogFragment;
import moe.htk.dndmode.DNDHandler;
import moe.htk.dndmode.DNDService;

public class MainActivity extends FragmentActivity {

    //private final String LOG = "moe.htk.dndlauncher.MainActivity";
    private Filterable filterTarget = null;
    public static boolean gameOnly = true;
    private static boolean opOnResume = true;

    private AllTasksListFragment games, all;
    private static boolean populated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DNDHandler.mContext = getApplicationContext();
        DNDHandler.mNotificationManager = (NotificationManager) DNDHandler.mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        Bundle data = this.getIntent().getExtras();
        if (data != null) {
            Log.d("DNDL-extras", String.valueOf(data.size()));
            if (data.containsKey("target_pkg")) {
                ComponentName componentName = new ComponentName((String) data.get("target_pkg"), (String) data.get("target_cmp"));
                LauncherIconCreator.launchActivity(getApplicationContext(), componentName);
                DNDHandler.enableDND();
                opOnResume = false;
            }
        }

        setContentView(R.layout.activity_main);

        if (!getPreferences(Context.MODE_PRIVATE).getBoolean("disclaimer_accepted", false)) {
            DialogFragment dialog = new DisclaimerDialogFragment();
            dialog.show(getSupportFragmentManager(), "DisclaimerDialogFragment");
        }

        games = new AllTasksListFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, games).commit();
        filterTarget = games;

        DNDHandler.initMonitor(getApplicationContext());
        if (!DNDHandler.checkPermission(getApplicationContext())) {
            Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            this.startActivity(intent);
        }
        DNDHandler.saveNotiMode();
        //startService(new Intent(getBaseContext(), DNDService.class));
        //Intent serviceIntent = new Intent(this, DNDService.class);

        //ContextCompat.startForegroundService(this, serviceIntent);
    }

    /**
     * Backward-compatible version of {@link ActionBar#getThemedContext()} that
     * simply returns the {@link android.app.Activity} if
     * <code>getThemedContext</code> is unavailable.
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private Context getActionBarThemedContextCompat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return getActionBar().getThemedContext();
        } else {
            return this;
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current dropdown position.
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setQueryHint(this.getText(R.string.filter_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                onFilter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                onFilter(newText);
                return true;
            }
        });

        return true;
    }

    private void onFilter(String query) {
        Filter filter = filterTarget.getFilter();
        if (filter != null) {
            filter.filter(query);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_games_only:
                if (!populated && this.gameOnly) {
                    this.gameOnly = !this.gameOnly;
                    all = new AllTasksListFragment();
                } else { this.gameOnly = !this.gameOnly; }
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, (this.gameOnly ? games : all)).commit();
                item.setChecked(this.gameOnly);
                return true;
            case R.id.action_view_source:
                Intent i2 = new Intent(Intent.ACTION_VIEW);
                i2.setData(Uri.parse(this.getString(R.string.url_source)));
                this.startActivity(i2);
                return true;

            case R.id.action_view_translation:
                Intent i3 = new Intent(Intent.ACTION_VIEW);
                i3.setData(Uri.parse(this.getString(R.string.url_translation)));
                this.startActivity(i3);
                return true;

            case R.id.action_view_bugs:
                Intent i4 = new Intent(Intent.ACTION_VIEW);
                i4.setData(Uri.parse(this.getString(R.string.url_bugs)));
                this.startActivity(i4);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current dropdown position.
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        //DNDHandler.saveNotiMode();
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            DNDHandler.mContext = getApplicationContext();
            if (opOnResume) {
                DNDHandler.loadNotiMode();
                stopService(new Intent(getBaseContext(), DNDService.class));
            }
        } catch (Exception e) {
            Log.e("DNDL-onResume", e.toString());
            //Toast.makeText(this, R.string.error_dnd_denied, Toast.LENGTH_LONG).show();
        }
    }
}
