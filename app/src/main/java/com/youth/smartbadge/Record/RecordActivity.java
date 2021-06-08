package com.youth.smartbadge.Record;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.youth.smartbadge.R;

public class RecordActivity extends AppCompatActivity {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private MediaRecorder recorder;
    private String fileName_roadway, fileName_crosswalk, fileName_sidewalk;

    private MediaPlayer player;
    private int position = 0; //다시 시작 기능을 위한 현재 재생 위치 확인 변수
    private Button btnRecord_roadway;
    private Button btnStopRecording_roadway;
    private Button btnPlay_roadway;
    private Button btnRecord_sidewalk;
    private Button btnStopRecording_sidewalk;
    private Button btnPlay_sidewalk;
    private Button btnRecord_crosswalk;
    private Button btnStopRecording_crosswalk;
    private Button btnPlay_crosswalk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        init();

        // roadway 차도일 때 버튼이벤트
        //차도 녹음
        btnRecord_roadway.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                btnRecord_roadway.setVisibility (View.GONE);
                btnStopRecording_roadway.setVisibility (View.VISIBLE);
                recordAudio (fileName_roadway);
            }
        });

        //차도 녹음 중지
        btnStopRecording_roadway.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                btnRecord_roadway.setVisibility (View.VISIBLE);
                btnStopRecording_roadway.setVisibility (View.GONE);
                stopRecording ( );
            }
        });

        //차도 녹음 재생
        btnPlay_roadway.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                playAudio (fileName_roadway);
            }
        });

        //sidewalk 인도일 때 버튼 이벤트
        //인도 녹음
        btnRecord_sidewalk.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                btnRecord_sidewalk.setVisibility (View.GONE);
                btnStopRecording_sidewalk.setVisibility (View.VISIBLE);
                recordAudio (fileName_sidewalk);
            }
        });

        //인도 녹음 중지
        btnStopRecording_sidewalk.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {

                btnRecord_sidewalk.setVisibility (View.VISIBLE);
                btnStopRecording_sidewalk.setVisibility (View.GONE);
                stopRecording ( );
            }
        });

        //인도 녹음 재생
        btnPlay_sidewalk.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                playAudio (fileName_sidewalk);
            }
        });

        //crosswalk 횡단보도일 때 버튼 이벤트
        //횡단보도 녹음
        btnRecord_crosswalk.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                btnRecord_crosswalk.setVisibility (View.GONE);
                btnStopRecording_crosswalk.setVisibility (View.VISIBLE);
                recordAudio (fileName_crosswalk);
            }
        });

        //횡단보도 녹음정지
        btnStopRecording_crosswalk.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                btnRecord_crosswalk.setVisibility (View.VISIBLE);
                btnStopRecording_crosswalk.setVisibility (View.GONE);
                stopRecording ( );

            }
        });

        //횡단보도 녹음 재생
        btnPlay_crosswalk.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                playAudio (fileName_crosswalk);
            }
        });
    }




    public void recordAudio(String fileName) {
        recorder = new MediaRecorder ();
        recorder.setAudioSource (MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat (MediaRecorder.OutputFormat.MPEG_4);
        recorder.setOutputFile (fileName);
        recorder.setAudioEncoder (MediaRecorder.AudioEncoder.AAC);
        try {
            recorder.prepare ();

            Toast.makeText (this,"녹음시작",Toast.LENGTH_SHORT).show ();
        }catch (Exception e){
            Log.e ("sampleAudioRecorder","Exception: ",e);
        }
        recorder.start ();
    }

    public void stopRecording(){
        if (recorder!=null){
            recorder.stop ();
            recorder.release ();
            recorder=null;

            Toast.makeText (this,"녹음중지", Toast.LENGTH_SHORT).show ();
        }
    }

    public void playAudio(String fileName){
        try {
            closePlayer();

            player=new MediaPlayer ();
            player.setDataSource (fileName);
            player.prepare ();
            player.start ();

            Toast.makeText (this,"재생시작",Toast.LENGTH_LONG).show ();
        }catch (Exception e){
            e.printStackTrace ();
        }
    }

    private void closePlayer() {
        if (player!= null){
            player.release ();
            player=null;
        }
    }

    public void init(){
        btnRecord_roadway = findViewById (R.id.record_roadway);
        btnStopRecording_roadway = findViewById (R.id.recordStop_roadway);
        btnPlay_roadway = findViewById (R.id.play_roadway);
        btnRecord_sidewalk = findViewById (R.id.record_sidewalk);
        btnStopRecording_sidewalk = findViewById (R.id.recordStop_sidewalk);
        btnPlay_sidewalk = findViewById (R.id.play_sidewalk);
        btnRecord_crosswalk = findViewById (R.id.record_crosswalk);
        btnStopRecording_crosswalk = findViewById (R.id.recordStop_crosswalk);
        btnPlay_crosswalk = findViewById (R.id.play_crosswalk);

        //위험 권한 부여하기
        int permissionCheck = ContextCompat.checkSelfPermission (this, Manifest.permission.RECORD_AUDIO);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText (this, "Audio 권한 있음.", Toast.LENGTH_LONG).show ( );
        } else {
            Toast.makeText (this, "Audio 권한 없음", Toast.LENGTH_LONG).show ( );
            if (ActivityCompat.shouldShowRequestPermissionRationale (this, Manifest.permission.RECORD_AUDIO)) {
                Toast.makeText (this, "Audio 권한 설명 필요함.", Toast.LENGTH_LONG).show ( );
            } else {
                ActivityCompat.requestPermissions (this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
            }
        }
        //Record to the external cache directory for visibility(외부캐시에 기록)-기본경로
        String fileName = getExternalCacheDir ( ).getAbsolutePath ( );
        Log.e ("MainActivity", "저장할 파일 명 : " + fileName);
        fileName_roadway =fileName+ "/audiorecordtest_roadway.mp3";
        fileName_crosswalk =fileName+ "/audiorecordtest_crosswalk.mp3";
        fileName_sidewalk =fileName+ "/audiorecordtest_sidewalk.mp3";
    }
}