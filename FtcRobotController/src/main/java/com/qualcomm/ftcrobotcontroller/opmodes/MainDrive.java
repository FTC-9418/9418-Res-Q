/* Copyright (c) 2014, 2015 Qualcomm Technologies Inc

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Qualcomm Technologies Inc nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import java.util.Map;

/**
 * TeleOp Mode
 * <p>
 *Enables control of the robot via the gamepad
 */
public class MainDrive extends OpMode {

  private ElapsedTime runtime = new ElapsedTime();

  // Drive motors
  DcMotor motorRight;
  DcMotor motorLeft;

  // Winch motors
  DcMotor winchRight;
  DcMotor winchLeft;

  // Arm motor
  DcMotor armMotor;

  // Leg Motor
  DcMotor legMotor;

  @Override
  public void init() {

    // Drive Motor init
    motorRight = hardwareMap.dcMotor.get("m1");
    motorLeft = hardwareMap.dcMotor.get("m2");
    motorRight.setDirection(DcMotor.Direction.REVERSE);

    // Winch init
    winchRight = hardwareMap.dcMotor.get("wR");
    winchLeft = hardwareMap.dcMotor.get("wL");
    winchLeft.setDirection(DcMotor.Direction.REVERSE);

    // Arm motor init
    armMotor = hardwareMap.dcMotor.get("armMotor");

    // Leg motor init
    legMotor = hardwareMap.dcMotor.get("legMotor");
    }

  /*
     * Code to run when the op mode is first enabled goes here
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#start()
     */

  @Override
  public void init_loop() {

    for(Map.Entry<String, DcMotor> entry : hardwareMap.dcMotor.entrySet()) {
      telemetry.addData(entry.getKey(), String.format("Port = %d", entry.getValue().getPortNumber()));
    }
    runtime.reset();
    telemetry.addData("Motors", runtime.toString());
  }

  /*
   * This method will be called repeatedly in a loop
   * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#loop()
   */

  @Override
  public void loop() {
    // function calls
    drive();
    arm();
    winch();
    //winch_manual();
    leg();
  }

  private void drive() {
    // Gamepad 1 Drive
    // note that if y equal -1 then joystick is pushed all of the way forward.
    float left = -gamepad1.left_stick_y;
    float right = -gamepad1.right_stick_y;

    // clip the right/left values so that the values never exceed +/- 1
    right = Range.clip(right, -1, 1);
    left = Range.clip(left, -1, 1);

    // scale the joystick value to make it easier to control
    // the robot more precisely at slower speeds.
    right = (float)scaleInput(right);
    left =  (float)scaleInput(left);

    // write the values to the motors
    motorRight.setPower(right);
    motorLeft.setPower(left);
    telemetry.addData("left tgt pwr", "left  pwr: " + String.format("%.2f", left));
    telemetry.addData("right tgt pwr", "right pwr: " + String.format("%.2f", right));
  }

  private void arm()
  {
    if(gamepad1.a) {
      armMotor.setPower(1);
    } else if(gamepad1.b) {
      armMotor.setPower(-1);
    } else {
      armMotor.setPower(0);
    }
  }


  private void winch() {
    if(gamepad1.right_bumper) {
      winchRight.setPower(1);
      winchLeft.setPower(1);
      telemetry.addData("Winch:", "Winch is extending");
      return;
    } else if(gamepad1.left_bumper) {
      winchRight.setPower(-1);
      winchLeft.setPower(-1);
      telemetry.addData("Winch:", "Winch is retracting");
      return;
    } else if(gamepad1.right_trigger > 0) {
      winchRight.setPower(0.4);
      winchLeft.setPower(0.4);
      telemetry.addData("Winch:", "Winch extending slowly");
      return;
    } else if(gamepad1.left_trigger > 0) {
      winchRight.setPower(-0.4);
      winchLeft.setPower(-0.4);
      telemetry.addData("Winch:", "Winch retracting slowly");
      return;
    }

    winchRight.setPower(0);
    winchLeft.setPower(0);
    telemetry.addData("Winch:", "Winch is idle");

  }
  private void winch_manual() {
    float left = -gamepad2.left_stick_y;

    // clip the right/left values so that the values never exceed +/- 1
    left = Range.clip(left, -1, 1);

    left =  (float)scaleInput(left);
    winchLeft.setPower(left);
    winchRight.setPower(left);
  }

  private void leg() {
    if(gamepad1.y) {
      legMotor.setPower(-0.1);
    } else if(gamepad1.x) {
      legMotor.setPower(0.1);
    } else {
      legMotor.setPower(0);
    }
  }

  double scaleInput(double dVal)  {
    double[] scaleArray = { 0.0, 0.05, 0.09, 0.10, 0.12, 0.15, 0.18, 0.24,
            0.30, 0.36, 0.43, 0.50, 0.60, 0.72, 0.85, 1.00, 1.00 };

    // get the corresponding index for the scaleInput array.
    int index = (int) (dVal * 16.0);

    // index should be positive.
    if (index < 0) {
      index = -index;
    }

    // index cannot exceed size of array minus 1.
    if (index > 16) {
      index = 16;
    }

    // get value from the array.
    double dScale;
    if (dVal < 0) {
      dScale = -scaleArray[index];
    } else {
      dScale = scaleArray[index];
    }

    // return scaled value.
    return dScale;
  }
}
