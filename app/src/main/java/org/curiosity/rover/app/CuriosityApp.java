package org.curiosity.rover.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CuriosityApp {
    private static final Logger logger = LoggerFactory.getLogger(CuriosityApp.class);

    public static void main(String[] args) {

        AppArgs appArgs = AppArgs.parse(args);


    }
}
