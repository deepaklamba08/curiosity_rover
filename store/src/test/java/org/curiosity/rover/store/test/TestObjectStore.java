package org.curiosity.rover.store.test;

import org.curiosity.rover.store.ObjectStore;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.util.Collections;

public class TestObjectStore {

    private ObjectStore store;

    @BeforeSuite
    public void init() {
        this.store = new ObjectStore("E:\\work\\curiosity_rover\\store\\target");
    }

    @Test
    public void runRegisterObject() {
        this.store.registerObject("test_1", Collections.emptyMap());
    }

}
