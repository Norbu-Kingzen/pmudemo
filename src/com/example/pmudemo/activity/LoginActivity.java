package com.example.pmudemo.activity;

import com.example.pmudemo.R;

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
	private Button signinBtn;
	private EditText unameTxt;
	private CheckBox savepwdChk;
	private boolean isSavePwdChecked = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		signinBtn = (Button)findViewById(R.id.signinBtn);
		signinBtn.setOnClickListener(new SigninButtonOnClickLsnr());
		savepwdChk = (CheckBox)findViewById(R.id.savepwdChk);
		savepwdChk.setOnCheckedChangeListener(new SavepwdCheckboxOnCheckedChangeLsnr());
		
	}
	
	class SigninButtonOnClickLsnr implements Button.OnClickListener{
		@Override
		public void onClick( View v ) {
//			savepwdChk = (CheckBox)findViewById(R.id.savepwdChk);
//			savepwdChk.setOnCheckedChangeListener(new SavepwdCheckboxOnCheckedChangeLsnr());
			// TODO Add save password logic here
			if(isSavePwdChecked){ 
				Toast.makeText( LoginActivity.this, "Save password checked", Toast.LENGTH_SHORT ).show();
            }else{ 
            	Toast.makeText( LoginActivity.this, "Save password uncheck", Toast.LENGTH_SHORT ).show();
            }
			
			unameTxt = (EditText)findViewById(R.id.unameTxt);
			String uName = unameTxt.getText().toString().trim();
			// Transport values to next activity
			Intent intent=new Intent();
			intent.setClass(LoginActivity.this, MapActivity.class);
			intent.putExtra("username", uName);
			startActivity(intent);
		}
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
}
