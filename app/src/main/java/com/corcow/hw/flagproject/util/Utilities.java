package com.corcow.hw.flagproject.util;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.corcow.hw.flagproject.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by HYUNWOO on 2016-05-06.
 */
public class Utilities {



    public static String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
    }

    public static Bitmap getThumbnail(Context context, String path) {
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] { MediaStore.MediaColumns._ID }, MediaStore.MediaColumns.DATA + "=?",
                new String[] { path }, null);
        if (cursor != null && cursor.moveToFirst())
        {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(), id, MediaStore.Images.Thumbnails.MICRO_KIND, null);
        }
        cursor.close();
        return null;
    }

    public static String getThumnailPath(Context context, String path)
    {
        String result = null;
        long imageId = -1;
        try
        {
            String[] projection = new String[] { MediaStore.MediaColumns._ID };
            String selection = MediaStore.MediaColumns.DATA + "=?";
            String[] selectionArgs = new String[] { path };
            Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst())
            {
                imageId = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            }
            cursor.close();

            cursor = MediaStore.Images.Thumbnails.queryMiniThumbnail(context.getContentResolver(), imageId, MediaStore.Images.Thumbnails.MINI_KIND, null);
            if (cursor != null && cursor.getCount() > 0)
            {
                cursor.moveToFirst();
                result = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.DATA));
            }
            cursor.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(result))
            result = path;

        return result;
    }

    /** openFile()  ... 파일 실행
     * @param selectedFile : 실행하고자 하는 파일
     *
     * .. 안드로이드는 확장자(extension)로 파일을 구분하여 실행하지 못하고, 파일 타입을 "MIME TYPE"으로 구분.
     * .. 파일 확장자(extension)와 MIME TYPE이 대응되는 MimeTypeMap 이라는 MAP이 존재함.
     */
    public static void openFile(Context context, File selectedFile) {
        MimeTypeMap myMime = MimeTypeMap.getSingleton();                        // MIME TYPE & extension map
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String fileExtension = Utilities.getExtension(selectedFile.getName());

        // MimeTypeMap에서 확장자에 해당하는 MIME type을 가져옴.
        String mimeType = myMime.getMimeTypeFromExtension(fileExtension);
        // MimeTypeMap에 없는녀석은 직접 MIME을 mapping.
        if (TextUtils.isEmpty(mimeType)) {
            if (fileExtension.equalsIgnoreCase("xlsx") || fileExtension.equalsIgnoreCase("xls") || fileExtension.equalsIgnoreCase("xlsm"))
                mimeType = "application/vnd.ms-excel";
            else if (fileExtension.equalsIgnoreCase("doc") || fileExtension.equalsIgnoreCase("docx"))
                mimeType = "application/msword";
            else if (fileExtension.equalsIgnoreCase("hwp"))
                mimeType = "application/hwp";
            else if (fileExtension.equalsIgnoreCase("ppt") || fileExtension.equalsIgnoreCase("pptx"))
                mimeType = "application/vnc.ms-powerpoint";
        }

        intent.setDataAndType(Uri.fromFile(selectedFile), mimeType);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No handler for this type of file.", Toast.LENGTH_LONG).show();
        }
    }

    /** moveFile()  ... 파일 이동
     * @param originPath : 이동 전 파일/폴더 경로 (Absolute Path)
     * @param newPath : 이동 후 파일/폴더 경로 (Absolute Path)
     */
    public static void moveFile(String originPath, String newPath) {
        InputStream in = null;
        OutputStream out = null;
        try {
            //create output directory if it doesn't exist
            File originFile = new File(originPath);
            if (originFile.isDirectory()) {
                // 옮기려는 파일이 폴더인 경우
                File[] originFiles = originFile.listFiles();        // 폴더 내의 파일들
                for (File childFile : originFiles) {                        // 순회
                    // 회심의 recursive !!
                    moveFile(childFile.getAbsolutePath(), newPath + "/" + originFile.getName());
                    childFile.delete();
                }
                originFile.delete();                            // 기존 폴더 삭제
            } else {
                // 옮기려는 파일이 단일 파일인 경우
                File newFile = new File (newPath);          // 생성할 파일
                if (!newFile.exists())                      // 디렉토리가없다면
                    newFile.mkdirs();                       // 디렉토리 만들기
                in = new FileInputStream(originPath);
                out = new FileOutputStream(newPath + "/" + originFile.getName());

                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                in.close();
                in = null;

                // write the output file
                out.flush();
                out.close();
                out = null;

                // 기존 위치의 파일 삭제
                new File(originPath).delete();
            }
        }
        catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
    }


    public static void DeleteDir (String path)
    {
        File file = new File(path);
        File[] childFileList = file.listFiles();
        for(File childFile : childFileList)
        {
            if(childFile.isDirectory()) {
                DeleteDir(childFile.getAbsolutePath());     //하위 디렉토리 루프
            }
            else {
                childFile.delete();    //하위 파일삭제
            }
        }
        file.delete();    //root 삭제
    }



    public static File downloadFile (Context context, String url, String filename) {
        File direct = new File(Environment.getExternalStorageDirectory() + "/" + "FLAG");

        if (!direct.exists()) {
            direct.mkdirs();
        } else {
            File file = new File(Environment.getExternalStorageDirectory() + "/" + "FLAG" + "/" + filename);
            if(file.exists())
                file.delete();
        }

        DownloadManager mgr = (DownloadManager) context.getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);

        Uri downloadUri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(downloadUri);

        request.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false).setTitle(context.getResources().getString(R.string.app_name))
                .setDescription("Downloading Share Image for " + context.getResources().getString(R.string.app_name))
                .setDestinationInExternalPublicDir("/" + "FLAG", filename);

        mgr.enqueue(request);
        return direct;
    }

    public static void deleteFile(String inputPath, String inputFile) {
        try {
            // delete the original file
            new File(inputPath + inputFile).delete();
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
    }

    public static void copyFile(String inputPath, String inputFile, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {
            //create output directory if it doesn't exist
            File dir = new File (outputPath);
            if (!dir.exists())
            {
                dir.mkdirs();
            }


            in = new FileInputStream(inputPath + inputFile);
            out = new FileOutputStream(outputPath + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;

        }  catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

    }
}
