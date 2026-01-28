package org.curiosity.rover.app;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AppArgs {
    private final Map<String, String> args;

    private AppArgs(Map<String, String> args) {
        this.args = args;
    }

    public static AppArgs parse(String[] args) {
        Map<String, String> argMap = IntStream.range(0, args.length / 2)
                .boxed()
                .collect(Collectors.toMap(
                        i -> args[i * 2],
                        i -> args[i * 2 + 1]));
        return new AppArgs(argMap);
    }
}
