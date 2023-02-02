package com.sqcred.spackets.utils;

import lombok.Getter;
import lombok.Setter;

public class Logger {

    @Getter
    @Setter
    private static boolean debug = false;

    public static void log(String message, Level level, Side side){
        System.out.println("[SPackets][" + side.getName() + "][" + level.getName() + "]: " + message);
    }

    public static void debug(String message, Side side){
        if(debug){
            log(message, Level.DEBUG, side);
        }
    }

    public static void info(String message, Side side){
        log(message, Level.INFO, side);
    }

    public static void error(String message, Side side){
        log(message, Level.ERROR, side);
    }

    public static void warn(String message, Side side){
        log(message, Level.WARN, side);
    }

    public enum Side {
        CLIENT("Client"), SERVER("Server"), UNKNOWN("Unknown");

        @Getter
        private final String name;

        Side(String name){
            this.name = name;
        }

    }

    public enum Level {
        INFO("Info"), ERROR("Error"), WARN("Warn"), DEBUG("Debug");

        @Getter
        private final String name;

        Level(String name){
            this.name = name;
        }

    }

}
