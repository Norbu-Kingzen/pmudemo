package com.example.pmudemo.activity;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.example.pmudemo.MyApplication;
import com.example.pmudemo.R;
import com.example.pmudemo.helper.HttpRequestHelper;
import com.example.pmudemo.util.Const;
import com.example.pmudemo.util.Util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

public class LoginActivity extends Activity{
	
	private HttpRequestHelper httpHelper;
	private MyApplication myApp;
	
	private Button signinBtn;
	private EditText unameTxt;
	private EditText pwdTxt;
	private CheckBox savepwdChk;
	private boolean isSavePwdChecked = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		init();
	}
	
	private void init() {
		signinBtn = (Button)findViewById(R.id.signinBtn);
		signinBtn.setOnClickListener(new SigninButtonOnClickLsnr());
		unameTxt = (EditText)findViewById(R.id.unameTxt);
		pwdTxt = (EditText)findViewById(R.id.pwdTxt);
		
		savepwdChk = (CheckBox)findViewById(R.id.savepwdChk);
		savepwdChk.setOnCheckedChangeListener(new SavepwdCheckboxOnCheckedChangeLsnr());
	}
	
	class SigninButtonOnClickLsnr implements Button.OnClickListener{
		@Override
		public void onClick( View v ) {
			// TODO Add save password logic here
			if(isSavePwdChecked){ 
				Toast.makeText( LoginActivity.this, "Save password checked", Toast.LENGTH_SHORT ).show();
            }else{ 
            	Toast.makeText( LoginActivity.this, "Save password uncheck", Toast.LENGTH_SHORT ).show();
            }
			if (isParamNull()) {
				return ;
			}
			
			String uName = unameTxt.getText().toString().trim();
			String pwd = pwdTxt.getText().toString().trim();
			// Login process
			login(uName, pwd);
			
		}
	}
	
	private boolean isParamNull() {
		if (Const.BLANK.equals(unameTxt.getText().toString().trim())
				|| unameTxt.getText().toString().trim() == null) {
			Util.showShortToast(LoginActivity.this, getResString(R.string.error_username_null));
			return true;
		}
		else if (Const.BLANK.equals(pwdTxt.getText().toString().trim())
				|| pwdTxt.getText().toString().trim() == null) {
			Util.showShortToast(LoginActivity.this, getResString(R.string.error_password_null));
			return true;
		}
		else {
			return false;
		}
	}
	
	private void login(final String userName, final String pwd) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				httpHelper = new HttpRequestHelper();
				ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("username", userName));
				params.add(new BasicNameValuePair("password", pwd));
				// TODO url没有，临时注释
//				String loginRst = httpHelper.sendGetRequestAndReturnString(Const.URL_LOGIN);
				// TODO 登录成功与否的判断和处理
				if(true) {
					myApp = getMyApp();
					myApp.put("username", userName);
					
					// Transport values to next activity
					Intent intent=new Intent();
					intent.setClass(LoginActivity.this, MapActivity.class);
					startActivity(intent);
				}
				else {
					Util.showShortToast(LoginActivity.this, getResString(R.string.err_login_failed));
				}
			}
			
		}).start();
	}
	
	class SavepwdCheckboxOnCheckedChangeLsnr implements OnCheckedChangeListener{
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			isSavePwdChecked = isChecked;
//			if(isSavePwdChecked){ 
//				Toast.makeText( LoginActivity.this, "Save password checked", Toast.LENGTH_SHORT ).show();
//            }else{ 
//            	Toast.makeText( LoginActivity.this, "Save password uncheck", Toast.LENGTH_SHORT ).show();
//            }
			
		}
	}
	
	private MyApplication getMyApp() {
		if(myApp == null) {
			myApp = (MyApplication)this.getApplication();
		}
		return myApp;
	}
	private String getResString(int id)
	{
		return getResources().getString(id);
	}
}
