package com.bkic.lymenglong.audiobookbkic.Presenters.Review;

import android.app.Activity;
import android.content.Context;

public interface PresenterReviewImp {
    void ReviewDialog(Context context);

    void ReviewBookDialog(Context context);

    void RequestReviewBook(Activity activity, int userId, int bookId, int rateNumber, String review);
}
