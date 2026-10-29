package de.ub0r.android.choosebrowser;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Android 15+ enforces edge-to-edge for apps targeting SDK 35+, which draws the action bar and
 * content behind the status and navigation bars. Pad the window content by the system bar insets.
 */
final class EdgeToEdge {

    private EdgeToEdge() {
    }

    static void applyInsets(@NonNull final Activity activity) {
        final ViewGroup decor = (ViewGroup) activity.getWindow().getDecorView();
        final View root = decor.getChildAt(0);
        if (root == null) {
            return;
        }
        // keep the regular background for the content, color the system bar areas
        root.setBackground(decor.getBackground());
        decor.setBackground(new ColorDrawable(ContextCompat.getColor(activity, R.color.colorPrimaryDark)));
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, windowInsets) -> {
            final Insets insets = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            v.setPadding(insets.left, insets.top, insets.right, insets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
        ViewCompat.requestApplyInsets(root);
    }
}
