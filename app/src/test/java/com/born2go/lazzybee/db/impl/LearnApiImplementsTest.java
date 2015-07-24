package com.born2go.lazzybee.db.impl;

import java.lang.System;
import java.util.Date;

import static org.junit.Assert.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

/**
 * Created by Hue on 7/22/2015.
 */
public class LearnApiImplementsTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void test_updateCardQueueAndCardDue() throws Exception {
        long time = new Date().getTime();
        System.out.println("Time long value is :" + time);
        System.out.println("Time int value (before) is :" + ((int)time) /1000);
        System.out.println("Time int value (after) is :" + (int)(time/1000));
        System.out.println("Time int value (after) + 600 is :" + (int)(time/1000+600));
    }
}