package com.bots.RacoonClient;

import com.bots.RacoonShared.Logging.Log;
import com.bots.RacoonShared.Logging.Loggers.LoggerBase;

// TODO:
public class WindowLogger extends LoggerBase {
    private static WindowLogger instance = null;

    protected WindowLogger() {
        super(1);
    }

    public void initialise() {

    }

    public static WindowLogger getInstance() {
        if (instance == null)
            instance = new WindowLogger();

        return instance;
    }

    @Override
    public void log(Log log) {

    }
}
