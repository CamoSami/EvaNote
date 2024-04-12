package com.example.btl_android.models;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.example.btl_android.utilities.Constants;
import com.example.btl_android.utilities.PreferenceManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;


public class PrivateNote extends _DefaultNote{
    private final static String TOKEN_KEY = "123456781234567812345678";

    public PrivateNote(){}

    public PrivateNote(PrivateNote privateNote) {
        super(privateNote);
    }

    public PrivateNote(String fileName, String title, Date dateCreated, Date dateModified,
                       String content,
                       boolean isFavorite)
    {
        super(fileName, title, dateCreated, dateModified, content, isFavorite);
    }

    public boolean Validate()
    {
        return (!Objects.equals(title, "") || !Objects.equals(content, ""));
    }

    @Override
    public boolean WriteToStorage(Context context, boolean isDuplicated)
    {
        //      Initiate
        Map<String, String> noteMap = new HashMap<>();
        SimpleDateFormat longDateFormat = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss", Locale.getDefault()); //Định dạng thời gian

        //      Write Defaults
        noteMap.put(Constants.JSON_DEFAULT_TITLE, this.getTitle());
        noteMap.put(Constants.JSON_DEFAULT_DATE_CREATED, longDateFormat.format(this.getDateCreated()));
        noteMap.put(Constants.JSON_DEFAULT_DATE_MODIFIED, longDateFormat.format(this.getDateModified()));
        noteMap.put(Constants.JSON_DEFAULT_CONTENT, encrypt(this.getContent()));
        noteMap.put(Constants.JSON_DEFAULT_FAVORITE, String.valueOf(this.isFavorite()));

        //      Get GSON class to write Map<> -> JSON
        Gson gson = new Gson();
        String json = gson.toJson(noteMap);

        //      Get Directory via PrefereceManager
        PreferenceManager preferenceManager = new PreferenceManager(context);
        String directory = preferenceManager.getString(Constants.SETTINGS_STORAGE_LOCATION);
        File file;
        int i = 1;

        if (this.fileName == null)
        {
            //      TODO: File + Date Format + .txt
            this.fileName = "PrivateNote" + longDateFormat.format(this.getDateCreated()) + ".txt";

            file = new File(directory, this.fileName);

            try
            {
                while (!file.createNewFile()) {
                    this.fileName = "PrivateNote" + longDateFormat.format(this.getDateCreated()) + "(" + i++ + ").txt";

                    file = new File(directory, this.fileName);
                }
            }
            catch (IOException e)
            {
                Log.d("PrivateNoteTemp", "File not created");
                Log.d("PrivateNoteTemp", "Dir: " + file.getAbsolutePath() + ", Name: " + this.fileName);

                Toast.makeText(context, "Note can not be created, please give adequate permissions!", Toast.LENGTH_SHORT).show();

                e.printStackTrace();

                return false;
            }
        }
        else if (isDuplicated)
        {
            //      Duplication!
            String regex = "\\([0-9]+\\)\\.txt";
            String fileName = this.fileName.substring(this.fileName.lastIndexOf(".") - 3, this.fileName.length());

            Pattern pattern = Pattern.compile(regex, Pattern.UNICODE_CASE);
            Matcher matcher = pattern.matcher(fileName);

            if (matcher.matches())
            {
//				Log.d("AttachableNoteTemp", "Duplication: " + this.fileName + " -> " + this.fileName.substring(0, this.fileName.lastIndexOf(".") - 3));

                fileName = this.fileName.substring(0, this.fileName.lastIndexOf(".") - 3);
            }
            else
            {
//				Log.d("AttachableNoteTemp", "Duplication: " + this.fileName + " -> " + this.fileName.substring(0, this.fileName.lastIndexOf(".")));

                fileName =  this.fileName.substring(0, this.fileName.lastIndexOf("."));
            }

            //      Create a New File with Similar Name
            file = new File(directory, this.fileName = fileName + "(" + i++ + ").txt");
            try
            {
                while (!file.createNewFile()) {
                    this.fileName = fileName + "(" + i++ + ").txt";

                    file = new File(directory, this.fileName);
                }
            }
            catch (IOException e)
            {
                Log.d("PrivateNoteTemp", "File not created");
                Log.d("PrivateNoteTemp", "Dir: " + file.getAbsolutePath() + ", Name: " + this.fileName);

                Toast.makeText(context, "Note can not be created, please give adequate permissions!", Toast.LENGTH_SHORT).show();

                e.printStackTrace();

                return false;
            }
        }
        else
        {
            file = new File(directory, this.fileName);
        }

        //      Attempt to Write File
        try
        {
            FileOutputStream stream = new FileOutputStream(file);

            stream.write(json.getBytes());
            stream.close();
        }
        catch (IOException e)
        {
            Log.d("PrivateNoteTemp", "File not written");
            Log.d("PrivateNoteTemp", "Dir: " + file.getAbsolutePath() + ", Name: " + this.fileName);

            Toast.makeText(context, "Note can not be written, please give adequate permissions!", Toast.LENGTH_SHORT).show();

            e.printStackTrace();

            return false;
        }

        return true;
    }

