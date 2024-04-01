package com.example.btl_android.utilities;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.btl_android.models.TodoListNote;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import com.google.gson.reflect.TypeToken;

public class Helpers<T> {
    public String transferObjectToJson(T object) {
        Gson gson = new Gson();
        return gson.toJson(object);
    }

    public static boolean WriteFile(Context context, String fileName, String json) {
        // Get Directory via PrefereceManager
        PreferenceManager preferenceManager = new PreferenceManager(context);
        String directory = preferenceManager.getString(Constants.SETTINGS_STORAGE_LOCATION);

        File file = new File(directory, fileName);

        //      Attempt to Write File
        try
        {
            FileOutputStream stream = new FileOutputStream(file);

            stream.write(json.getBytes());
            stream.close();
        }
        catch (IOException e)
        {
            Log.d("AttachableNoteTemp", "File not written");
            Log.d("AttachableNoteTemp", "Dir: " + file.getAbsolutePath() + ", Name: " + fileName);

            Toast.makeText(context, "Note can not be written, please give adequate permissions!", Toast.LENGTH_SHORT).show();

            e.printStackTrace();

            return false;
        }

        return true;
    }

    public ArrayList<T> ReadFile(Context context, String fileName, Class<T> clazz) {
        // Get Directory via PreferenceManager
        Log.d("Fix me", "ReadFile: ");
        PreferenceManager preferenceManager = new PreferenceManager(context);
        String directory = preferenceManager.getString(Constants.SETTINGS_STORAGE_LOCATION);

        File file = new File(directory, fileName);

        try {
            FileInputStream stream = new FileInputStream(file);

            BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String readFileGradually;

            while ((readFileGradually = br.readLine()) != null) {
                sb.append(readFileGradually);
                sb.append('\n');
            }

            String json = sb.toString();

            Gson gson = new Gson();

            Type type = new ParameterizedTypeImpl(ArrayList.class, new Class[]{clazz});
            ArrayList<T> object = gson.fromJson(json, type);

            if (object == null) {
                Log.d("AttachableNoteTemp", "File not read");
                Log.d("AttachableNoteTemp", "Dir: " + file.getAbsolutePath() + ", Name: " + fileName);

                Toast.makeText(context, "ERROR: Note reading failed!", Toast.LENGTH_SHORT).show();

                return null;
            }
            Log.d("AttachableNoteTemp", "ReadFile: done");

            return object;
        } catch (IOException e) {
            Log.d("AttachableNoteTemp", "File not pre-read");
            Log.d("AttachableNoteTemp", "Dir: " + file.getAbsolutePath() + ", Name: " + fileName);

            Toast.makeText(context, "Note can not be read, please give adequate permissions!", Toast.LENGTH_SHORT).show();

            e.printStackTrace();

            return null;
        }
    }

    static class ParameterizedTypeImpl implements ParameterizedType {
        private final Class raw;
        private final Type[] args;

        ParameterizedTypeImpl(Class raw, Type[] args) {
            this.raw = raw;
            this.args = args != null ? args : new Type[0];
        }

        public Type[] getActualTypeArguments() {
            return args;
        }

        public Type getRawType() {
            return raw;
        }

        public Type getOwnerType() {
            return null;
        }
    }

}
