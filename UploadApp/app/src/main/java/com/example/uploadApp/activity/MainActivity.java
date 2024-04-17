package com.example.uploadApp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.uploadApp.util.Encrypter;
import com.example.uploadApp.R;
import com.example.uploadApp.util.Server;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private static int REQUEST_CODE = 1;
    private String account;
    private EditText money;
    private String securityKey;
    private String result;

    private final Server server = new Server();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Block Chain");

        money = findViewById(R.id.edit_text);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_generate_qr_code:
                        startGenerateQRCodeActivity();
                        return true;
                    case R.id.menu_scan_qr_code:
                        startScanQRCodeActivity();
                        return true;
                    default:
                        return false;
                }
            }
        });

        Button uploadButton = findViewById(R.id.upload_button);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (account == null) {
                    Toast.makeText(getApplicationContext(), "请登录区块链账户！", Toast.LENGTH_LONG).show();
                    return;
                }
                String moneyInfo = money.getText().toString().trim();
                if (result != null) {
                    try {
                        JSONObject data = new JSONObject(result);
                        data.put("price", moneyInfo);
                        data.put("upload account", account);
                        data.put("timestamp", Long.toString(System.currentTimeMillis()));
                        Encrypter encrypter = new Encrypter(data, securityKey);
                        JSONObject encrypt = encrypter.exec();
                        server.upload(account, encrypt);
                        Toast.makeText(getApplicationContext(), "上传成功", Toast.LENGTH_LONG).show();
                        result = null;
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "请扫描二维码收集数据", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button LogButton = findViewById(R.id.log_button);
        LogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = setAlertDialog();
                alertDialog.show();
            }
        });
    }

    private AlertDialog setAlertDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.login_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("登录区块链账户");
        dialogBuilder.setPositiveButton("登录", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText editTextUsername = dialogView.findViewById(R.id.edit_text_username);
                EditText editTextPassword = dialogView.findViewById(R.id.edit_text_key);
                String accountTemp = editTextUsername.getText().toString();
                String securityKeyTemp = editTextPassword.getText().toString();
                String encryptedAccount = Encrypter.encryptAccount(accountTemp, securityKeyTemp);
                Boolean success = server.log(accountTemp, encryptedAccount);
                if (success) {
                    account = accountTemp;
                    securityKey = securityKeyTemp;
                    Toast.makeText(MainActivity.this, "登录成功！", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(MainActivity.this, "登录失败！", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "取消登录！", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        return dialogBuilder.create();
    }

    private void startGenerateQRCodeActivity() {
        Intent intent = new Intent(this, GenerateQRCodeActivity.class);
        startActivity(intent);
    }

    private void startScanQRCodeActivity() {
        Intent intent = new Intent(this, ScanQRCodeActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            result = data.getStringExtra("data return");
            Toast.makeText(getApplicationContext(), "获取数据成功", Toast.LENGTH_SHORT).show();
        }
    }
}
