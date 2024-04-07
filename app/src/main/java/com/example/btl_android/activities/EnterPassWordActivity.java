package com.example.btl_android.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_android.databinding.ActivityEnterPassWordBinding;

public class EnterPassWordActivity extends AppCompatActivity {
    private ActivityEnterPassWordBinding binding;
    private String pass = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding =  ActivityEnterPassWordBinding.inflate(getLayoutInflater());
        this.setContentView(this.binding.getRoot());
        this.SetListeners();
    }

    private void SetListeners(){

        this.binding.EPEdtPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true; // Ngăn chặn sự kiện chạm vào EditText
            }
        });

        this.binding.EPEdtPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return true; // Ngăn chặn sự kiện phím được nhấn trên EditText
            }
        });
        this.binding.EPBtn0.setOnClickListener(view ->{
            if(this.binding.EPEdtPassword.length()== 6){
                this.binding.EPEdtPassword.setText(this.binding.EPEdtPassword.getText().toString().substring(0, 6));

                return;
            }

            pass = pass + "0";
            this.binding.EPEdtPassword.setText(pass);
        });
        this.binding.EPBtn1.setOnClickListener(view ->{
            if(this.binding.EPEdtPassword.length()== 6){
                this.binding.EPEdtPassword.setText(this.binding.EPEdtPassword.getText().toString().substring(0, 6));

                return;
            }

            pass = pass + "1";
            this.binding.EPEdtPassword.setText(pass);
        });
        this.binding.EPBtn2.setOnClickListener(view ->{
            if(this.binding.EPEdtPassword.length()== 6){
                this.binding.EPEdtPassword.setText(this.binding.EPEdtPassword.getText().toString().substring(0, 6));

                return;
            }

            pass = pass + "2";
            this.binding.EPEdtPassword.setText(pass);
        });
        this.binding.EPBtn3.setOnClickListener(view ->{
            if(this.binding.EPEdtPassword.length()== 6){
                this.binding.EPEdtPassword.setText(this.binding.EPEdtPassword.getText().toString().substring(0, 6));
                return;
            }

            pass = pass + "3";
            this.binding.EPEdtPassword.setText(pass);
        });
        this.binding.EPBtn4.setOnClickListener(view ->{
            if(this.binding.EPEdtPassword.length()== 6){
                this.binding.EPEdtPassword.setText(this.binding.EPEdtPassword.getText().toString().substring(0, 6));

                return;
            }

            pass = pass + "4";
            this.binding.EPEdtPassword.setText(pass);
        });
        this.binding.EPBtn5.setOnClickListener(view ->{
            if(this.binding.EPEdtPassword.length()== 6){
                this.binding.EPEdtPassword.setText(this.binding.EPEdtPassword.getText().toString().substring(0, 6));
                return;
            }

            pass = pass + "5";
            this.binding.EPEdtPassword.setText(pass);
        });
        this.binding.EPBtn6.setOnClickListener(view ->{
            if(this.binding.EPEdtPassword.length()== 6){
                this.binding.EPEdtPassword.setText(this.binding.EPEdtPassword.getText().toString().substring(0, 6));

                return;
            }

            pass = pass + "6";
            this.binding.EPEdtPassword.setText(pass);
        });
        this.binding.EPBtn7.setOnClickListener(view ->{
            if(this.binding.EPEdtPassword.length()== 6){
                this.binding.EPEdtPassword.setText(this.binding.EPEdtPassword.getText().toString().substring(0, 6));
                return;
            }

            pass = pass + "7";
            this.binding.EPEdtPassword.setText(pass);
        });
        this.binding.EPBtn8.setOnClickListener(view ->{
            if(this.binding.EPEdtPassword.length()== 6){
                this.binding.EPEdtPassword.setText(this.binding.EPEdtPassword.getText().toString().substring(0, 6));

                return;
            }
            pass = pass + "8";
            this.binding.EPEdtPassword.setText(pass);
        });
        this.binding.EPBtn9.setOnClickListener(view ->{
            if(this.binding.EPEdtPassword.length()== 6){
                this.binding.EPEdtPassword.setText(this.binding.EPEdtPassword.getText().toString().substring(0, 6));
                return;
            }
            pass = pass + "9";
            this.binding.EPEdtPassword.setText(pass);
        });
        this.binding.EPBtnDelete.setOnClickListener(view ->{
            if(pass != null){
                pass = pass.substring(0, pass.length() - 1);
                //pass = newPass;
                this.binding.EPEdtPassword.setText(pass);
            }
            else{
                this.binding.EPEdtPassword.setText("");
            }
        });
        this.binding.EPBtnDone.setOnClickListener(view ->{
            if(this.binding.EPEdtPassword.getText().toString().equals("123456")){
                Intent intent = new Intent(this, PrivateNoteActivity.class);
                startActivity(intent);
                finish();
            }
            else{
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Error");
                builder.setMessage("Mật khẩu vừa nhập chưa đúng");

                builder.setNegativeButton("OK", null);

                builder.create().show(); //Dialog được tạo và hiện lên màn hình
            }
        });
    }
}

// ...