    public static PrivateNote ReadFromStorage(Context context, String fileName)
    {
        //      Initiate
        TypeToken<Map<String, String>> typeToken = new TypeToken<Map<String, String>>() {};
        SimpleDateFormat longDateFormat = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss", Locale.getDefault());
        Gson gson = new Gson();

        PreferenceManager preferenceManager = new PreferenceManager(context);
        String directory = preferenceManager.getString(Constants.SETTINGS_STORAGE_LOCATION);
        File file = new File(directory, fileName);
        String readFile;
        try {
            FileInputStream stream = new FileInputStream(file);

            BufferedReader br = new BufferedReader( new InputStreamReader(stream, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String readFileGradually;

            while(( readFileGradually = br.readLine()) != null ) {
                sb.append( readFileGradually );
                sb.append( '\n' );
            }

            readFile = sb.toString();
        }
        catch (IOException e)
        {
            Log.d("PrivateNoteTemp", "File not pre-read");
            Log.d("PrivateNoteTemp", "Dir: " + file.getAbsolutePath() + ", Name: " + fileName);

            Toast.makeText(context, "Note can not be read, please give adequate permissions!", Toast.LENGTH_SHORT).show();

            e.printStackTrace();

            return null;
        }

        Map<String, String> noteMap = gson.fromJson(readFile, typeToken.getType());

        if (noteMap == null) {
            Log.d("AttachableNoteTemp", "File not read");
            Log.d("AttachableNoteTemp", "Dir: " + file.getAbsolutePath() + ", Name: " + fileName);

            Toast.makeText(context, "ERROR: Note reading failed!", Toast.LENGTH_SHORT).show();

            return null;
        }

        //      Initiate
        PrivateNote note = new PrivateNote();

        try {
            //      Read Default
            note.setFileName(fileName);
            note.setTitle(noteMap.get(Constants.JSON_DEFAULT_TITLE));
            note.setDateCreated(longDateFormat.parse(noteMap.get(Constants.JSON_DEFAULT_DATE_CREATED)));
            note.setDateModified(longDateFormat.parse(noteMap.get(Constants.JSON_DEFAULT_DATE_MODIFIED)));
            note.setFavorite(Boolean.parseBoolean(noteMap.get(Constants.JSON_DEFAULT_FAVORITE)));

            String EncryptedContent = noteMap.get(Constants.JSON_DEFAULT_CONTENT);
            String DecryptedContent = decrypt(EncryptedContent);
            note.setContent(DecryptedContent);

            return note;
        }
        catch (Exception e)
        {
            Log.d("PrivateNoteTemp", "File not post-read");
            Log.d("PrivateNoteTemp", "Dir: " + file.getAbsolutePath() + ", Name: " + fileName);

            Toast.makeText(context, "ERROR: Note post-reading failed!", Toast.LENGTH_SHORT).show();

            e.printStackTrace();

            return null;
        }
    }

    public static String encrypt(String plain){
        try {
            byte[] iv = new byte[16];
            new SecureRandom().nextBytes(iv); //Hàm tạo các giá trị ngẫu nhiên an toàn
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE,new SecretKeySpec(TOKEN_KEY.getBytes("utf-8"),
                    "AES"), new IvParameterSpec(iv));
            byte[] cipherText = cipher.doFinal(plain.getBytes("utf-8"));
            byte[] ivAndCipherText = getCombineArray(iv, cipherText);
            return Base64.encodeToString(ivAndCipherText, Base64.NO_WRAP);

        } catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }
    public static String decrypt(String encoded){
        try{
            // Base64.decode: Giải mã,
            byte[] ivAndCipherText = Base64.decode(encoded,Base64.NO_WRAP); // Loại bỏ kí tự xuống dòng trong chuỗi
            byte[] iv = Arrays.copyOfRange(ivAndCipherText, 0, 16);
            byte[] cipherText = Arrays.copyOfRange(ivAndCipherText, 16, ivAndCipherText.length);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(TOKEN_KEY.getBytes("utf-8"),
                    "AES"),new IvParameterSpec(iv));
            return new String (cipher.doFinal(cipherText), "utf-8");
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static byte[] getCombineArray(byte[] one, byte[] two){
        byte[] combined = new byte[one.length + two.length];
        for(int i = 0; i < combined.length; ++i){
            combined[i] = i < one.length ? one[i] : two[i-one.length];
        }
        return  combined;
    }
}
