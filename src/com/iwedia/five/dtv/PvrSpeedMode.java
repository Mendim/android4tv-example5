/*
 * Copyright (C) 2014 iWedia S.A. Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package com.iwedia.five.dtv;

/**
 * Class that holds required PVR playback speed constants.
 */
public class PvrSpeedMode {
    public static final int PVR_SPEED_BACKWARD_X64 = -6400;
    public static final int PVR_SPEED_BACKWARD_X32 = -3200;
    public static final int PVR_SPEED_BACKWARD_X16 = -1600;
    public static final int PVR_SPEED_BACKWARD_X8 = -800;
    public static final int PVR_SPEED_BACKWARD_X4 = -400;
    public static final int PVR_SPEED_BACKWARD_X2 = -200;
    public static final int PVR_SPEED_BACKWARD_X1 = -100;
    public static final int PVR_SPEED_BACKWARD_X0_5 = -50;
    public static final int PVR_SPEED_BACKWARD_X0_25 = -25;
    public static final int PVR_SPEED_PAUSE = 0;
    public static final int PVR_SPEED_FORWARD_X0_25 = 25;
    public static final int PVR_SPEED_FORWARD_X0_5 = 50;
    public static final int PVR_SPEED_FORWARD_X1 = 100;
    public static final int PVR_SPEED_FORWARD_X2 = 200;
    public static final int PVR_SPEED_FORWARD_X4 = 400;
    public static final int PVR_SPEED_FORWARD_X8 = 800;
    public static final int PVR_SPEED_FORWARD_X16 = 1600;
    public static final int PVR_SPEED_FORWARD_X32 = 3200;
    public static final int PVR_SPEED_FORWARD_X64 = 6400;
    public static final int SPEED_ARRAY_FORWARD[] = {
            PvrSpeedMode.PVR_SPEED_FORWARD_X2,
            PvrSpeedMode.PVR_SPEED_FORWARD_X4,
            PvrSpeedMode.PVR_SPEED_FORWARD_X8,
            PvrSpeedMode.PVR_SPEED_FORWARD_X16,
            PvrSpeedMode.PVR_SPEED_FORWARD_X32,
            PvrSpeedMode.PVR_SPEED_FORWARD_X64 };
    public static final int SPEED_ARRAY_REWIND[] = {
            PvrSpeedMode.PVR_SPEED_BACKWARD_X1,
            PvrSpeedMode.PVR_SPEED_BACKWARD_X2,
            PvrSpeedMode.PVR_SPEED_BACKWARD_X4,
            PvrSpeedMode.PVR_SPEED_BACKWARD_X8,
            PvrSpeedMode.PVR_SPEED_BACKWARD_X16,
            PvrSpeedMode.PVR_SPEED_BACKWARD_X32,
            PvrSpeedMode.PVR_SPEED_BACKWARD_X64 };

    /**
     * Converts speed to text representation.
     */
    public static String converSpeedToString(int speed) {
        switch (speed) {
            case PVR_SPEED_FORWARD_X1: {
                return "1x";
            }
            case PVR_SPEED_FORWARD_X2: {
                return "2x";
            }
            case PVR_SPEED_FORWARD_X4: {
                return "4x";
            }
            case PVR_SPEED_FORWARD_X8: {
                return "8x";
            }
            case PVR_SPEED_FORWARD_X16: {
                return "16x";
            }
            case PVR_SPEED_FORWARD_X32: {
                return "32x";
            }
            case PVR_SPEED_FORWARD_X64: {
                return "64x";
            }
            case PVR_SPEED_PAUSE: {
                return "pause";
            }
            case PVR_SPEED_BACKWARD_X1: {
                return "-1x";
            }
            case PVR_SPEED_BACKWARD_X2: {
                return "-2x";
            }
            case PVR_SPEED_BACKWARD_X4: {
                return "-4x";
            }
            case PVR_SPEED_BACKWARD_X8: {
                return "-8x";
            }
            case PVR_SPEED_BACKWARD_X16: {
                return "-16x";
            }
            case PVR_SPEED_BACKWARD_X32: {
                return "-32x";
            }
            case PVR_SPEED_BACKWARD_X64: {
                return "-64x";
            }
            default:
                return "";
        }
    }
}
