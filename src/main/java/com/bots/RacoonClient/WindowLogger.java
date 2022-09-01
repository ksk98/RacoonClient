package com.bots.RacoonClient;

import com.bots.RacoonShared.Logging.Log;
import com.bots.RacoonShared.Logging.Loggers.Logger;
import com.bots.RacoonShared.Logging.Loggers.LoggerBase;

// TODO:
public class WindowLogger extends LoggerBase {
    private static WindowLogger instance = null;

    private WindowLogger() {

    }

    public void initialise() {

    }

    public static Logger getInstance() {
        if (instance == null)
            instance = new WindowLogger();

        return instance;
    }

    @Override
    public void log(Log log) {
        System.out.println(log);
    }
}
