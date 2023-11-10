package com.example.boeing301house;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class ItemUnitTest {
    private final long date = new GregorianCalendar(2022, Calendar.NOVEMBER, 3).getTimeInMillis();
    private Item mockItem() {
        Item item = new Item();
        item.setValue(2);
        item.setDate(date);
        item.setComment("test comment");
        item.setDescription("test desc");
        item.setItemID("11112223");
        item.setMake("test make");
        item.setModel("test model");
        return item;
    }


    @Test
    public void testItemBuilder() {
        Item builtItem = new ItemBuilder()
                .addValue(2)
                .addDate(date)
                .addComment("test comment")
                .addDescription("test desc")
                .addID("11112223")
                .addMake("test make")
                .addModel("test model")
                .build();

        Item item = mockItem();
        assertEquals(builtItem.getValue(), item.getValue(), .1);
        assertEquals(builtItem.getDate(), item.getDate());
        assertEquals(builtItem.getComment(), item.getComment());
        assertEquals(builtItem.getDescription(), item.getDescription());
        assertEquals(builtItem.getItemID(), item.getItemID());
        assertEquals(builtItem.getMake(), item.getMake());
        assertEquals(builtItem.getModel(), item.getModel());

    }

    @Test
    public void testItemDateString() {
        Item item = mockItem();
        assertEquals("11/03/2022", item.getDateString());

        item.setDate(new GregorianCalendar(2001, Calendar.SEPTEMBER, 14).getTimeInMillis());
        assertEquals("09/14/2001", item.getDateString());
    }


}
