package com.example.fatla.mooncatcanvas;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.github.chrisbanes.photoview.PhotoView;
import android.view.Window;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;

import org.w3c.dom.Text;

import yuku.ambilwarna.AmbilWarnaDialog;

public class ModeSoloDraw extends AppCompatActivity implements View.OnClickListener {

    private static  final String TAG = ModeSoloDraw.class.getSimpleName();
    private ImageButton currpaint, drawbtn, baru, erase, save, imgView,cwheel, redobtn, undobtn, flipbtn, linebtn, bucketbtn, increase;
    private DrawingView drawView;
    private TextView textView;
    private String currColor = "#000000";
    public int pcolor =0xFF660000;
    private ModeSoloDraw modeSoloDrawView;

    private Button next, prev;
//    , redobtn, undobtn, flipbtn, linebtn, bucketbtn, increase
    ImageSwitcher imageSwitcher;
    Integer[] images = {R.drawable.image_8,R.drawable.image_1, R.drawable.image_2, R.drawable.image_3,
            R.drawable.image_4, R.drawable.image_5, R.drawable.image_6, R.drawable.image_7,
            R.drawable.image_8, R.drawable.image_9};
    int i = 0;
    int size = 10;
    int newsize = 10;
    private boolean flagflip = true;
    private boolean flagline = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);

        drawView = (DrawingView)findViewById(R.id.drawing);
        drawbtn = (ImageButton)findViewById(R.id.draw_btn);
        baru = (ImageButton)findViewById(R.id.new_btn);
        erase = (ImageButton)findViewById(R.id.erase_btn);
        save = (ImageButton)findViewById(R.id.save_btn);

        PhotoView photoView = (PhotoView) findViewById(R.id.photoViewV);

        cwheel = (ImageButton)findViewById(R.id.cwheel_btn);
        increase = (ImageButton)findViewById(R.id.increase_btn);

        next = (Button)findViewById(R.id.b_next);
        prev = (Button)findViewById(R.id.b_prev);

        redobtn = (ImageButton)findViewById(R.id.redo);
        undobtn = (ImageButton)findViewById(R.id.undo);

        flipbtn = (ImageButton)findViewById(R.id.flip);

        linebtn = (ImageButton)findViewById(R.id.line);

        bucketbtn = (ImageButton)findViewById(R.id.bucket);

        LinearLayout paintLayout = (LinearLayout)findViewById(R.id.paint_colors);
        currpaint = (ImageButton)paintLayout.getChildAt(0);

        currpaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
        drawbtn.setOnClickListener(this);
        baru.setOnClickListener(this);
        erase.setOnClickListener(this);
        save.setOnClickListener(this);

        // increase decrease brush size
        increase.setOnClickListener(this);

        //redo and undo buttons
        redobtn.setOnClickListener(this);
        undobtn.setOnClickListener(this);

        //flip view
        flipbtn.setOnClickListener(this);

        //line
        linebtn.setOnClickListener(this);

        //bucket
        bucketbtn.setOnClickListener(this);

        // launch color wheel
        ImageButton cwheel = (ImageButton)findViewById(R.id.cwheel_btn);
        cwheel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPicker();
            }
        });

        //setup image switcher
        imageSwitcher = (ImageSwitcher) findViewById(R.id.imageSwitcher);

        imageSwitcher.setFactory(new ViewSwitcher.ViewFactory(){
            public View makeView(){
                ImageView imageView = new ImageView(getApplicationContext());
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setLayoutParams(
                        new ImageSwitcher.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT));
                return imageView;
            }
        });

        // image switcher listener
        next.setOnClickListener(this);
        prev.setOnClickListener(this);

        // switch image
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (i>0){
                    i--;
                    imageSwitcher.setImageResource(images[i]);
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i < images.length - 1) {
                    i++;
                    imageSwitcher.setImageResource(images[i]);
                }
            }
        });

        redobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.onClickRedo();
            }
        });
        undobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.onClickUndo();
            }
        });

        flipbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(flagflip == true) {
                    drawView.setScaleX(-1);
                    imageSwitcher.setScaleX(-1);
                    flagflip = false;
                } else {
                    drawView.setScaleX(1);
                    imageSwitcher.setScaleX(1);
                    flagflip = true;
                }
            }
        });

        linebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flagline == false) {
                    drawView.drawLine(true);
                    flagline = true;
                } else {
                    drawView.drawLine(false);
                    flagline = false;
                }
            }
        });
    }

    public void paintClicked(View view) {
        if (view != currpaint) {
            imgView = (ImageButton)view;
            String color = view.getTag().toString();
            drawView.setColor(color);
            currColor = color;
            imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            currpaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            currpaint = (ImageButton)view;
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.increase_btn) {
            final AlertDialog.Builder alertDialog= new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog);

            alertDialog.setTitle("Brush Size");

            final SeekBar seek = new SeekBar(this);
            seek.setMax(255);
            seek.setKeyProgressIncrement(1);
            seek.getProgressDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
            Drawable progressDrawable = getResources().getDrawable(R.drawable.roundcorner);
            seek.setProgressDrawable(progressDrawable);
            alertDialog.setTitle("Please select your brush size. ");
            alertDialog.setView(seek);
            seek.setProgress(size);


            alertDialog.setMessage("Current brush size: " + size);

            seek.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY));

            final AlertDialog dialog = alertDialog.create();
