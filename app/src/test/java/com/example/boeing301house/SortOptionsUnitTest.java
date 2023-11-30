package com.example.boeing301house;

import com.example.boeing301house.itemlist.SortOptions;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for SortOptions model class
 */
public class SortOptionsUnitTest {

    /**
     * Test default values + reset
     */
    @Test
    public void testDefault() {
        SortOptions sortOptions = SortOptions.getInstance();
        Assert.assertEquals("ASC", sortOptions.getOrder());
        Assert.assertEquals("Date Added", sortOptions.getType());

        sortOptions.resetSort();
        Assert.assertEquals("ASC", sortOptions.getOrder());
        Assert.assertEquals("Date Added", sortOptions.getType());
    }

    /**
     * Test order setting and getting
     */
    @Test
    public void testOrder() {
        SortOptions sortOptions = SortOptions.getInstance();
        Assert.assertEquals("ASC", sortOptions.getOrder());
        sortOptions.setOrder("DESC");
        Assert.assertEquals("DESC", sortOptions.getOrder());
        sortOptions.setOrder("ASC");
        Assert.assertEquals("ASC", sortOptions.getOrder());
    }

    /**
     * Test type setting and getting
     */
    @Test
    public void testType() {
        SortOptions sortOptions = SortOptions.getInstance();
        Assert.assertEquals("Date Added", sortOptions.getType());
        for (String type: SortOptions.types) {
            sortOptions.setType(type);
            Assert.assertEquals(type, sortOptions.getType());
        }
        sortOptions.resetSort();
        Assert.assertEquals("Date Added", sortOptions.getType());
    }

}
