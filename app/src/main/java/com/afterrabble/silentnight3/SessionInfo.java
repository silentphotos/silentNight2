package com.afterrabble.silentnight3;

import java.util.UUID;

/**
 * Created by AaronR on 11/17/17.
 */

public  class SessionInfo {
    // This was Going to be an enum
    // but a static class was fewer lines of code

    static final int SINGLE_FRAME = 0;
    static final int LOWLIGHT_COMPOSIT = 1;
    static final int SUBJECT_COMPOSIT = 2;
    public static String currentSessionID = UUID.randomUUID().toString();

    public static void resetSessionID(){
        currentSessionID = UUID.randomUUID().toString();
    }


}

