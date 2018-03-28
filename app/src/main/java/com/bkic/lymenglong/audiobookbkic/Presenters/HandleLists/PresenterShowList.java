package com.bkic.lymenglong.audiobookbkic.Presenters.HandleLists;

import com.bkic.lymenglong.audiobookbkic.Views.HandleLists.ListBook.ListBook;
import com.bkic.lymenglong.audiobookbkic.Views.HandleLists.ListCategory.ListCategory;
import com.bkic.lymenglong.audiobookbkic.Views.HandleLists.ListMenu.ListMenu;

public class PresenterShowList implements PresenterShowListImp{

    ListMenu listMenuActivity;
    ListCategory listCategoryActivity;
    ListBook listBookActivity;

    public PresenterShowList(ListMenu listMenuActivity) {
        this.listMenuActivity = listMenuActivity;
    }

    public PresenterShowList(ListCategory listCategoryActivity) {
        this.listCategoryActivity = listCategoryActivity;
    }

    public PresenterShowList(ListBook listBookActivity) {
        this.listBookActivity = listBookActivity;
    }

}
