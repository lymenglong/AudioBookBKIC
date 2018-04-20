package com.bkic.lymenglong.audiobookbkic.Presenters.Review;

import android.app.Activity;

public interface PresenterReviewImp {
    void ReviewBookDialog();

    void RequestReviewBook(Activity activity, int userId, int bookId, int rateNumber, String review);
}
