# How
- Service
```kotlin
fun downloadAndOpenImage(context: Context, url: String, fileName: String) {
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

```
- Java 
```java
public void downloadAndOpenImage(Context context, String url, String fileName) {
    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, fileName);
    request.allowScanningByMediaScanner();
    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
    DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
    final long downloadId = manager.enqueue(request);

    BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (downloadId == id) {
                openImage(context, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + fileName);
                context.unregisterReceiver(this);
            }
        }
    };

    context.registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
}

public void openImage(Context context, String filePath) {
    Intent intent = new Intent(Intent.ACTION_VIEW);
    Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", new File(filePath));
    intent.setDataAndType(uri, "image/*");
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    context.startActivity(intent);
}

```
- File Provider
```
// AndroidManifest.xml
<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="${applicationId}.provider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/provider_paths"/>
</provider>
// provider_paths.xml
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <external-path name="external_files" path="." />
</paths>
```
- Permission
```
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```
