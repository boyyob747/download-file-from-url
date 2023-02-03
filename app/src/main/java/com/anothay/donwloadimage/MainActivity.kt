package com.anothay.donwloadimage

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import pub.devrel.easypermissions.EasyPermissions
import java.io.File


class MainActivity : AppCompatActivity() {
    private val rcWriteExternalStorage = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val button: Button = findViewById(R.id.button_download)
        button.setOnClickListener {
            if (hasWriteExternalStoragePermission()) {
                downloadAndOpenImage(this, "https://i.pinimg.com/736x/e9/94/04/e99404afa65ee34732ac20386ed09f8c.jpg", "temp_image")
            } else {
                EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.required_permission_write_external_storage),
                    rcWriteExternalStorage,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }

    }

    private fun hasWriteExternalStoragePermission():Boolean {
        return EasyPermissions.hasPermissions(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    private fun downloadAndOpenImage(context: Context, url: String, fileName: String) {
        val request = DownloadManager.Request(Uri.parse(url))
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, fileName)
        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = manager.enqueue(request)

        val onDownloadComplete = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (downloadId == id) {
                    openImage(context, "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)}/$fileName")
                    context.unregisterReceiver(this)
                }
            }
        }

        context.registerReceiver(onDownloadComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    fun openImage(context: Context, filePath: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        val uri = FileProvider.getUriForFile(context, context.packageName + ".provider", File(filePath))
        intent.setDataAndType(uri, "image/*")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(intent)
    }
}