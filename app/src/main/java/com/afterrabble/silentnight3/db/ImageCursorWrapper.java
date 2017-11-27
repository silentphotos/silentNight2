package com.afterrabble.silentnight3.db;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.afterrabble.silentnight3.Image;

import java.util.Date;
import java.util.UUID;

import static com.afterrabble.silentnight3.db.ImageDbSchema.*;

public class ImageCursorWrapper extends CursorWrapper {

    public ImageCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Image getIamge() {
        String title = getString(getColumnIndex(ImageTable.Cols.TITLE));
        String date = getString(getColumnIndex(ImageTable.Cols.DATE));
        long groupId = getLong(getColumnIndex(ImageTable.Cols.GROUP_ID));

        Image image = new Image(title, date, groupId);

        return image;
    }
}
