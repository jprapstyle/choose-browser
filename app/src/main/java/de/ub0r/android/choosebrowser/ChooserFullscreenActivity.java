package de.ub0r.android.choosebrowser;

import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.OnBackPressedCallback;

public class ChooserFullscreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        EdgeToEdge.applyInsets(this);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override public void handleOnBackPressed() { finishAndRemoveTask(); }
        });

        final Uri uri = getIntent().getData();
        if (!new IntentParser().validUri(uri)) {
            finishAndRemoveTask();
            return;
        }
        if (savedInstanceState != null) return;
        final ChooserFragment f = ChooserFragment.newInstance(uri, false);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, f)
                .commit();
    }

}
