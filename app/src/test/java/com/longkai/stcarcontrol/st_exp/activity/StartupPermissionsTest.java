package com.longkai.stcarcontrol.st_exp.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;

import com.longkai.stcarcontrol.st_exp.communication.ConnectionListener;
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.ShadowToast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 33, application = Application.class,
        shadows = StartupPermissionsTest.ShadowServiceManager.class)
public class StartupPermissionsTest {
    private final List<List<String>> requests = new ArrayList<>();

    @Before
    public void capturePermissionRequests() {
        ActivityCompat.setPermissionCompatDelegate(new ActivityCompat.PermissionCompatDelegate() {
            @Override
            public boolean requestPermissions(Activity activity, String[] permissions, int requestCode) {
                requests.add(Arrays.asList(permissions));
                return true;
            }

            @Override
            public boolean onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
                return false;
            }
        });
    }

    @After
    public void clearPermissionDelegate() {
        ActivityCompat.setPermissionCompatDelegate(null);
    }

    @Test
    public void mainActivityAcceptsCancelledPermissionResult() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).get();

        activity.onRequestPermissionsResult(1, new String[0], new int[0]);

        assertTrue(ShadowLog.getLogsForTag("BaseActivity").stream()
                .anyMatch(log -> log.msg.contains("cancelled")));
    }

    @Test
    public void mainActivityAcceptsIncompletePermissionResult() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).get();

        activity.onRequestPermissionsResult(1,
                new String[]{Manifest.permission.RECORD_AUDIO}, new int[0]);
        activity.onRequestPermissionsResult(1, new String[0],
                new int[]{PackageManager.PERMISSION_GRANTED});

        assertEquals(2, ShadowLog.getLogsForTag("BaseActivity").stream()
                .filter(log -> log.msg.contains("incomplete")).count());
    }

    @Test
    @Config(sdk = {28, 29})
    public void mainActivityRequestsPermissionsOnce() {
        Robolectric.buildActivity(MainActivity.class).create();

        assertEquals(1, requests.size());
        assertEquals(3, requests.get(0).size());
        assertTrue(requests.get(0).containsAll(Arrays.asList(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO)));
    }

    @Test
    @Config(sdk = 28)
    public void chooseActivityRequestsPermissionsOnce() {
        Robolectric.buildActivity(ChooseActivity.class).create();

        assertEquals(1, requests.size());
        assertEquals(Arrays.asList(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE), requests.get(0));
    }

    @Test
    public void mainActivityOnlyRequestsMicrophoneOnAndroid13() {
        Robolectric.buildActivity(MainActivity.class).create();

        assertEquals(Arrays.asList(Arrays.asList(Manifest.permission.RECORD_AUDIO)), requests);
    }

    @Test
    @Config(sdk = {30, 32})
    public void mainActivityDoesNotRequestObsoleteWritePermission() {
        Robolectric.buildActivity(MainActivity.class).create();

        assertEquals(1, requests.size());
        assertEquals(Arrays.asList(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO), requests.get(0));
    }

    @Test
    public void chooseActivityDoesNotRequestObsoleteStoragePermissions() {
        Robolectric.buildActivity(ChooseActivity.class).create();

        assertTrue(requests.isEmpty());
    }

    @Test
    @Config(sdk = 28)
    public void mainActivitySkipsAlreadyGrantedPermissions() {
        shadowOf(RuntimeEnvironment.getApplication()).grantPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        Robolectric.buildActivity(MainActivity.class).create();

        assertEquals(Arrays.asList(Arrays.asList(Manifest.permission.RECORD_AUDIO)), requests);
    }

    @Test
    public void mainActivityDoesNotRequestPermissionsWhenAlreadyGranted() {
        shadowOf(RuntimeEnvironment.getApplication()).grantPermissions(Manifest.permission.RECORD_AUDIO);

        Robolectric.buildActivity(MainActivity.class).create();

        assertTrue(requests.isEmpty());
    }

    @Test
    public void mainActivityDoesNotRepeatRequestOnRecreation() {
        Robolectric.buildActivity(MainActivity.class).create(new Bundle());

        assertTrue(requests.isEmpty());
    }

    @Test
    public void mainActivityReportsDeniedPermission() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).get();

        activity.onRequestPermissionsResult(1,
                new String[]{Manifest.permission.RECORD_AUDIO},
                new int[]{PackageManager.PERMISSION_DENIED});

        assertEquals("You denied the permission", ShadowToast.getTextOfLatestToast());
        assertTrue(ShadowLog.getLogsForTag("BaseActivity").stream()
                .anyMatch(log -> log.msg.contains(Manifest.permission.RECORD_AUDIO)));
    }

    @Test
    public void mainActivityAcceptsGrantedPermission() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).get();

        activity.onRequestPermissionsResult(1,
                new String[]{Manifest.permission.RECORD_AUDIO},
                new int[]{PackageManager.PERMISSION_GRANTED});

        assertNull(ShadowToast.getTextOfLatestToast());
    }

    @Test
    public void mainActivityChecksEachPermissionResult() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).get();

        activity.onRequestPermissionsResult(1,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO},
                new int[]{PackageManager.PERMISSION_GRANTED, PackageManager.PERMISSION_DENIED});

        assertEquals("You denied the permission", ShadowToast.getTextOfLatestToast());
        assertEquals(1, ShadowLog.getLogsForTag("BaseActivity").size());
        assertTrue(ShadowLog.getLogsForTag("BaseActivity").get(0).msg
                .contains(Manifest.permission.RECORD_AUDIO));
    }

    @Test
    public void grantOnlySetupDoesNotRunForCancelledOrDeniedResults() {
        RecordingActivity activity = Robolectric.buildActivity(RecordingActivity.class).get();

        activity.onRequestPermissionsResult(1, new String[0], new int[0]);
        activity.onRequestPermissionsResult(1,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                new int[]{PackageManager.PERMISSION_DENIED});
        assertEquals(0, activity.grantedCalls);

        activity.onRequestPermissionsResult(1,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                new int[]{PackageManager.PERMISSION_GRANTED});
        assertEquals(1, activity.grantedCalls);
    }

    @Test
    public void unrelatedPermissionResultDoesNotTriggerStartupSetup() {
        RecordingActivity activity = Robolectric.buildActivity(RecordingActivity.class).get();

        activity.onRequestPermissionsResult(99,
                new String[]{Manifest.permission.RECORD_AUDIO},
                new int[]{PackageManager.PERMISSION_GRANTED});

        assertEquals(0, activity.grantedCalls);
    }

    public static class RecordingActivity extends BaseActivity {
        int grantedCalls;

        @Override
        protected void onStartupPermissionsGranted() {
            grantedCalls++;
        }
    }

    // Permission tests must not establish a connection to car hardware.
    @Implements(value = ServiceManager.class, isInAndroidSdk = false)
    public static class ShadowServiceManager {
        @Implementation
        protected void setConnectionListener(ConnectionListener listener) {
        }
    }
}
