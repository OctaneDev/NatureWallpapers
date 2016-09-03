package biz.bigtooth.naturewallpapers;

import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.io.IOException;
import java.util.HashMap;

import biz.bigtooth.naturewallpapers.R;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    InterstitialAd mInterstitialAd;

    final private static int PERMISSION_REQUEST_CODE = 1;

    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 23)
        {
            if (checkSelfPermission(READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED) {
                Log.e("testing", "Permission is granted");
            } else {
                ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE}, 1);
                Log.e("testing", "Permission is revoked");
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.e("testing", "Permission is already granted");

        }

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-5164171001589422/5381023392");

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-5164171001589422/5381023392");

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });

        requestNewInterstitial();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_share:
                share();
                return true;
            case R.id.action_rate:
                rate();
                return true;
            case R.id.action_about:
                about();
                return true;
            case R.id.action_help:
                help();
                return true;
            case R.id.action_exit:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void contribute() {
        Intent intent = new Intent(this, ContributeActivity.class);
        startActivity(intent);
    }

    private void help() {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }

    private void about() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("About")
                .setMessage("Made proudly in America by Octane Development")
                .setIcon(R.drawable.ic_dialog_info)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void share() {
        String link = "http://play.google.com/store/apps/details?id=" + getPackageName();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Check out these awesome wallpapers! " + link);
        startActivity(intent);
    }

    private void rate() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + getPackageName()));
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("5C6759521D465119182182A234FF5CE3")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    public void blooming(View view) {

        new AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Set as your wallpaper?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    WallpaperManager myWallpaperManager
                            = WallpaperManager.getInstance(getApplicationContext());

                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                            try {
                                myWallpaperManager.setResource(R.raw.blooming);
                                Toast.makeText(MainActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(MainActivity.this,
                                        "Whoops! Something went wrong!", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                myWallpaperManager.setResource(R.raw.blooming);
                                Toast.makeText(MainActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(MainActivity.this,
                                        "Whoops! Something went wrong!", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    public void cave_pool(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Set as your wallpaper?")
                .setIcon(R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    WallpaperManager myWallpaperManager
                            = WallpaperManager.getInstance(getApplicationContext());

                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                            try {
                                myWallpaperManager.setResource(R.raw.cave_pool);
                                Toast.makeText(MainActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(MainActivity.this,
                                        "Whoops! Something went wrong!", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                myWallpaperManager.setResource(R.raw.cave_pool);
                                Toast.makeText(MainActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(MainActivity.this,
                                        "Whoops! Something went wrong!", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    public void coral(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Set as your wallpaper?")
                .setIcon(R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    WallpaperManager myWallpaperManager
                            = WallpaperManager.getInstance(getApplicationContext());

                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                            try {
                                myWallpaperManager.setResource(R.raw.coral);
                                Toast.makeText(MainActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(MainActivity.this,
                                        "Whoops! Something went wrong!", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                myWallpaperManager.setResource(R.raw.coral);
                                Toast.makeText(MainActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(MainActivity.this,
                                        "Whoops! Something went wrong!", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    public void dandy_lions(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Set as your wallpaper?")
                .setIcon(R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    WallpaperManager myWallpaperManager
                            = WallpaperManager.getInstance(getApplicationContext());

                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                            try {
                                myWallpaperManager.setResource(R.raw.dandy_lions);
                                Toast.makeText(MainActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(MainActivity.this,
                                        "Whoops! Something went wrong!", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                myWallpaperManager.setResource(R.raw.dandy_lions);
                                Toast.makeText(MainActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(MainActivity.this,
                                        "Whoops! Something went wrong!", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    public void forest(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Set as your wallpaper?")
                .setIcon(R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    WallpaperManager myWallpaperManager
                            = WallpaperManager.getInstance(getApplicationContext());

                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                            try {
                                myWallpaperManager.setResource(R.raw.forest);
                                Toast.makeText(MainActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(MainActivity.this,
                                        "Whoops! Something went wrong!", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                myWallpaperManager.setResource(R.raw.forest);
                                Toast.makeText(MainActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(MainActivity.this,
                                        "Whoops! Something went wrong!", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    public void forest_trail(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Set as your wallpaper?")
                .setIcon(R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    WallpaperManager myWallpaperManager
                            = WallpaperManager.getInstance(getApplicationContext());

                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                            try {
                                myWallpaperManager.setResource(R.raw.forest_trail);
                                Toast.makeText(MainActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(MainActivity.this,
                                        "Whoops! Something went wrong!", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                myWallpaperManager.setResource(R.raw.forest_trail);
                                Toast.makeText(MainActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(MainActivity.this,
                                        "Whoops! Something went wrong!", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    public void hills(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Set as your wallpaper?")
                .setIcon(R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    WallpaperManager myWallpaperManager
                            = WallpaperManager.getInstance(getApplicationContext());

                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                            try {
                                myWallpaperManager.setResource(R.raw.hills);
                                Toast.makeText(MainActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(MainActivity.this,
                                        "Whoops! Something went wrong!", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                myWallpaperManager.setResource(R.raw.hills);
                                Toast.makeText(MainActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(MainActivity.this,
                                        "Whoops! Something went wrong!", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    public void holly(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Set as your wallpaper?")
                .setIcon(R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    WallpaperManager myWallpaperManager
                            = WallpaperManager.getInstance(getApplicationContext());

                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                            try {
                                myWallpaperManager.setResource(R.raw.holly);
                                Toast.makeText(MainActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(MainActivity.this,
                                        "Whoops! Something went wrong!", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                myWallpaperManager.setResource(R.raw.holly);
                                Toast.makeText(MainActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(MainActivity.this,
                                        "Whoops! Something went wrong!", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    public void leaves(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Set as your wallpaper?")
                .setIcon(R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    WallpaperManager myWallpaperManager
                            = WallpaperManager.getInstance(getApplicationContext());

                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                            try {
                                myWallpaperManager.setResource(R.raw.leaves);
                                Toast.makeText(MainActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(MainActivity.this,
                                        "Whoops! Something went wrong!", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                myWallpaperManager.setResource(R.raw.leaves);
                                Toast.makeText(MainActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(MainActivity.this,
                                        "Whoops! Something went wrong!", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    public void lilac(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Set as your wallpaper?")
                .setIcon(R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    WallpaperManager myWallpaperManager
                            = WallpaperManager.getInstance(getApplicationContext());

                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                            try {
                                myWallpaperManager.setResource(R.raw.lilac);
                                Toast.makeText(MainActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(MainActivity.this,
                                        "Whoops! Something went wrong!", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                myWallpaperManager.setResource(R.raw.lilac);
                                Toast.makeText(MainActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(MainActivity.this,
                                        "Whoops! Something went wrong!", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    public void lovers_leap(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Set as your wallpaper?")
                .setIcon(R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    WallpaperManager myWallpaperManager
                            = WallpaperManager.getInstance(getApplicationContext());

                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                            try {
                                myWallpaperManager.setResource(R.raw.lovers_leap);
                                Toast.makeText(MainActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(MainActivity.this,
                                        "Whoops! Something went wrong!", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                myWallpaperManager.setResource(R.raw.lovers_leap);
                                Toast.makeText(MainActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(MainActivity.this,
                                        "Whoops! Something went wrong!", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    public void mountain_trail(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Set as your wallpaper?")
                .setIcon(R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    WallpaperManager myWallpaperManager
                            = WallpaperManager.getInstance(getApplicationContext());

                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                            try {
                                myWallpaperManager.setResource(R.raw.mountain_trail);
                                Toast.makeText(MainActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(MainActivity.this,
                                        "Whoops! Something went wrong!", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                myWallpaperManager.setResource(R.raw.mountain_trail);
                                Toast.makeText(MainActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(MainActivity.this,
                                        "Whoops! Something went wrong!", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    public void rock_fall(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Set as your wallpaper?")
                .setIcon(R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    WallpaperManager myWallpaperManager
                            = WallpaperManager.getInstance(getApplicationContext());

                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                            try {
                                myWallpaperManager.setResource(R.raw.rock_fall);
                                Toast.makeText(MainActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(MainActivity.this,
                                        "Whoops! Something went wrong!", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                myWallpaperManager.setResource(R.raw.rock_fall);
                                Toast.makeText(MainActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(MainActivity.this,
                                        "Whoops! Something went wrong!", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    public void stone_bridge(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Set as your wallpaper?")
                .setIcon(R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    WallpaperManager myWallpaperManager
                            = WallpaperManager.getInstance(getApplicationContext());

                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                            try {
                                myWallpaperManager.setResource(R.raw.stone_bridge);
                                Toast.makeText(MainActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(MainActivity.this,
                                        "Whoops! Something went wrong!", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                myWallpaperManager.setResource(R.raw.stone_bridge);
                                Toast.makeText(MainActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(MainActivity.this,
                                        "Whoops! Something went wrong!", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    public void twigs(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Set as your wallpaper?")
                .setIcon(R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    WallpaperManager myWallpaperManager
                            = WallpaperManager.getInstance(getApplicationContext());

                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                            try {
                                myWallpaperManager.setResource(R.raw.twigs);
                                Toast.makeText(MainActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(MainActivity.this,
                                        "Whoops! Something went wrong!", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                myWallpaperManager.setResource(R.raw.twigs);
                                Toast.makeText(MainActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(MainActivity.this,
                                        "Whoops! Something went wrong!", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    public void whale_shark(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Set as your wallpaper?")
                .setIcon(R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    WallpaperManager myWallpaperManager
                            = WallpaperManager.getInstance(getApplicationContext());

                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                            try {
                                myWallpaperManager.setResource(R.raw.whale_shark);
                                Toast.makeText(MainActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(MainActivity.this,
                                        "Whoops! Something went wrong!", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                myWallpaperManager.setResource(R.raw.whale_shark);
                                Toast.makeText(MainActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(MainActivity.this,
                                        "Whoops! Something went wrong!", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    public void white_flowers(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Set as your wallpaper?")
                .setIcon(R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    WallpaperManager myWallpaperManager
                            = WallpaperManager.getInstance(getApplicationContext());

                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                            try {
                                myWallpaperManager.setResource(R.raw.white_flowers);
                                Toast.makeText(MainActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(MainActivity.this,
                                        "Whoops! Something went wrong!", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                myWallpaperManager.setResource(R.raw.white_flowers);
                                Toast.makeText(MainActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(MainActivity.this,
                                        "Whoops! Something went wrong!", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }
}
