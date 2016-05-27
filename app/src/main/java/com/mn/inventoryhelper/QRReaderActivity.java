package com.mn.inventoryhelper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView.OnQRCodeReadListener;

public class QRReaderActivity extends Activity implements OnQRCodeReadListener {

    QRCodeReaderView idQRCodeReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrreader);

        idQRCodeReader = (QRCodeReaderView) findViewById(R.id.idQRCodeReader);
        idQRCodeReader.setOnQRCodeReadListener(this);
    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        System.out.println(text);
        Intent intent = new Intent(getApplicationContext(), EntryDetailsActivity.class);
        intent.putExtra("idNumber", text);
        startActivity(intent);
        finish();
    }

    @Override
    public void cameraNotFound() {
        finish();
    }

    @Override
    public void QRCodeNotFoundOnCamImage() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        idQRCodeReader.getCameraManager().stopPreview();
    }

    @Override
    protected void onResume() {
        super.onResume();
        idQRCodeReader.getCameraManager().startPreview();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
