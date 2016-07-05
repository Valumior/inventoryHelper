package com.mn.inventoryhelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

import java.util.ArrayList;

public class QRReaderMassActivity extends Activity implements QRCodeReaderView.OnQRCodeReadListener {

    ArrayList<String> readCodes;
    QRCodeReaderView QRCodeMassReader;
    Button QRCodeMassOkButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrreader_mass);

        Intent intent = getIntent();
        if(intent.hasExtra("readCodes")){
            readCodes = intent.getStringArrayListExtra("readCodes");
        } else {
            readCodes = new ArrayList<>();
        }

        QRCodeMassReader = (QRCodeReaderView)findViewById(R.id.QRCodeMassReader);

        QRCodeMassOkButton = (Button)findViewById(R.id.QRCodeMassOkButton);

        QRCodeMassOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resIntent = new Intent();
                resIntent.putStringArrayListExtra("readCodes", readCodes);
                setResult(RESULT_OK, resIntent);
                finish();
            }
        });

        QRCodeMassReader.setOnQRCodeReadListener(this);
    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        if(readCodes.contains(text)){
            Toast toast = Toast.makeText(getApplicationContext(), "Już przeczytano: " + text, Toast.LENGTH_SHORT);
            toast.show();

        } else {
            readCodes.add(text);
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(400);
            Toast toast = Toast.makeText(getApplicationContext(), "Przeczytano: " + text, Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    @Override
    public void cameraNotFound() {

    }

    @Override
    public void QRCodeNotFoundOnCamImage() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        QRCodeMassReader.getCameraManager().stopPreview();
    }

    @Override
    protected void onResume() {
        super.onResume();
        QRCodeMassReader.getCameraManager().startPreview();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }
}
