package ai.sensy.jadia;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, new ListFragment())
                .commit();
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.container);
        if (f != null && f instanceof OnWindowFocusChangedListener) {
            ((OnWindowFocusChangedListener) f).onWindowFocusChanged(hasFocus);
        }
    }

    interface OnWindowFocusChangedListener {

        void onWindowFocusChanged(boolean hasFocus);
    }
}
