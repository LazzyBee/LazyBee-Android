package com.born2go.lazzybee.algorithms;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
//import org.junit.runner.RunWith;
//import org.robolectric.RobolectricGradleTestRunner;
//import org.robolectric.RobolectricTestRunner;
//import org.robolectric.annotation.Config;

/**
 * Created by Nobody on 7/9/2015.
 */

//@RunWith(RobolectricGradleTestRunner.class)
//@Config(constants = BuildConfig.class)//, emulateSdk = 17)
public class WordEstimateTest{
    private static final double DELTA = 1e-6;
    WordEstimate testEstimate;
    @Before
    public void setUp() throws Exception {
        testEstimate = new WordEstimate();
        testEstimate.setUpParams(1, 600, 20, 20); // 100% level 1 => voca = 600
        testEstimate.setUpParams(2, 600, 30, 25); // voca +=
        testEstimate.setUpParams(3, 600, 20, 10);
        testEstimate.setUpParams(4, 600, 20, 5);
        testEstimate.setUpParams(5, 600, 15, 3);
        testEstimate.setUpParams(6, 600, 10, 1);
        testEstimate.setUpParams(7, 200, 5, 0);
    }

    @Test
    public void test_estimateVoca() throws Exception {
        System.out.println("Your estimated vocablary is " + testEstimate._estimateVoca() + " words.");
    }

    @Test
    public void test_getLastResult() throws Exception {
        Assert.fail(); // Just want to have a failure test
    }

    @Test
    public void testUpdateVoca() throws Exception {
        testEstimate.updateVoca(1, true);
        Assert.assertEquals(1730.0, testEstimate._estimateVoca(), DELTA);
    }

    @Test
    public void testUpdateVoca1() throws Exception {

    }

    @Test
    public void testGetVoca() throws Exception {

    }

    @Test
    public void testjsonArrayToIntArray()throws Exception {

    }

    @Test
    public void testintArrayToJSONArray()throws Exception {
        System.out.println(WordEstimate.intArrayToJSONArray(testEstimate.wordTotal));
    }

    @Test
    public void test_sampleNumberWordEachLevel() throws Exception {
        int set[] = testEstimate._sampleNumberWordEachLevel();
        Assert.assertEquals(100, set[0] + set[1] + set[2] + set[3] + set[4] + set[5] + set[6] + set[7],
                DELTA);
    }
    @Test
    public void test_packSaveResult(){
        System.out.println(testEstimate.packSaveResult());
    }

    @Test
    public void test_getEstimateLevel(){
        int[] arr = {321, 450, 589, 612, 750, 861, 1024, 1350, 1649, 1999, 2410, 2845, 3210, 3568, 3974, 6543, 9876 };
        for (int i : arr)
            System.out.println("Your estimated level for voca " + i + " is " +
                Arrays.toString(testEstimate._getEstimateLevel(i)) +
                "; " + Arrays.toString(testEstimate.getNumberWordEachLevel(i)));
/*        System.out.println("Your estimated level for voca 450 is " +
                Arrays.toString(testEstimate._getEstimateLevel(450)));
        System.out.println("Your estimated level for voca 589 is " +
                Arrays.toString(testEstimate._getEstimateLevel(589)));
        System.out.println("Your estimated level for voca 612 is " +
                Arrays.toString(testEstimate._getEstimateLevel(612)));
        System.out.println("Your estimated level for voca 750 is " +
                Arrays.toString(testEstimate._getEstimateLevel(750)));
        System.out.println("Your estimated level for voca 861 is " +
                Arrays.toString(testEstimate._getEstimateLevel(861)));
        System.out.println("Your estimated level for voca 1024 is " +
                Arrays.toString(testEstimate._getEstimateLevel(1024)));
        System.out.println("Your estimated level for voca 1350 is " +
                Arrays.toString(testEstimate._getEstimateLevel(1350)));
        System.out.println("Your estimated level for voca 1649 is " +
                Arrays.toString(testEstimate._getEstimateLevel(1649)));
        System.out.println("Your estimated level for voca 1999 is " +
                Arrays.toString(testEstimate._getEstimateLevel(1999)));
        System.out.println("Your estimated level for voca 2410 is " +
                Arrays.toString(testEstimate._getEstimateLevel(2410)));
        System.out.println("Your estimated level for voca 2845 is " +
                Arrays.toString(testEstimate._getEstimateLevel(2845)));
        System.out.println("Your estimated level for voca 3210 is " +
                Arrays.toString(testEstimate._getEstimateLevel(3210)));
        System.out.println("Your estimated level for voca 3568 is " +
                Arrays.toString(testEstimate._getEstimateLevel(3568)));
        System.out.println("Your estimated level for voca 3974 is " +
                Arrays.toString(testEstimate._getEstimateLevel(3974)));

*/
    }
}
