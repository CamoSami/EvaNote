package com.example.btl_android.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.example.btl_android.databinding.ActivityCreateOrChangePasswordActivityBinding;
import com.example.btl_android.utilities.Constants;
import com.example.btl_android.utilities.PreferenceManager;

import java.security.SecureRandom;
import java.util.concurrent.Executor;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class CreateOrChangePasswordActivity
		extends AppCompatActivity
{
    private ActivityCreateOrChangePasswordActivityBinding binding;
    private final static String TOKEN_KEY = "123456781234567812345678";
    private String newPass, confirmedPass;
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;

    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.binding = ActivityCreateOrChangePasswordActivityBinding.inflate(getLayoutInflater());
        setContentView(this.binding.getRoot());
        Intent intent = this.getIntent();
        this.SetListeners();
    }

    private void SetListeners()
    {
        this.binding.backButton.setOnClickListener(view ->
        {
            finish();
        });

        this.binding.COCPBtnUpdate.setOnClickListener(view ->
        {
            newPass = this.binding.COCPEdtNewPass.getText().toString();
            confirmedPass = this.binding.COCPEdtConfirmedPass.getText().toString();

            if (newPass == null)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Error");
                builder.setMessage("Enter your new password !");
                builder.setPositiveButton("OK", null);
                builder.create().show();
                return;
            }

            if (newPass.length() < 6)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Error");
                builder.setMessage("Your new password must be 6 digits ");
                builder.setPositiveButton("OK", null);
                builder.create().show();
                this.binding.COCPEdtNewPass.setText("");
                this.binding.COCPEdtConfirmedPass.setText("");
                return;
            }

            if (newPass.compareTo(confirmedPass) != 0)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Error");
                builder.setMessage("Your confirmed password doesn't match");
                builder.setPositiveButton("OK", null);
                builder.create().show();
                this.binding.COCPEdtNewPass.setText("");
                this.binding.COCPEdtConfirmedPass.setText("");

                return;
            }

            //Sử dụng sinh trắc học
            BiometricManager biometricManager = BiometricManager.from(this);

            //Kiểm tra khả năng sử dụng sinh trắc học trên thiết bị
            switch (biometricManager.canAuthenticate())
            {
                //Thiết bị không hỗ trợ sinh trắc học
                case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                    Toast.makeText(getApplicationContext(), "Device doesn't have fingerprint",
                            Toast.LENGTH_SHORT
                    ).show();

                    return;

                //Xác thực sinh trắc học không khả dụng ở thời điểm hiện tại
                case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                    Toast.makeText(getApplicationContext(), "No Working", Toast.LENGTH_SHORT).show();

                    return;

                //Không có vân tay nào được đăng ký ở thiết bị
                case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                    Toast.makeText(getApplicationContext(), "No FingerPrint Assigned",
                            Toast.LENGTH_SHORT
                    ).show();

                    return;
            }

            Executor executor = ContextCompat.getMainExecutor(this);

            biometricPrompt = new BiometricPrompt(CreateOrChangePasswordActivity.this, executor,
                    new BiometricPrompt.AuthenticationCallback()
                    {
                        @Override
                        public void onAuthenticationError(int errorCode, @NonNull CharSequence errString
                        )
                        {
                            super.onAuthenticationError(errorCode, errString);
                        }

                        @Override public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result
                        )
                        {
                            super.onAuthenticationSucceeded(result);

                            Toast.makeText(getApplicationContext(), "Login Success",
                                    Toast.LENGTH_SHORT
                            ).show();

                            //Lưu pass
                            newPass = encrypt(newPass);

                            PreferenceManager preferenceManager =
                                    new PreferenceManager(CreateOrChangePasswordActivity.this);
                            preferenceManager.putString(Constants.PRIVATE_NOTE_PASSWORD, newPass);

                            //Quay về CreateNoteActivity
                            Intent intent = new Intent(CreateOrChangePasswordActivity.this,
                                    CreateNoteActivity.class
                            );

                            startActivity(intent);

                            CreateOrChangePasswordActivity.this.finish();
                        }

                        @Override public void onAuthenticationFailed()
                        {
                            super.onAuthenticationFailed();
                            Toast.makeText(getApplicationContext(), "Fingerprints do not match",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
            );

            promptInfo = new BiometricPrompt.PromptInfo.Builder().setTitle(
                    "EvaNote").setDescription("Use FingerPrint To Change Password").setDeviceCredentialAllowed(true).build();

            biometricPrompt.authenticate(promptInfo);
        });
    }

    public static String encrypt(String plain)
    {
        try
        {
            byte[] iv = new byte[16];
            new SecureRandom().nextBytes(iv); //Hàm tạo các giá trị ngẫu nhiên an toàn
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(TOKEN_KEY.getBytes("utf-8"), "AES"),
                    new IvParameterSpec(iv)
            );
            byte[] cipherText = cipher.doFinal(plain.getBytes("utf-8"));
            byte[] ivAndCipherText = getCombineArray(iv, cipherText);
            return Base64.encodeToString(ivAndCipherText, Base64.NO_WRAP);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }

    }

    private static byte[] getCombineArray(byte[] one, byte[] two)
    {
        byte[] combined = new byte[one.length + two.length];
        for (int i = 0; i < combined.length; ++i)
        {
            combined[i] = i < one.length ? one[i] : two[i - one.length];
        }
        return combined;
    }
}