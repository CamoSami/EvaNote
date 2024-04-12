package com.example.btl_android.activities;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
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

import com.example.btl_android.R;
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
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;
    private final static int REQUEST_CODE_PIN_LOCK = 0;

    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        this.binding = ActivityCreateOrChangePasswordActivityBinding.inflate(getLayoutInflater());
        this.setContentView(this.binding.getRoot());

        this.SetListeners();
    }

    //      call back when password is correct
    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PIN_LOCK)
        {
            if (resultCode == RESULT_OK)
            {
                //      do something you want when pass the security

                this.ChangePassword();
            }
        }
    }

    private void SetListeners()
    {
        this.binding.backButton.setOnClickListener(view ->
        {
            this.finish();
        });

        this.binding.COCPBtnUpdate.setOnClickListener(view ->
        {
            String newPass = this.binding.COCPEdtNewPass.getText().toString();
            String confirmedPass = this.binding.COCPEdtConfirmedPass.getText().toString();

            if (newPass == null || newPass.length() == 0)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("Error");
                builder.setMessage("Enter your new password!");
                builder.setPositiveButton("OK", null);
                builder.create().show();

                this.binding.COCPEdtNewPass.setText("");
                this.binding.COCPEdtConfirmedPass.setText("");

                return;
            }

            if (newPass.length() < 6)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("Error");
                builder.setMessage("Your new password must be 6 digits");
                builder.setPositiveButton("OK", null);
                builder.create().show();

                this.binding.COCPEdtNewPass.setText("");
                this.binding.COCPEdtConfirmedPass.setText("");

                return;
            }

            if (confirmedPass == null || confirmedPass.length() == 0)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("Error");
                builder.setMessage("Confirm your password!");
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

            //      Sử dụng sinh trắc học
            BiometricManager biometricManager = BiometricManager.from(this);

            //      Kiểm tra khả năng sử dụng sinh trắc học trên thiết bị
            switch (biometricManager.canAuthenticate())
            {
                //      Thiết bị không hỗ trợ sinh trắc học
                case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:

                //      Xác thực sinh trắc học không khả dụng ở thời điểm hiện tại
                case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:

                //      Không có vân tay nào được đăng ký ở thiết bị
                case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                    CreateOrChangePasswordActivity.this.EnterDevicePassword();

                    return;
            }

            Executor executor = ContextCompat.getMainExecutor(this);

            biometricPrompt = new BiometricPrompt(CreateOrChangePasswordActivity.this, executor,
                    new BiometricPrompt.AuthenticationCallback()
                    {
                        @Override
                        public void onAuthenticationError(int errorCode, @NonNull CharSequence errString)
                        {
                            super.onAuthenticationError(errorCode, errString);
                        }

                        @Override public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result)
                        {
                            super.onAuthenticationSucceeded(result);

                            CreateOrChangePasswordActivity.this.ChangePassword();
                        }

                        @Override public void onAuthenticationFailed()
                        {

                        }
                    }
            );

            promptInfo = new BiometricPrompt.PromptInfo.Builder().setTitle("EvaNote").setDescription("Use FingerPrint To Change Password")
                    .setDeviceCredentialAllowed(true).build();

            biometricPrompt.authenticate(promptInfo);
        });
    }

    private void EnterDevicePassword()
    {
        //      Nếu máy hiện tại có hỗ trợ đăng nhập mật khẩu
        KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

        //      Nếu máy có mật khẩu
        if (km.isDeviceSecure())
        {
            Intent authIntent = km.createConfirmDeviceCredentialIntent("EvaNote", "Confirm your password");

            startActivityForResult(authIntent, REQUEST_CODE_PIN_LOCK);
        }
        //      Nếu máy không có mật khẩu
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Warning!");
            builder.setMessage("Your device does not have password nor does it support" +
                    " fingerprint! This will greatly affect EvaNote security!");
            builder.setPositiveButton("OK :(", new DialogInterface.OnClickListener()
            {
                @Override public void onClick(DialogInterface dialog, int which)
                {
                    CreateOrChangePasswordActivity.this.ChangePassword();
                }
            });

            builder.create().show();
        }
    }

    private void ChangePassword()
    {
        //Lưu pass
        String newPass = CreateOrChangePasswordActivity.this.binding.COCPEdtNewPass.getText().toString();

        PreferenceManager preferenceManager =
                new PreferenceManager(CreateOrChangePasswordActivity.this);
        preferenceManager.putString(Constants.PRIVATE_NOTE_PASSWORD, newPass);

        //  Quay về
        CreateOrChangePasswordActivity.this.finish();
    }
}