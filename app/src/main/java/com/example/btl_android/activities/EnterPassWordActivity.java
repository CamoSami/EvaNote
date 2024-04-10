package com.example.btl_android.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.impl.model.Preference;

import com.example.btl_android.R;
import com.example.btl_android.databinding.ActivityEnterPassWordBinding;
import com.example.btl_android.utilities.Constants;
import com.example.btl_android.utilities.PreferenceManager;

import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EnterPassWordActivity
		extends AppCompatActivity
{
    private ActivityEnterPassWordBinding binding;
    private final static String TOKEN_KEY = "123456781234567812345678";
    private String fileName = null;

    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.binding = ActivityEnterPassWordBinding.inflate(getLayoutInflater());
        this.setContentView(this.binding.getRoot());

        this.SetListeners();

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle == null)
        {
            return;
        }

        this.fileName = bundle.getString(Constants.BUNDLE_FILENAME_KEY);
    }

    private void SetListeners()
    {
        this.binding.backButton.setOnClickListener(view ->
        {
            finish();
        });

        this.binding.EPEdtPassword.setOnTouchListener(new View.OnTouchListener()
        {
            @Override public boolean onTouch(View v, MotionEvent event)
            {
                return true; // Ngăn chặn sự kiện chạm vào EditText
            }
        });

        this.binding.EPEdtPassword.setOnKeyListener(new View.OnKeyListener()
        {
            @Override public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                return true; // Ngăn chặn sự kiện phím được nhấn trên EditText
            }
        });

        this.binding.EPBtn0.setOnClickListener(view ->
        {
            if (this.binding.EPEdtPassword.length() == 6)
            {
                this.binding.EPEdtPassword.setText(
                        this.binding.EPEdtPassword.getText().toString().substring(0, 6));

                return;
            }

            this.binding.EPEdtPassword.setText(
                    this.binding.EPEdtPassword.getText().toString() +
                            "0"
            );
        });

        this.binding.EPBtn1.setOnClickListener(view ->
        {
            if (this.binding.EPEdtPassword.length() == 6)
            {
                this.binding.EPEdtPassword.setText(
                        this.binding.EPEdtPassword.getText().toString().substring(0, 6));

                return;
            }

            this.binding.EPEdtPassword.setText(
                    this.binding.EPEdtPassword.getText().toString() +
                            "1"
            );
        });

        this.binding.EPBtn2.setOnClickListener(view ->
        {
            if (this.binding.EPEdtPassword.length() == 6)
            {
                this.binding.EPEdtPassword.setText(
                        this.binding.EPEdtPassword.getText().toString().substring(0, 6));

                return;
            }

            this.binding.EPEdtPassword.setText(
                    this.binding.EPEdtPassword.getText().toString() +
                            "2"
            );
        });

        this.binding.EPBtn3.setOnClickListener(view ->
        {
            if (this.binding.EPEdtPassword.length() == 6)
            {
                this.binding.EPEdtPassword.setText(
                        this.binding.EPEdtPassword.getText().toString().substring(0, 6));
                return;
            }

            this.binding.EPEdtPassword.setText(
                    this.binding.EPEdtPassword.getText().toString() +
                            "3"
            );
        });

        this.binding.EPBtn4.setOnClickListener(view ->
        {
            if (this.binding.EPEdtPassword.length() == 6)
            {
                this.binding.EPEdtPassword.setText(
                        this.binding.EPEdtPassword.getText().toString().substring(0, 6));

                return;
            }

            this.binding.EPEdtPassword.setText(
                    this.binding.EPEdtPassword.getText().toString() +
                            "4"
            );
        });

        this.binding.EPBtn5.setOnClickListener(view ->
        {
            if (this.binding.EPEdtPassword.length() == 6)
            {
                this.binding.EPEdtPassword.setText(
                        this.binding.EPEdtPassword.getText().toString().substring(0, 6));
                return;
            }

            this.binding.EPEdtPassword.setText(
                    this.binding.EPEdtPassword.getText().toString() +
                            "5"
            );
        });

        this.binding.EPBtn6.setOnClickListener(view ->
        {
            if (this.binding.EPEdtPassword.length() == 6)
            {
                this.binding.EPEdtPassword.setText(
                        this.binding.EPEdtPassword.getText().toString().substring(0, 6));

                return;
            }

            this.binding.EPEdtPassword.setText(
                    this.binding.EPEdtPassword.getText().toString() +
                            "6"
            );
        });

        this.binding.EPBtn7.setOnClickListener(view ->
        {
            if (this.binding.EPEdtPassword.length() == 6)
            {
                this.binding.EPEdtPassword.setText(
                        this.binding.EPEdtPassword.getText().toString().substring(0, 6));
                return;
            }

            this.binding.EPEdtPassword.setText(
                    this.binding.EPEdtPassword.getText().toString() +
                            "7"
            );
        });

        this.binding.EPBtn8.setOnClickListener(view ->
        {
            if (this.binding.EPEdtPassword.length() == 6)
            {
                this.binding.EPEdtPassword.setText(
                        this.binding.EPEdtPassword.getText().toString().substring(0, 6));

                return;
            }

            this.binding.EPEdtPassword.setText(
                    this.binding.EPEdtPassword.getText().toString() +
                            "8"
            );
        });

        this.binding.EPBtn9.setOnClickListener(view ->
        {
            if (this.binding.EPEdtPassword.length() == 6)
            {
                this.binding.EPEdtPassword.setText(
                        this.binding.EPEdtPassword.getText().toString().substring(0, 6));
                return;
            }

            this.binding.EPEdtPassword.setText(
                    this.binding.EPEdtPassword.getText().toString() +
                            "9"
            );
        });

        this.binding.EPBtnDelete.setOnClickListener(view ->
        {
            this.binding.EPEdtPassword.setText("");
        });

        this.binding.EPBtnDone.setOnClickListener(view ->
        {
            // Khởi tạo đối tượng SharedPreferences
            PreferenceManager preferenceManager = new PreferenceManager(this);

            // Lấy dữ liệu từ SharedPreferences
            String oldPass = preferenceManager.getString(Constants.PRIVATE_NOTE_PASSWORD);
            oldPass = decrypt(oldPass);

            if (oldPass.length() < 6)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("Error").setMessage(
                                "The file containing the password has been deleted. Please create a new password")
                        .setPositiveButton("Update password", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int whichButton)
                            {
                                Intent intent = new Intent(EnterPassWordActivity.this,
                                        CreateOrChangePasswordActivity.class
                                );

                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Nah", null).create().show();
            }

            if (this.binding.EPEdtPassword.getText().toString().equals(oldPass))
            {
                Intent intent = new Intent(this, PrivateNoteActivity.class);

                if (this.fileName != null)
                {
                    intent.putExtra(Constants.BUNDLE_FILENAME_KEY, this.fileName);
                }

                startActivity(intent);

                finish();
            }
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Error");
                builder.setMessage("Password does not match. Please try again");

                builder.setNegativeButton("OK", null);

                builder.create().show(); //Dialog được tạo và hiện lên màn hình
            }
        });

        this.binding.settingsButton.setOnClickListener(view ->
        {
            PopupMenu popupMenu = new PopupMenu(view.getContext(), this.binding.settingsButton);

            popupMenu.getMenuInflater().inflate(R.menu.menu_enter_pass_word, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
            {
                @Override public boolean onMenuItemClick(MenuItem menuItem)
                {
                    int id = menuItem.getItemId();

                    if (id == R.id.menuItemForgetPassword)
                    {
                        Intent intent = new Intent(EnterPassWordActivity.this,
                                CreateOrChangePasswordActivity.class
                        );

                        startActivity(intent);

                        finish();
                    }

                    return true;
                }
            });

            // Showing the popup menu
            popupMenu.show();
        });
    }

    public static String decrypt(String encoded)
    {
        try
        {
            // Base64.decode: Giải mã,
            byte[] ivAndCipherText = Base64.decode(encoded, Base64.NO_WRAP); // Loại bỏ kí tự xuống dòng trong chuỗi
            byte[] iv = Arrays.copyOfRange(ivAndCipherText, 0, 16);
            byte[] cipherText = Arrays.copyOfRange(ivAndCipherText, 16, ivAndCipherText.length);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(TOKEN_KEY.getBytes("utf-8"), "AES"),
                    new IvParameterSpec(iv));

            return new String(cipher.doFinal(cipherText), "utf-8");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}