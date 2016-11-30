package test.jinesh.avatargenerator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created by Jinesh on 30-11-2016.
 */

public class AvatarGenerator {
    private Context context;

    public AvatarGenerator(Context context) {
        this.context = context;
    }

    public Bitmap generateAvatar(String imagePath) {
        Bitmap avatar = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        avatar = BitmapFactory.decodeFile(imagePath, options);
        FaceDetector faceDetector = new
                FaceDetector.Builder(context).setTrackingEnabled(false)
                .build();
        if (!faceDetector.isOperational()) {
            Log.e("FaceDetector","Could not set up the face detector!");
            return avatar;
        }
        Frame frame = new Frame.Builder().setBitmap(avatar).build();
        SparseArray<Face> faces = faceDetector.detect(frame);
        ArrayList<Bitmap> bitmapList = new ArrayList<>();
        for (int i = 0; i < faces.size(); i++) {
            Face thisFace = faces.valueAt(i);
            float x1 = thisFace.getPosition().x;
            float y1 = thisFace.getPosition().y;
            float x2 = x1 + thisFace.getWidth();
            float y2 = y1 + thisFace.getHeight();
            try {
                Bitmap bitmap = Bitmap.createBitmap(avatar, (int) x1, (int) y1, (int) (x2), (int) (y2));
                bitmapList.add(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (bitmapList.size() == 0) {
            return avatar;
        } else if (bitmapList.size() == 1)
            return bitmapList.get(0);
        else {
            showAlert(context, bitmapList, imagePath);
        }
        return avatar;
    }

    private void showAlert(Context context, ArrayList<Bitmap> bitmapList, String imagePath) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        GridView gridView = new GridView(context);
        alertDialog.setView(gridView);
        alertDialog.setTitle("Choose Your face");
        alertDialog.setCancelable(false);
        AlertDialog alertDialog1 = alertDialog.create();
        alertDialog1.show();
        gridView.setAdapter(new ImageAdapter(bitmapList, imagePath, alertDialog1));
    }

    private class ImageAdapter extends BaseAdapter {
        private ArrayList<Bitmap> bitmapArrayList;
        private String imagePath;
        private AlertDialog builder;

        public ImageAdapter(ArrayList<Bitmap> bitmaps, String path, AlertDialog alertDialog) {
            bitmapArrayList = bitmaps;
            imagePath = path;
            builder = alertDialog;
        }

        @Override
        public int getCount() {
            return bitmapArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                ImageView imageView = new ImageView(context);
                imageView.setImageBitmap(bitmapArrayList.get(position));
                convertView = imageView;
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Method onImageResult = null;
                    Method[] methods = context.getClass().getMethods();
                    for (Method method : methods) {
                        if (method.getName().equals("onImageResult")) {
                            onImageResult = method;
                            break;
                        }
                    }
                    if (onImageResult != null) {
                        try {
                            onImageResult.invoke(context, imagePath, imagePath, bitmapArrayList.get(position), CameraHandler.FACE_DETECT);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                    builder.dismiss();

                }
            });
            return convertView;
        }
    }
}
