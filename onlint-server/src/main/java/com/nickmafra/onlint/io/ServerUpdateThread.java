package com.nickmafra.onlint.io;

import com.nickmafra.concurrent.LimitedRateThread;
import com.nickmafra.onlint.RetanguloState;

public class ServerUpdateThread extends LimitedRateThread {

    public static final int PERIOD = 20;

    public ServerUpdateThread(RetanguloState state) {
        super("UpdateThread", PERIOD, state::updateByServer);
    }
}
