package com.bots.RacoonClient.Loggers;

import com.bots.RacoonClient.Views.Main.LogOutput;
import com.bots.RacoonShared.Logging.Exceptions.LogException;
import com.bots.RacoonShared.Logging.Log;
import com.bots.RacoonShared.Logging.Loggers.LoggerBase;

public class WindowLogger extends LoggerBase implements RemoteLogger {
    private static WindowLogger instance = null;
    private LogOutput target = null;

    private WindowLogger() {

    }

    @Override
    public void fallbackLog(Log log, String s) {

    }

    @Override
    public void displayLog(Log log) throws LogException {
        if (target == null)
            System.out.println(log);
        else
            target.outputLocalLog(log);
    }

    public static WindowLogger getInstance() {
        if (instance == null)
            instance = new WindowLogger();

        return instance;
    }

    public LogOutput getTarget() {
        return target;
    }

    public void setTarget(LogOutput target) {
        this.target = target;
    }

    @Override
    public void logRemote(Log log) {
        if (target != null) {
            target.outputRemoteLog(log);
        }
    }
}
