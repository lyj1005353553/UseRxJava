package utils;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import java.io.File;

/**
 * Created by Administrator on 2017/2/28.
 * 一个简单的下载
 * 1.虽然下载什么的不需要自己操心了，但是建议还是将整个上面四段代码放在Service中执行，
 * 因为放在Activity中时，当用户按home键后，即使下载完了，也不会弹出安装界面
 * 2.建议使用startService的方式启动Service，这样不会与Activity生命周期绑定，保证下载完后能顺利安装。
 * 3.Service使用完后要及时地停掉！
 */

public class DownLoadUtils {
    private Context mCxt;
    /**
     * 本地存储路径名称
     */
    private String localPath = "UseRxxxx/";

    private DownloadManager downloadManager;


    /**
     * 下载队列中的id，可以通过该Id取消，重启任务等等
     */
    public long mTaskId;


    public DownLoadUtils(Context mCxt) {
        this.mCxt = mCxt;
    }

    /**
     * 下载资源
     *
     * @param path     路径
     * @param fileName 文件名称
     */
    public void downLoadRs(String path, String fileName) {
        //创建下载任务，path就是下载链接
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(path));
        request.setAllowedOverRoaming(false);//漫游网络是否可以下载，默认是true

        //设置文件类型，可以在下载结束后自动打开该文件
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String minmeStr = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap
                .getFileExtensionFromUrl(path));
        request.setMimeType(minmeStr);

        //在通知栏中显示，默认就是显示的
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setVisibleInDownloadsUi(true);


        //指定下载路径和下载文件名
        request.setDestinationInExternalPublicDir(localPath, fileName);

        //获取下载管理器
        downloadManager = (DownloadManager) mCxt.getSystemService(Context
                .DOWNLOAD_SERVICE);
        //将下载任务加入下载队列，否则不会进行下载
        mTaskId = downloadManager.enqueue(request);
        //注册广播接收者，监听下载状态
        mCxt.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //检查下载状态
            checkDownLoadStatus();
        }
    };

    /**
     * 检查下载状态
     */
    private void checkDownLoadStatus() {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(mTaskId);//筛选下载任务，传入任务Id，可变参数
        Cursor c = downloadManager.query(query);
        if (c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                case DownloadManager.STATUS_PAUSED:
                    //下载暂停
                    break;
                case DownloadManager.STATUS_PENDING:
                    //下载延迟
                    break;
                case DownloadManager.STATUS_RUNNING:
                    //正在下载
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    //下载完成安装apk
                    installApk(new File(localPath));
                    break;
                case DownloadManager.STATUS_FAILED:
                    //下载失败
                    break;
            }
        }
    }

    /**
     * 下载到本地后执行安装
     *
     * @param file
     */
    protected void installApk(File file) {
        if (!file.exists()) return;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse("file://" + file.toString());
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        //在服务中开启activity必须设置flag
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mCxt.startActivity(intent);
    }
}
