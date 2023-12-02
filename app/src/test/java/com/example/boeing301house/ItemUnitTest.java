package com.example.boeing301house;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Unit test for {@link Item} object
 */
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

    /**
     * Test item builder class
     */
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

    /**
     * Test {@link Item#getDateString()}
     */
    @Test
    public void testItemDateString() {
        Item item = mockItem();
        assertEquals("11/03/2022", item.getDateString());

        item.setDate(new GregorianCalendar(2001, Calendar.SEPTEMBER, 14).getTimeInMillis());
        assertEquals("09/14/2001", item.getDateString());
    }

    /**
     * Test {@link Item#getValueString()}
     */
    @Test
    public void testItemValueString() {
        Item item = mockItem();
        assertEquals("2.00", item.getValueString());

        item.setValue(1231421.134214124124123);
        assertEquals("1231421.13", item.getValueString());

        item.setValue(1231421.139214124124123);
        assertEquals("1231421.14", item.getValueString());
    }

    /**
     * Test item selection methods
     */
    @Test
    public void testItemSelection() {
        Item item = mockItem();
        assertFalse(item.isSelected());

        item.select();
        assertTrue(item.isSelected());

        item.deselect();
        assertFalse(item.isSelected());
    }


}
