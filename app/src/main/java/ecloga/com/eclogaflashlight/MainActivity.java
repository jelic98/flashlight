package ecloga.com.eclogaflashlight;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    ImageButton btnSwitch, btnSound;
    TextView tvCopyRight1, tvCopyRight2, tvMode;
    Camera camera;
    boolean isFlashOn, isSoundOn;
    boolean hasFlash, timer;
    Parameters params;
    MediaPlayer mp;
    int mode;
    RelativeLayout activity_main;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isSoundOn = true;
        mode = 1;

        activity_main = (RelativeLayout) findViewById(R.id.activity_main);
        btnSwitch = (ImageButton) findViewById(R.id.btnSwitch);
        btnSound = (ImageButton) findViewById(R.id.btnSound);
        hasFlash = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        tvCopyRight1 = (TextView) findViewById(R.id.tvCopyRight1);
        tvCopyRight2 = (TextView) findViewById(R.id.tvCopyRight2);
        tvMode = (TextView) findViewById(R.id.tvMode);

        btnSound.setColorFilter(Color.WHITE);

        if (!hasFlash) {
            AlertDialog alert = new AlertDialog.Builder(MainActivity.this).create();
            alert.setTitle("Error");
            alert.setMessage("Sorry, your device doesn't have LED flash! You can use White Screen.");
            alert.setButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    activity_main.setBackgroundColor(getResources().getColor(R.color.white_screen));
                    btnSwitch.setVisibility(View.INVISIBLE);
                    btnSound.setVisibility(View.INVISIBLE);

                }
            });
            alert.show();
            return;
        }

        getCamera();

        toggleButtonImage();

        btnSound.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isSoundOn) {
                    isSoundOn = false;
                    btnSound.setImageResource(R.drawable.btn_sound_off);
                    btnSound.setColorFilter(Color.BLACK);
                } else {
                    isSoundOn = true;
                    btnSound.setImageResource(R.drawable.btn_sound_on);
                    btnSound.setColorFilter(Color.WHITE);
                }
            }
        });

        btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pressAnimation();

                new CountDownTimer(500, 250) {
                    public void onTick(long millisUntilFinished) {

                    }

                    public void onFinish() {
                        if (isFlashOn) {
                            turnOffFlash();
                        } else {
                            turnOnFlash();
                        }
                    }
                }.start();
            }
        });

        tvMode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch(mode) {
                    case 1:
                        mode = 2;
                        tvMode.setText("2");
                        if (isFlashOn) {
                            turnOffFlash();
                            turnOnFlash();
                        }
                        break;
                    case 2:
                        mode = 3;
                        tvMode.setText("3");
                        if (isFlashOn) {
                            turnOffFlash();
                            turnOnFlash();
                        }
                        break;
                    case 3:
                        mode = 1;
                        tvMode.setText("1");
                        if (isFlashOn) {
                            turnOffFlash();
                            turnOnFlash();
                        }
                        break;
                }
            }
        });
    }

    private void getCamera() {
        if (camera == null) {
            try {
                camera = Camera.open();
                params = camera.getParameters();
            } catch (RuntimeException e) {
                Log.e("Failed to Open. Error: ", e.getMessage());
            }
        }
    }

    private void pressAnimation() {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        btnSwitch.startAnimation(shake);
    }

    private void turnOnFlash() {
        if (!isFlashOn) {
            if (camera == null || params == null) {
                return;
            }

            switch(mode) {
                case 1:
                    params = camera.getParameters();
                    params.setFlashMode(Parameters.FLASH_MODE_TORCH);
                    camera.setParameters(params);
                    camera.startPreview();
                    break;
                case 2:
                    new CountDownTimer(250, 250) {
                        public void onTick(long millisUntilFinished) {
                            if (timer) {
                                params = camera.getParameters();
                                params.setFlashMode(Parameters.FLASH_MODE_TORCH);
                                camera.setParameters(params);
                                camera.startPreview();
                            }else {
                                params = camera.getParameters();
                                params.setFlashMode(Parameters.FLASH_MODE_OFF);
                                camera.setParameters(params);
                                camera.startPreview();
                            }
                            timer = !timer;

                            if (!isFlashOn) {
                                cancel();
                                turnOffFlash();
                            }
                        }

                        public void onFinish() {
                            start();

                            if (mode == 3) {
                                cancel();
                                turnOnFlash();
                            }

                            if (!isFlashOn) {
                                cancel();
                                turnOffFlash();
                            }
                        }
                    }.start();
                    break;
                case 3:
                    new CountDownTimer(50, 50) {
                        public void onTick(long millisUntilFinished) {
                            if (timer) {
                                params = camera.getParameters();
                                params.setFlashMode(Parameters.FLASH_MODE_TORCH);
                                camera.setParameters(params);
                                camera.startPreview();
                            }else {
                                params = camera.getParameters();
                                params.setFlashMode(Parameters.FLASH_MODE_OFF);
                                camera.setParameters(params);
                                camera.startPreview();
                            }
                            timer = !timer;

                            if (!isFlashOn) {
                                cancel();
                                turnOffFlash();
                            }
                        }

                        public void onFinish() {
                            start();

                            if (mode == 1) {
                                cancel();
                                turnOnFlash();
                            }

                            if (!isFlashOn) {
                                cancel();
                                turnOffFlash();
                            }
                        }
                    }.start();
                    break;
            }
            isFlashOn = true;

            playSound();
            toggleButtonImage();
        }
    }

    private void turnOffFlash() {
        if (isFlashOn) {
            if (camera == null || params == null) {
                return;
            }

            params = camera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_OFF);
            camera.setParameters(params);
            camera.stopPreview();
            isFlashOn = false;

            playSound();
            toggleButtonImage();
        }
    }

    private void playSound() {
        if(isSoundOn) {
            if (isFlashOn) {
                mp = MediaPlayer.create(MainActivity.this, R.raw.light_switch_off);
            }else{
                mp = MediaPlayer.create(MainActivity.this, R.raw.light_switch_on);
            }
            mp.setOnCompletionListener(new OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });
            mp.start();
        }
    }

    private void toggleButtonImage() {
        if (isFlashOn) {
            btnSwitch.setImageResource(R.drawable.btn_switch_on);
        } else {
            btnSwitch.setImageResource(R.drawable.btn_switch_off);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFlashOn)
            turnOnFlash();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(hasFlash)
            turnOnFlash();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isFlashOn)
            turnOnFlash();
    }
}