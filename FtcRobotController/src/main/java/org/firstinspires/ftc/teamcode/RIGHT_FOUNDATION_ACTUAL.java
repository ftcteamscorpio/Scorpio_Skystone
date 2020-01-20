package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

@Autonomous
public class RIGHT_FOUNDATION_ACTUAL extends LinearOpMode {
    Motor left1;
    Motor right1;
    Motor left2;
    Motor right2;
    MotorBlock block;
    double frontspeed = 0.4;
    double turnspeed = (0.25+0.6)/2;
    boolean isright = true;
    BNO055IMU imu;
        BNO055IMU.Parameters parameters;
        Orientation             lastAngles = new Orientation();
        double                  globalAngle;
        HardwareMap hardwareMap2;
        Telemetry telemetry2;
        public void imuinit () {
            if (isright) imu = hardwareMap.get(BNO055IMU.class,"imu");
            else imu = hardwareMap2.get(BNO055IMU.class,"imu");
            parameters = new BNO055IMU.Parameters();
            parameters.mode                = BNO055IMU.SensorMode.IMU;
            parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
            parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
            parameters.loggingEnabled      = false;
            imu.initialize(parameters);

            if (isright) telemetry.addData("Gyro Mode", "calibrating...");
            else telemetry2.addData("Gyro Mode", "calibrating...");
            if (isright) telemetry.update();
            else telemetry2.update();

            while (!isStopRequested() && !imu.isGyroCalibrated()) {
                sleep(50);
                idle();
            }

            if (isright) telemetry.addData("Gyro Mode", "ready");
            else telemetry2.addData("Gyro Mode", "ready");
            if (isright) telemetry.addData("imu calib status", imu.getCalibrationStatus().toString());
            else telemetry2.addData("imu calib status", imu.getCalibrationStatus().toString());
            if (isright) telemetry.update();
            else telemetry2.update();
        }
        private void resetAngle()
        {
            lastAngles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

            globalAngle = 0;
        }
        private double getAngle()
        {
            // We experimentally determined the Z axis is the axis we want to use for heading angle.
            // We have to process the angle because the imu works in euler angles so the Z axis is
            // returned as 0 to +180 or 0 to -180 rolling back to -179 or +179 when rotation passes
            // 180 degrees. We detect this transition and track the total cumulative angle of rotation.

            Orientation angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

            double deltaAngle = angles.firstAngle - lastAngles.firstAngle;

            if (deltaAngle < -180)
                deltaAngle += 360;
            else if (deltaAngle > 180)
                deltaAngle -= 360;

            globalAngle += deltaAngle;

            lastAngles = angles;

            return globalAngle;
        }
        public void runleft (HardwareMap a, Telemetry b) throws  InterruptedException {
            isright = false;
            hardwareMap2 = a;
            telemetry2 = b;
            runOpMode();
        }
        public void runOpMode() throws InterruptedException {
            //Variable Definitions
            Servo mover1;
            Servo mover2;
            if (isright) {
                left1 = new Motor(hardwareMap.get(DcMotor.class, "left1"));
                right1 = new Motor(hardwareMap.get(DcMotor.class, "right1"));
                left2 = new Motor(hardwareMap.get(DcMotor.class, "left2"));
                right2 = new Motor(hardwareMap.get(DcMotor.class, "right2"));
                block = new MotorBlock(left1,right1,left2,right2);
                mover1 = hardwareMap.get(Servo.class,"mover1");
                mover2 = hardwareMap.get(Servo.class,"mover2");
            }
        else {
            left1 = new Motor(hardwareMap2.get(DcMotor.class, "left1"));
            right1 = new Motor(hardwareMap2.get(DcMotor.class, "right1"));
            left2 = new Motor(hardwareMap2.get(DcMotor.class, "left2"));
            right2 = new Motor(hardwareMap2.get(DcMotor.class, "right2"));
            block = new MotorBlock(left1,right1,left2,right2);
            mover1 = hardwareMap2.get(Servo.class,"mover1");
            mover2 = hardwareMap2.get(Servo.class,"mover2");
        }

        imuinit();

        waitForStart();

        block.backwardrotations(frontspeed,37.5);

        block.stop();

        mover1.setPosition(1);
        mover2.setPosition(1);

        sleep(300);

        //block.forwardrotations(frontspeed,18);

        resetAngle();

        block.left1.motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        block.right1.motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        block.right2.motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        block.left2.motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        if (isright) {
            block.leftcurve(turnspeed);
            while (opModeIsActive() && getAngle() == 0) {
                telemetry.addData("angle",getAngle());
                telemetry.update();
            }

            while (opModeIsActive() && getAngle() < 45) {
                telemetry.addData("angle",getAngle());
                telemetry.update();
            }
        }

        resetAngle();

        if (isright) {
            block.leftcurve2(turnspeed);
            while (opModeIsActive() && getAngle() == 0) {
                telemetry.addData("angle",getAngle());
                telemetry.update();
            }

            while (opModeIsActive() && getAngle() < 45) {
                telemetry.addData("angle",getAngle());
                telemetry.update();
            }
        }

        if (isright) telemetry.addData("final angle is:",getAngle());
        if (isright) telemetry.update();

        block.stop();

        mover1.setPosition(0);
        mover2.setPosition(0);

        Motor turnclaw = new Motor(hardwareMap.get(DcMotor.class, "turnclaw"));
        turnclaw.setPower(0.5);
        sleep(1000);
        turnclaw.setPower(0);

        block.backwardrotations(0.5,18);
        block.stop();

        block.rightsidewaysrotations(0.5, 5);

        block.forwardrotations(1,36);
        block.stop();

        sleep(500);

        block.leftsidewaysrotations(1,5,imu);

        block.stop();

    }
}

