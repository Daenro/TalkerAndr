package com.example.talker;

import android.app.Activity;
import android.widget.LinearLayout;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.ViewGroup;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.media.MediaRecorder;
import android.media.MediaPlayer;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity {
	private static final String LOG_TAG = "AudioRecordTest";
	private static String mFileName = null;

	private RecordButton mRecordButton = null;
	private MediaRecorder mRecorder = null;

	private PlayButton mPlayButton = null;
	private MediaPlayer mPlayer = null;

	private UploadButton mUploadButton = null;

	private void onRecord(boolean start) {
		if (start) {
			startRecording();
		} else {
			stopRecording();
		}
	}

	private void onPlay(boolean start) {
		if (start) {
			startPlaying();
		} else {
			stopPlaying();
		}
	}

	private void startPlaying() {
		mPlayer = new MediaPlayer();
		try {
			mPlayer.setDataSource(mFileName);
			mPlayer.prepare();
			mPlayer.start();
		} catch (IOException e) {
			Log.e(LOG_TAG, "prepare() failed");
		}
	}

	private void stopPlaying() {
		mPlayer.release();
		mPlayer = null;
	}

	private void startRecording() {
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mRecorder.setOutputFile(mFileName);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

		try {
			mRecorder.prepare();
		} catch (IOException e) {
			Log.e(LOG_TAG, "prepare() failed");
		}

		mRecorder.start();
	}

	private void stopRecording() {
		mRecorder.stop();
		mRecorder.release();
		mRecorder = null;
	}

	class RecordButton extends Button {
		boolean mStartRecording = true;

		OnClickListener clicker = new OnClickListener() {
			public void onClick(View v) {
				onRecord(mStartRecording);
				if (mStartRecording) {
					setText("Stop recording");
				} else {
					setText("Start recording");
				}
				mStartRecording = !mStartRecording;
			}
		};

		public RecordButton(Context ctx) {
			super(ctx);
			setText("Start recording");
			setOnClickListener(clicker);
		}
	}
	
	public static String getStringFromInputStream(InputStream is) {
		  BufferedReader br = null;
		  StringBuilder sb = new StringBuilder();

		  String line;
		  try {
		   br = new BufferedReader(new InputStreamReader(is));
		   while ((line = br.readLine()) != null) {
		    sb.append(line);
		   }

		  } catch (IOException e) {
		   e.printStackTrace();
		  } finally {
		   if (br != null) {
		    try {
		     br.close();
		    } catch (IOException e) {
		     e.printStackTrace();
		    }
		   }
		  }

		  return sb.toString();
		 }

	public static byte[] convertFileToByteArray(File f) {
		byte[] byteArray = null;
		try {
			InputStream inputStream = new FileInputStream(f);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] b = new byte[1024 * 8];
			int bytesRead = 0;

			while ((bytesRead = inputStream.read(b)) != -1) {
				bos.write(b, 0, bytesRead);
			}

			byteArray = bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return byteArray;
	}
	
	 static String encodeArray(byte[] in) throws IOException {
		  StringBuffer out = new StringBuffer();
		  out.append(Base64Coder.encode(in, 0, in.length));
		  return out.toString();
		 }

	class UploadButton extends Button {
		OnClickListener clicker = new OnClickListener() {
			public void onClick(View v) {
				RandomAccessFile f = null;
				File file = new File(mFileName);
				try {
					 f = new RandomAccessFile(file,"r");
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				byte[] b = null;
				try {
					b = new byte[(int)f.length()];
					f.read(b);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				byte[] res = convertFileToByteArray(file);
				
				
				StringBuilder sb = new StringBuilder();  

				String http = "http://37.57.72.159:55034/api/record/admin";  
				 

				
				try {  
					 HttpClient httpclient = new DefaultHttpClient();
					  HttpPost httpPost = new HttpPost(http);

				    //Create JSONObject here
				    JSONObject jsonParam = new JSONObject();
				    jsonParam.put("UserName", "admin");
				    //String base64 = new String(Base64.encode(b,Base64.DEFAULT));
				    String base64 = encodeArray(b);
				    jsonParam.put("Value", base64);
				    jsonParam.put("Message", "FromAndroidJava");
				    String json = jsonParam.toString();
				    
				    StringEntity se = new StringEntity(json,"UTF-8");
				  
		            httpPost.setEntity(se);
		            httpPost.setHeader("Accept", "application/json");
		            httpPost.setHeader("Content-type", "application/json");
		            String authInfo = "admin" + ":" + "qwerty";
		            String base664 = Base64.encodeToString(authInfo.getBytes("UTF-8"), Base64.URL_SAFE|Base64.NO_WRAP);
		            httpPost.setHeader("Authorization", "Basic "+base664 );
				    
				    HttpResponse httpResponse = httpclient.execute(httpPost);
				    
				    HttpEntity entity = httpResponse.getEntity();
				    String content = getStringFromInputStream(entity.getContent());
				    int vasa = 4;
				    vasa=6;
				    
				    
				} catch (MalformedURLException e) {  

				         e.printStackTrace();  
				}  
				catch (IOException e) {  

				    e.printStackTrace();  
				    } catch (JSONException e) {
				    // TODO Auto-generated catch block
				    e.printStackTrace();
				}
				
			}
		};

		public UploadButton(Context ctx) {
			super(ctx);
			setText("Upload file");
			setOnClickListener(clicker);
		}
	}

	class PlayButton extends Button {
		boolean mStartPlaying = true;

		OnClickListener clicker = new OnClickListener() {
			public void onClick(View v) {
				onPlay(mStartPlaying);
				if (mStartPlaying) {
					setText("Stop playing");
				} else {
					setText("Start playing");
				}
				mStartPlaying = !mStartPlaying;
			}
		};

		public PlayButton(Context ctx) {
			super(ctx);
			setText("Start playing");
			setOnClickListener(clicker);
		}
	}

	public MainActivity() {
		mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
		mFileName += "/audiorecordtest.wav";
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

		StrictMode.setThreadPolicy(policy); 
		LinearLayout ll = new LinearLayout(this);
		mRecordButton = new RecordButton(this);
		ll.addView(mRecordButton, new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, 0));
		mPlayButton = new PlayButton(this);
		ll.addView(mPlayButton, new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, 0));
		mUploadButton = new UploadButton(this);
		ll.addView(mUploadButton, new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, 0));
		setContentView(ll);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mRecorder != null) {
			mRecorder.release();
			mRecorder = null;
		}

		if (mPlayer != null) {
			mPlayer.release();
			mPlayer = null;
		}
	}
	
}

