package com.qualcomm.ftcrobotcontroller.opmodes;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import java.util.ArrayList;
import java.util.List;

public class autonomous extends LinearOpMode {

    // Motors
    DcMotor motorRight;
    DcMotor motorLeft;

    @Override
    public void runOpMode() throws InterruptedException {

        /*
        Time (in seconds) | Tiles Traversed
        1                 | 0.9
        2                 | 1.8
        3                 | 2.7
        4                 | 3.6
        5                 | ~4.5

        Time (in seconds) | Angle (in degrees)

        Right
        0.5               | 20
        1                 | 95
        1.5               |
        2                 |
        2.5               |
        3                 |

        Left
        0.5               |
        1                 |
        1.5               |
        2                 |
        2.5               |
        3                 |
         */

        initHardware();
        waitForStart();

        List<float[]> moves = new ArrayList<>();

        // Right Turn
        moves.add(new float[]{1, -1, 500});
        moves.add(new float[]{0, 0, 4000});
        moves.add(new float[]{1, -1, 1500});
        moves.add(new float[]{0, 0, 4000});
        moves.add(new float[]{1, -1, 2000});
        moves.add(new float[]{0, 0, 4000});
        moves.add(new float[]{1, -1, 2500});
        moves.add(new float[]{0, 0, 4000});
        moves.add(new float[]{1, -1, 3000});
        moves.add(new float[]{0, 0, 4000});

        // Left Turn
        moves.add(new float[]{-1, 1, 500});
        moves.add(new float[]{0, 0, 4000});
        moves.add(new float[]{-1, 1, 1500});
        moves.add(new float[]{0, 0, 4000});
        moves.add(new float[]{-1, 1, 2000});
        moves.add(new float[]{0, 0, 4000});
        moves.add(new float[]{-1, 1, 2500});
        moves.add(new float[]{0, 0, 4000});
        moves.add(new float[]{-1, 1, 3000});
        moves.add(new float[]{0, 0, 4000});

        for(float[] move : moves) {
            drive(move[0], move[1]);
            sleep((int) move[2]);
        }
        drive(0, 0);

    }

    void initHardware()  {
        // Drive motors
        motorRight = hardwareMap.dcMotor.get("m1");
        motorLeft = hardwareMap.dcMotor.get("m2");
        motorRight.setDirection(DcMotor.Direction.REVERSE);
    }

    void drive(float left, float right) {
        motorLeft.setPower(left);
        motorRight.setPower(right);
        telemetry.addData("left tgt pwr", "left  pwr: " + String.format("%.2f", left));
        telemetry.addData("right tgt pwr", "right pwr: " + String.format("%.2f", right));
    }

}
