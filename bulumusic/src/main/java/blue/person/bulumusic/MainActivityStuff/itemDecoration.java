package blue.person.bulumusic.MainActivityStuff;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by getbl on 2017/4/7.
 */

public class itemDecoration extends RecyclerView.ItemDecoration {

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) < parent.getAdapter().getItemCount() - 1) {
            outRect.left = 15;
            outRect.right = 15;
            outRect.bottom = 30;
        }
    }
}
