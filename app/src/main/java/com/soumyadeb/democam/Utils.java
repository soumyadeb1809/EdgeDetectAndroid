package com.soumyadeb.democam;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;

public class Utils {


    /**
     * check directory whether exist, if not then make one;
     * @param path absolute path of directory
     * @return
     */
    public static boolean checkDir(String path) {
        boolean result = true;
        File f = new File(path);
        if (!f.exists()){
            result = f.mkdirs();
        }else if (f.isFile()) {
            f.delete();
            result = f.mkdirs();
        }
        return result;
    }

    public static Bitmap loadBitmap(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap b = null;
        try {
            options.inSampleSize = 1;
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            if (options.mCancel || options.outWidth == -1
                    || options.outHeight == -1) {
                return null;
            }
            options.inSampleSize = 2;
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            b = BitmapFactory.decodeFile(path, options);
            if(b==null)
                return null;

            int orientation=getOrientation(path);
            if(orientation!=1){
                Matrix m = new Matrix();
                m.postRotate(getRotation(orientation));
                b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, false);
            }

        } catch (OutOfMemoryError ex) {
            ex.printStackTrace();
            System.gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    /**
     * get rotation degrees
     * @param orientation values in {1, 3, 6, 8}
     * @return values in {0, 90, 180, 270}
     */
    private static int getRotation(int orientation){
        switch(orientation){
            case 1:
                return 0;
            case 8:
                return 270;
            case 3:
                return 180;
            case 6:
                return 90;
            default :
                return 0;
        }
    }
    /**
     * read orientation from Image file
     * @param file
     * @return
     */
    private static int getOrientation(String file){
        int orientation=1;
        ExifInterface exif;
        if(!TextUtils.isEmpty(file)){
            try {
                exif = new ExifInterface(file);
                orientation=exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }catch(ExceptionInInitializerError e){
                e.printStackTrace();
            }
        }
        return orientation;
    }

}
