package test.jinesh.avatargenerator;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity implements CameraHandler.OnImageResultListner {
    TextView button;
    CameraHandler handler;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler=new CameraHandler(this);
        button= (TextView) findViewById(R.id.button);
        imageView= (ImageView) findViewById(R.id.imageView);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               handler.showDialogToCaptureImage();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        handler.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onImageResult(String imageName, String imagePath, Bitmap imageFile, int sourceType) {
        if(sourceType==CameraHandler.CAMERA_TYPE || sourceType==CameraHandler.GALLERY_TYPE ) {
            Bitmap bitmap = new AvatarGenerator(MainActivity.this).generateAvatar(imagePath);
            imageView.setImageBitmap(bitmap);
        }else if(sourceType==CameraHandler.FACE_DETECT){
            imageView.setImageBitmap(imageFile);
        }
    }
}
