package com.example.bookstats.features.scanner

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.bookstats.R
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CompoundBarcodeView
import android.Manifest
import com.example.bookstats.features.creation.BookCreationFragment.Companion.ISBN

class ScannerActivity : AppCompatActivity() {
    private lateinit var barcodeView: CompoundBarcodeView

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.scanner)

        barcodeView = findViewById(R.id.barcodeView)

        if (!hasCameraPermission()) {
            requestCameraPermission()
        }
    }

    override fun onResume() {
        super.onResume()
        barcodeView.resume()
        barcodeView.decodeSingle(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult?) {
                result?.let {
                    val isbn = result.text
                    Log.d("ScannerActivity", "Scanned ISBN: $isbn")
                    val resultIntent = Intent().apply {
                        putExtra(ISBN, isbn)
                    }
                    setResult(RESULT_OK, resultIntent)
                    finish()
                }
            }

            override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {}
        })
    }

    override fun onPause() {
        super.onPause()
        barcodeView.pause()
    }

    private fun hasCameraPermission(): Boolean {
        val permission = Manifest.permission.CAMERA
        val result = ContextCompat.checkSelfPermission(this, permission)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }
}