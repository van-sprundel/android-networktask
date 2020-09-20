package com.example.networktask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class NetworkResult {
    private final String TAG = NetworkResult.class.getSimpleName();

    //Private variables
    private final byte[] data;

    /**
     * Create new BinaryData instance
     *
     * @param data Binary data, containing Drawable or String
     */
    public NetworkResult(byte[] data) {
        this.data = data;
    }

    /**
     * Convert Drawable to BinaryData
     *
     * @param drawable The drawable
     */
    public NetworkResult(Drawable drawable) {
        if (drawable == null) {
            this.data = new byte[0];
            Log.e(TAG, "Drawable is NULL");
            return;
        }

        Bitmap bmp = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, os);
        this.data = os.toByteArray();
    }

    /**
     * Create new BinaryData object from String
     *
     * @param value The string
     */
    public NetworkResult(String value) {
        this.data = value.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Get String from inner byte[]
     *
     * @return String
     */
    @Override
    @NonNull
    public String toString() {
        InputStream is = new ByteArrayInputStream(this.data);
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    /**
     * Return JSONObject from data
     *
     * @return JSONObject
     */
    public JSONObject toJSONObject() throws JSONException {
        Log.d(TAG, "Parsing JSON: " + this.toString());
        return new JSONObject(this.toString());
    }

    public JSONArray toJSONArray() throws JSONException {
        Log.d(TAG, "Parsing JSON array: " + this.toString());
        return new JSONArray(this.toString());
    }

    /**
     * Get length
     *
     * @return Length of inner byte[]
     */
    public int length() {
        return this.data.length;
    }

    /**
     * Get drawable from inner byte[]
     *
     * @return Drawable
     */
    public Drawable toDrawable() {
        Log.d(TAG, "Converting to drawable");

        if (data.length == 0) {
            Log.e(TAG, "Data size must be greater than 0");
            return null;
        }

        InputStream is = new ByteArrayInputStream(this.data);
        return Drawable.createFromStream(is, "Useless");
    }

    public Bitmap toBitmap() {
        Log.d(TAG, "Converting to bitmap");

        if (data.length == 0) {
            Log.e(TAG, "Data size must be greater than 0");
            return null;
        }

        InputStream is = new ByteArrayInputStream(this.data);
        return BitmapFactory.decodeStream(is);
    }

    /**
     * Return inner byte[]
     *
     * @return byte[]
     */
    public byte[] getData() {
        return data;
    }

}
