package com.sp.stefanli.similarphoto; /**
 * Created by stefanli on 22/7/15.
 */

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.media.ThumbnailUtils;
import android.net.Uri;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class BitmapUtil {
    public static double calSimilarity(Bitmap bmp1, Bitmap bmp2) {
        // 位图转换成灰度
        bmp1 = toGrayscale(bmp1);
        bmp2 = toGrayscale(bmp2);
        // 生成缩略图
        bmp1 = ThumbnailUtils.extractThumbnail(bmp1, 32, 32);
        bmp2 = ThumbnailUtils.extractThumbnail(bmp2, 32, 32);
        // 获取灰度像素数组
        int[] pixels1 = new int[bmp1.getWidth() * bmp1.getHeight()];
        int[] pixels2 = new int[bmp2.getWidth() * bmp2.getHeight()];
        bmp1.getPixels(pixels1, 0, bmp1.getWidth(), 0, 0, bmp1.getWidth(), bmp1.getHeight());
        bmp2.getPixels(pixels2, 0, bmp2.getWidth(), 0, 0, bmp2.getWidth(), bmp2.getHeight());
        // 获取平均灰度颜色
        int averageColor1 = getAverageOfPixelArray(pixels1);
        int averageColor2 = getAverageOfPixelArray(pixels2);
        // 获取图像指纹序列
        int[] p1 = getPixelDeviateWeightsArray(pixels1, averageColor1);
        int[] p2 = getPixelDeviateWeightsArray(pixels2, averageColor2);
        // 获取两个图的汉明距离（假设另一个图也已经按上面步骤得到灰度比较数组）
        int hammingDistance = getHammingDistance(p1, p2);
        double similarity = calSimilarity(hammingDistance);
        return similarity;
    }

    public static Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height,
                Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    // 获取灰度图的平均像素颜色值
    public static int getAverageOfPixelArray(int[] pixels) {
        long sumRed = 0;
        for (int i = 0; i < pixels.length; i++) {
            sumRed += Color.red(pixels[i]);
        }
        int averageRed = (int) (sumRed / pixels.length);
        return averageRed;
    }

    // 获取灰度图的像素比较数组（平均值的离差）
    public static int[] getPixelDeviateWeightsArray(int[] pixels,
                                                    final int averageColor) {
        int[] dest = new int[pixels.length];
        for (int i = 0; i < pixels.length; i++) {
            dest[i] = Color.red(pixels[i]) - averageColor > 0 ? 1 : 0;
        }
        return dest;
    }

    // 获取两个缩略图的平均像素比较数组的汉明距离（距离越大差异越大）
    public static int getHammingDistance(int[] a, int[] b) {
        int sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i] == b[i] ? 0 : 1;
        }
        return sum;
    }

    // 通过汉明距离计算相似度
    public static double calSimilarity(int hammingDistance) {
        int length = 32 * 32;
        double similarity = (length - hammingDistance) / (double) length;
        // 使用指数曲线调整相似度结果
        similarity = java.lang.Math.pow(similarity, 2);
        return similarity;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float)height / (float)reqHeight);
            } else {
                inSampleSize = Math.round((float)width / (float)reqWidth);
            }
        }
        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromUri(ContentResolver cr, Uri url, int reqWidth, int reqHeight)
            throws FileNotFoundException, IOException {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        InputStream input = cr.openInputStream(url);
        Bitmap bitmap = BitmapFactory.decodeStream(input);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeStream(input);
        input.close();
        return bitmap;
    }

}