//            LayoutInflater inflater = this.getLayoutInflater();
//            dialog.setView(inflater.inflate(R.layout.brushsize_dialog, null));

            seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    final TextView t_view = new TextView(getApplicationContext());
                    newsize = progress;
                    dialog.setMessage("Current brush size: " + progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            dialog.setButton(dialog.BUTTON_POSITIVE, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            size = newsize;
                            drawView.changeBrushSize(size);
                            dialog.dismiss();
                        }
                    });
            dialog.setButton(dialog.BUTTON_NEGATIVE, "Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            dialog.show();
        }
        if (view.getId() == R.id.draw_btn) {
            drawView.setColor(currColor);
            drawView.drawLine(false);
            flagline = false;
        }
        if (view.getId() == R.id.erase_btn) {
            drawView.setColor("#FAF5EE");
        }
        if (view.getId() == R.id.new_btn) {
            AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
            newDialog.setTitle("New Drawing");
            newDialog.setMessage("Start New Drawing");
            newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    drawView.startNew();
                    dialog.dismiss();
                }
            });
            newDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            newDialog.show();
        }
        if (view.getId() == R.id.save_btn) {
            AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
            saveDialog.setTitle("Save Drawing");
            saveDialog.setMessage("Save drawing to device gallery?");
            saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    drawView.saveDrawing2();
//                    drawView.setDrawingCacheEnabled(true);
//                    String imgSaved = MediaStore.Images.Media.insertImage(getContentResolver(), drawView.getDrawingCache(), UUID.randomUUID().toString()+".png", "drawing");
//                    if (imgSaved != null) {
//                        Toast savedtoast = Toast.makeText(getApplicationContext(), "Drawing saved to Gallery", Toast.LENGTH_SHORT);
//                        savedtoast.show();
//                    } else {
//                        Toast unsaved = Toast.makeText(getApplicationContext(),"Image could not saved", Toast.LENGTH_SHORT);
//                        unsaved.show();
//                    }
//                    drawView.destroyDrawingCache();
                }
            });
            saveDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            saveDialog.show();
        }
    }

    //open color picker
    public void openColorPicker(){
        pcolor = drawView.getPaintColor();
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, pcolor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                String pcolor = "#" + String.format("%06X", (0xFFFFFF & color));
                currColor = pcolor;
                SharedPreferences settings = getSharedPreferences("GAME_DATA", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("CURR_COLOR", currColor);
                editor.commit();

                drawView.setColor(pcolor);
            }
        });
        colorPicker.show();
    }
}
