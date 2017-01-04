package com.yzdevelopment.inTouch;

import android.test.AndroidTestCase;
import android.util.Log;

import com.yzdevelopment.inTouch.dao.UserInfoDAO;
import com.yzdevelopment.inTouch.model.Field;

import org.junit.Test;

import java.util.List;

public class DAOTest extends AndroidTestCase {

    private UserInfoDAO dao = null;

    @Test
    public void testInitiateTable() {
        dao = new UserInfoDAO(getContext());
        List<Field> fields = dao.getAllSelectedFields();
        for (Field f : fields) {
            Log.i("Yan", f.getField_name());
        }
    }
}
