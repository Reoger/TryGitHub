package reoger.hut.com.trygithub;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import reoger.hut.com.mylibrary.CheckEditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }


    public void test(View view){
        Toast.makeText(this,"账号是否正确"+CheckEditText.dataLegality,Toast.LENGTH_LONG).show();
    }


}
