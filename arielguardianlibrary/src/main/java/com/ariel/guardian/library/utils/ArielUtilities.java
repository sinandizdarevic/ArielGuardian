package com.ariel.guardian.library.utils;

import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.pubnub.api.vendor.Base64;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

import io.realm.RealmObject;
/**
 * Created by mikalackis on 23.5.16..
 * Class holding utility methods used throughout the app
 */
public class ArielUtilities {

    private static final String TAG = ArielUtilities.class.getName();

    public static final String UUID_PREFIX = "ariel";

    private static final String PUBNUB_ARIEL_CHANNEL = "ariel_%s";
    private static final String PUBNUB_USER_CHANNEL = "user_%s";

    public static String encodeAsFirebaseKey(final String toEncode) {
        return toEncode.replaceAll("\\.", "%2E");
    }

    public static Date timestampToDate(final long timestamp) {
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();//get your local time zone.
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        sdf.setTimeZone(tz);//set time zone.
        String localTime = sdf.format(new Date(timestamp * 1000));
        Date date = new Date();
        try {
            date = sdf.parse(localTime);//get local date
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String getPubNubArielChannel(final String deviceId) {
        Log.i(TAG,"Config topic: " + String.format(PUBNUB_ARIEL_CHANNEL, deviceId));
        return String.format(PUBNUB_ARIEL_CHANNEL, deviceId);
    }

    //support multiple devices
//    public static String getPubNubUserChanel(final String uuid){
//        Log.i(TAG,"Config topic: "+String.format(PUBNUB_USER_CHANNEL, uuid));
//        return String.format(PUBNUB_USER_CHANNEL, uuid);
//    }

    public static String getUniquePseudoID() {
        // If all else fails, if the user does have lower than API 9 (lower
        // than Gingerbread), has reset their device or 'Secure.ANDROID_ID'
        // returns 'null', then simply the ID returned will be solely based
        // off their Android device information. This is where the collisions
        // can happen.
        // Thanks http://www.pocketmagic.net/?p=1662!
        // Try not to use DISPLAY, HOST or ID - these items could change.
        // If there are collisions, there will be overlapping data
        String m_szDevIDShort = UUID_PREFIX + (Build.BOARD.length() % 10)
                + (Build.BRAND.length() % 10) + (Build.CPU_ABI.length() % 10)
                + (Build.DEVICE.length() % 10)
                + (Build.MANUFACTURER.length() % 10)
                + (Build.MODEL.length() % 10) + (Build.PRODUCT.length() % 10);

        // Thanks to @Roman SL!
        // http://stackoverflow.com/a/4789483/950427
        // Only devices with API >= 9 have android.os.Build.SERIAL
        // http://developer.android.com/reference/android/os/Build.html#SERIAL
        // If a user upgrades software or roots their device, there will be a
        // duplicate entry
        String serial = null;
        try {
            serial = Build.class.getField("SERIAL").get(null)
                    .toString();

            // Go ahead and return the serial for api => 9
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode())
                    .toString();
        } catch (Exception exception) {
            // String needs to be initialized
            serial = "serial"; // some value
        }

        // Thanks @Joe!
        // http://stackoverflow.com/a/2853253/950427
        // Finally, combine the values we have found by using the UUID class to
        // create a unique identifier
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode())
                .toString();
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .create();
    }

    private static String getEncodedData(final String deviceId, final String cipher, final String secret) {
        try {
            AesCbcWithIntegrity.SecretKeys keys = AesCbcWithIntegrity.generateKeyFromPassword(cipher, secret);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, 60);
            String toEnc = deviceId.concat("_").concat(calendar.getTimeInMillis() + "");
            AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMac = null;
            try {
                cipherTextIvMac = AesCbcWithIntegrity.encrypt(toEnc, keys);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            //store or send to server
            return cipherTextIvMac.toString();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getDecodedData(final String cipherTextString, final String cipher, final String secret) {
        try {
            AesCbcWithIntegrity.SecretKeys keys = AesCbcWithIntegrity.generateKeyFromPassword(cipher, secret);
            AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMac = new AesCbcWithIntegrity.CipherTextIvMac(cipherTextString);
            String text = null;
            try {
                text = AesCbcWithIntegrity.decryptString(cipherTextIvMac, keys);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return text;
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            return null;
        }
    }

    public final static int WHITE = 0xFFFFFFFF;
    public final static int BLACK = 0xFF000000;
    public final static int WIDTH = 200;
    public final static int HEIGHT = 200;

    public static Bitmap generateDeviceQRCode(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, WIDTH, HEIGHT, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }

        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    public static String base64Encode2String(Bitmap bitmap) {
        return Base64.encodeToString(bitmap2Bytes(bitmap, Bitmap.CompressFormat.PNG), Base64.NO_WRAP);
    }

    public static byte[] bitmap2Bytes(Bitmap bitmap, Bitmap.CompressFormat format) {
        if (bitmap == null) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(format, 100, baos);
        return baos.toByteArray();
    }


}
