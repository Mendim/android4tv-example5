package com.iwedia.five.dtv;

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
}
