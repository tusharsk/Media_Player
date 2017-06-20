package com.example.tusharsk.mediaplayer;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    myCustomAdapter Adapter;
    ListView lv;
    MediaPlayer mp;
    SeekBar sb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv=(ListView) findViewById(R.id.lv);
        sb=(SeekBar)findViewById(R.id.sb);
        CheckUserPermsions();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SongInfo songInfo=SongsList.get(position);
                mp=new MediaPlayer();
                try {
                    mp.setDataSource(songInfo.path);
                    mp.prepare();
                    mp.start();
                    sb.setMax(mp.getDuration());
                    Toast.makeText(getApplicationContext()," song selected ", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"not song selected ", Toast.LENGTH_SHORT).show();
                }

            }
        });

        mythread my=new mythread();
        my.start();

    }



    // creating thread class
    public class mythread extends Thread
    {
        @Override
        public void run()
        {
            while(true)
            {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(mp!=null)
                            sb.setProgress(mp.getCurrentPosition());
                    }
                });
            }
        }
    }
    public void bustart(View view)
    {
        if(mp!=null)
            mp.start();
    }

    public void bupause(View view)
    {
        if(mp!=null)
            mp.pause();
    }
    public void bustop(View view)
    {
        if(mp!=null)
            mp.stop();
    }

    //getallsong() playing online songs
    ArrayList<SongInfo>  SongsList =new ArrayList<SongInfo>();
    /*public ArrayList<SongInfo> getallsong()
    {
        SongsList.clear();
        SongsList.add(new SongInfo("https://www.youtube.com/watch?v=EoCz3Vx1pXg&list=RDEoCz3Vx1pXg","shape of  you  ","bakar","quran"));
        SongsList.add(new SongInfo("http://server6.mp3quran.net/thubti/002.mp3","Bakara","bakar","quran"));
        SongsList.add(new SongInfo("http://server6.mp3quran.net/thubti/003.mp3","Al-Imran","bakar","quran"));
        SongsList.add(new SongInfo("http://server6.mp3quran.net/thubti/004.mp3","An-Nisa'","bakar","quran"));
        SongsList.add(new SongInfo("http://server6.mp3quran.net/thubti/005.mp3","Al-Ma'idah","bakar","quran"));
        SongsList.add(new SongInfo("http://server6.mp3quran.net/thubti/006.mp3","Al-An'am","bakar","quran"));
        SongsList.add(new SongInfo("http://server6.mp3quran.net/thubti/007.mp3","Al-A'raf","bakar","quran"));
        return SongsList;
    }*/

    public ArrayList<SongInfo> getallsong() {
        Uri allsongsuri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        Cursor cursor = getContentResolver().query(allsongsuri, null, selection, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String    song_name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    String fullpath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    String    album_name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                    String   artist_name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    SongsList.add(new SongInfo(fullpath,song_name,album_name,artist_name));

                } while (cursor.moveToNext());

            }
            cursor.close();

        }

        return SongsList;
    }
    // myadapter classs
    public class myCustomAdapter extends BaseAdapter
    {
        ArrayList<SongInfo> songlist;
        public myCustomAdapter(ArrayList<SongInfo> songlist)
        {
            this.songlist=songlist;
        }
        @Override
        public int getCount()
        {
            return songlist.size();
        }
        @Override
        public String getItem(int position)
        {
            return null;
        }
        @Override
        public long getItemId(int position)
        {
            return position;
        }


        @Override
        public View getView(int position, View covertView, ViewGroup parent)
        {
            LayoutInflater myinflater=getLayoutInflater();
            View myview=myinflater.inflate(R.layout.listitem,null);

            SongInfo s=songlist.get(position);
            TextView tv1=(TextView)myview.findViewById(R.id.songname);
            tv1.setText(s.song_name);
            TextView tv2=(TextView)myview.findViewById(R.id.artistname);
            tv2.setText(s.artist_name);

            return myview;
        }
    }



    // check permission
    void CheckUserPermsions(){
        if ( Build.VERSION.SDK_INT >= 23){
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED  ){
                requestPermissions(new String[]{
                                android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return ;
            }
        }

        LoadSng();

    }
    //get acces to location permsion
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LoadSng();
                } else {
                    // Permission Denied
                    Toast.makeText( this,"denail" , Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    void LoadSng(){
        Adapter=new  myCustomAdapter(getallsong());
        lv.setAdapter(Adapter);
    }

}

