package com.bots.RaccoonClient.Loggers;

import com.bots.RaccoonClient.Views.Main.LogOutput;
import com.bots.RaccoonShared.Logging.Exceptions.LogException;
import com.bots.RaccoonShared.Logging.Log;
import com.bots.RaccoonShared.Logging.Loggers.Logger;

public class WindowLogger extends Logger implements RemoteLogger {
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
