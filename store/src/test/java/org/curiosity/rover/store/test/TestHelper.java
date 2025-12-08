package org.curiosity.rover.store.test;

import java.io.File;

public class TestHelper {

    public static String getStoreBasePath() {
        String targetPath = new File(
                TestHelper.class.getProtectionDomain()
                        .getCodeSource()
                        .getLocation()
                        .getPath())
                .getParent();
        return targetPath;
    }

    public static String getObjectPath(String objectName) {
        return new File(getStoreBasePath(), objectName).getAbsolutePath();
    }

}
