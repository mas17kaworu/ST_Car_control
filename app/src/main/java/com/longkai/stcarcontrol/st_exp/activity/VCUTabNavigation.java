package com.longkai.stcarcontrol.st_exp.activity;

import static com.longkai.stcarcontrol.st_exp.ConstantData.FRAGMENT_TRANSACTION_BMS;
import static com.longkai.stcarcontrol.st_exp.ConstantData.FRAGMENT_TRANSACTION_CHARGE;
import static com.longkai.stcarcontrol.st_exp.ConstantData.FRAGMENT_TRANSACTION_CHASSIS;
import static com.longkai.stcarcontrol.st_exp.ConstantData.FRAGMENT_TRANSACTION_GYHLSD;
import static com.longkai.stcarcontrol.st_exp.ConstantData.FRAGMENT_TRANSACTION_HOME;
import static com.longkai.stcarcontrol.st_exp.ConstantData.FRAGMENT_TRANSACTION_MCU;
import static com.longkai.stcarcontrol.st_exp.ConstantData.FRAGMENT_TRANSACTION_MONITOR;
import static com.longkai.stcarcontrol.st_exp.ConstantData.FRAGMENT_TRANSACTION_OBC;
import static com.longkai.stcarcontrol.st_exp.ConstantData.FRAGMENT_TRANSACTION_OBC_DEMO;
import static com.longkai.stcarcontrol.st_exp.ConstantData.FRAGMENT_TRANSACTION_TBOX;
import static com.longkai.stcarcontrol.st_exp.ConstantData.FRAGMENT_TRANSACTION_TMP;
import static com.longkai.stcarcontrol.st_exp.ConstantData.FRAGMENT_TRANSACTION_TORQUE;
import static com.longkai.stcarcontrol.st_exp.ConstantData.FRAGMENT_TRANSACTION_UPDATE_FIRMWARE;
import static com.longkai.stcarcontrol.st_exp.ConstantData.FRAGMENT_TRANSACTION_VCUVCU;

/** Tab positions are presentation order, not the stable IDs used by setSelect callers. */
public final class VCUTabNavigation {
    private static final int[] PAGE_IDS = {
            FRAGMENT_TRANSACTION_HOME,
            FRAGMENT_TRANSACTION_TMP,
            FRAGMENT_TRANSACTION_CHASSIS,
            FRAGMENT_TRANSACTION_VCUVCU,
            FRAGMENT_TRANSACTION_OBC_DEMO,
            FRAGMENT_TRANSACTION_BMS,
            FRAGMENT_TRANSACTION_MCU,
            FRAGMENT_TRANSACTION_TBOX
    };

    private VCUTabNavigation() {
    }

    public static int pageIdAt(int tabPosition) {
        return PAGE_IDS[tabPosition];
    }

    public static int tabPositionForPage(int pageId) {
        switch (pageId) {
            case 200: // Legacy GYHLSD entry point.
            case FRAGMENT_TRANSACTION_GYHLSD:
            case FRAGMENT_TRANSACTION_CHARGE:
            case FRAGMENT_TRANSACTION_TORQUE:
            case FRAGMENT_TRANSACTION_OBC:
                pageId = FRAGMENT_TRANSACTION_VCUVCU;
                break;
            case FRAGMENT_TRANSACTION_MONITOR:
                pageId = FRAGMENT_TRANSACTION_BMS;
                break;
            case FRAGMENT_TRANSACTION_UPDATE_FIRMWARE:
                pageId = FRAGMENT_TRANSACTION_TBOX;
                break;
            default:
                break;
        }
        for (int position = 0; position < PAGE_IDS.length; position++) {
            if (PAGE_IDS[position] == pageId) {
                return position;
            }
        }
        return -1;
    }

    public static int animationDirection(int fromPageId, int toPageId) {
        int fromPosition = tabPositionForPage(fromPageId);
        int toPosition = tabPositionForPage(toPageId);
        if (fromPosition < 0 || toPosition < 0) {
            return 0;
        }
        if (fromPosition == toPosition) {
            return Integer.compare(toPageId, fromPageId);
        }
        return Integer.compare(toPosition, fromPosition);
    }
}
