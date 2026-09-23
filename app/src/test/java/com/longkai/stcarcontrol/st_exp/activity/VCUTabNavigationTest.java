package com.longkai.stcarcontrol.st_exp.activity;

import org.junit.Test;

import static com.longkai.stcarcontrol.st_exp.ConstantData.*;
import static org.junit.Assert.assertEquals;

public class VCUTabNavigationTest {
    @Test
    public void chassisIsAfterObcWithoutRenumberingLegacyPages() {
        int[] expected = {0, 1, 2, 3, FRAGMENT_TRANSACTION_CHASSIS, 4, 5, 6};
        for (int position = 0; position < expected.length; position++) {
            assertEquals(expected[position], VCUTabNavigation.pageIdAt(position));
            assertEquals(position, VCUTabNavigation.tabPositionForPage(expected[position]));
        }
        assertEquals(7, FRAGMENT_TRANSACTION_GYHLSD);
        assertEquals(11, FRAGMENT_TRANSACTION_OBC);
    }

    @Test
    public void animationUsesVisualPositionRatherThanPageId() {
        assertEquals(1, VCUTabNavigation.animationDirection(FRAGMENT_TRANSACTION_CHASSIS, FRAGMENT_TRANSACTION_BMS));
        assertEquals(-1, VCUTabNavigation.animationDirection(FRAGMENT_TRANSACTION_BMS, FRAGMENT_TRANSACTION_CHASSIS));
        assertEquals(0, VCUTabNavigation.animationDirection(FRAGMENT_TRANSACTION_CHASSIS, FRAGMENT_TRANSACTION_CHASSIS));
    }

    @Test
    public void drawerPagesKeepTheirOwningTab() {
        assertEquals(2, VCUTabNavigation.tabPositionForPage(FRAGMENT_TRANSACTION_GYHLSD));
        assertEquals(2, VCUTabNavigation.tabPositionForPage(200));
        assertEquals(2, VCUTabNavigation.tabPositionForPage(FRAGMENT_TRANSACTION_TORQUE));
        assertEquals(5, VCUTabNavigation.tabPositionForPage(FRAGMENT_TRANSACTION_MONITOR));
        assertEquals(7, VCUTabNavigation.tabPositionForPage(FRAGMENT_TRANSACTION_UPDATE_FIRMWARE));
        assertEquals(-1, VCUTabNavigation.tabPositionForPage(-100));
    }
}
