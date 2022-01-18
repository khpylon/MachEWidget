package com.example.khughes.machewidget;

import android.util.Log;

public class ProgramStateMachine {
    public enum States {
        INITIAL_STATE(1),
        ATTEMPT_TO_GET_ACCESS_TOKEN(2),
        ATTEMPT_TO_GET_VEHICLE_STATUS(3),
        ATTEMPT_TO_GET_VIN_AGAIN(4),
        HAVE_TOKEN_AND_STATUS(5),
        WAIT_FOR_SERVER(6);

        private final int id;

        States(int id) {
            this.id = id;
        }

        public int getValue() {
            return id;
        }
    }

    private States currentState;

    ProgramStateMachine() {
        currentState = States.INITIAL_STATE;
    }

    ProgramStateMachine(States state) {
        currentState = state;
    }

    ProgramStateMachine(String state) {
        currentState = States.valueOf(state);
    }

    public States getCurrentState() {
        return currentState;
    }

    public States FSM(Boolean networkUp, Boolean loginGood, Boolean VINGood, Boolean tokenRefused, Boolean serverDown) {
        switch (currentState) {
            case INITIAL_STATE:
                if (networkUp) {
                    currentState = States.ATTEMPT_TO_GET_ACCESS_TOKEN;
                    Log.d(MainActivity.CHANNEL_ID, "initial -> get token");
                }
                break;
            case ATTEMPT_TO_GET_ACCESS_TOKEN:
                if (loginGood) {
                    currentState = States.ATTEMPT_TO_GET_VEHICLE_STATUS;
                    Log.d(MainActivity.CHANNEL_ID, "get token -> get status");
                }
                break;
            case ATTEMPT_TO_GET_VEHICLE_STATUS:
                if (networkUp) {
                    if (VINGood) {
                        currentState = States.HAVE_TOKEN_AND_STATUS;
                        Log.d(MainActivity.CHANNEL_ID, "get status --> all good");
                    } else {
                        currentState = States.ATTEMPT_TO_GET_VIN_AGAIN;
                        Log.d(MainActivity.CHANNEL_ID, "get status --> get vin again");
                    }
                }
                break;
            case ATTEMPT_TO_GET_VIN_AGAIN:
                if (networkUp) {
                    if (VINGood) {
                        currentState = States.HAVE_TOKEN_AND_STATUS;
                        Log.d(MainActivity.CHANNEL_ID, "get vin again --> all good");
                    }
                }
                break;
            case HAVE_TOKEN_AND_STATUS:
                if (serverDown) {
                    currentState = States.WAIT_FOR_SERVER;
                    Log.d(MainActivity.CHANNEL_ID, "all good --> wait on server");
                } else if (networkUp && tokenRefused) {
                    currentState = States.ATTEMPT_TO_GET_ACCESS_TOKEN;
                    Log.d(MainActivity.CHANNEL_ID, "all good --> get token");
                }
                break;
            case WAIT_FOR_SERVER:
                if (!serverDown) {
                    currentState = States.HAVE_TOKEN_AND_STATUS;
                    Log.d(MainActivity.CHANNEL_ID, "wait on server --> all good");
                }
                break;
        }
        return currentState;
    }

    // nestState = state.loginGood();
    public States loginGood() {
        return FSM(true, true, false, false, false);
    }

    // nestState = state.networkUpLoginBad();
    public States loginBad() {
        return FSM(true, false, false, true, false);
    }

    // nestState = state.serverDown();
    public States serverDown() {
        return FSM(true, true, false, false, true);
    }

    // nestState = state.VINBad();
    public States badVIN() {
        return FSM(true, true, false, false, false);
    }

    // nestState = state.VINGood();
    public States goodVIN() {
        return FSM(true, true, true, false, false);
    }

    // nextState = state.networkDown();
    public States networkDown() {
        return FSM(false, false, false, false, false);
    }

    // nextState state.networkUpLoginBad();
    public States networkUpLoginBad() {
        return FSM(true, false, false, false, false);
    }
}
