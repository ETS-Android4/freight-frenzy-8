package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.constructors.Button;
import org.firstinspires.ftc.teamcode.constructors.OneShot;

import org.firstinspires.ftc.teamcode.constructors.TeleOpTemplate;

import static com.qualcomm.robotcore.util.Range.clip;

@TeleOp(name = "RobotController", group = "Controls")
//@Disabled
public class Controller extends OpMode {

    TeleOpTemplate robot         = new TeleOpTemplate();
    static final double SPEED         = -.5;

    double              grabberPower  = 0;

    private ElapsedTime runtime = new ElapsedTime();
    private ElapsedTime looptime = new ElapsedTime();

    OneShot wobbleButton = new OneShot();
    boolean             armRaised = true;

    Button wobbleToggled = new Button();

    Button              ringClamped = new Button();
    Button              collectingRings = new Button();
    Button              shooterActivated = new Button();

    boolean             rotatorLocked = false;
    
    double              intakeArmPower = 0;

    int                 hopperDepth   = 0;


    // The array driveMode stores all of the possible modes for driving our robot. At the start of
    // the program, the mode is set to 0, or "tank."
    enum Mode {

        SIDESTRAFE("Strafe with Side buttons"),
        TANK("Pure Tank Drive"),
        OMNI("Hybrid Tank/Mecanum Drive"),
        MECANUM("Pure Mecanum Drive");

        private String description;

        // getNext taken from (with modifications)
        // https://digitaljoel.nerd-herders.com/2011/04/05/get-the-next-value-in-a-java-enum/.
        public Mode getNext() {
            return this.ordinal() < Mode.values().length - 1
                    ? Mode.values()[this.ordinal() + 1]
                    : Mode.values()[0];
        }

        // Code taken from (with modifications)
        // https://stackoverflow.com/questions/15989316/
        // how-to-add-a-description-for-each-entry-of-enum/15989359#15989359
        private Mode(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    Mode mode = Mode.SIDESTRAFE;

    public void init() {

        robot.teleOpInit(hardwareMap);
    }

    @Override
    public void loop() {// Cycle through the driving modes when the "b" button on the first controller is pressed.
        looptime.reset();

        // Run code depending on which drive mode is currently active (at 75% speed, because
        // our robot is too fast at full speed):
        switch(mode) {
            case TANK: {

                // In "tank" drive mode,
                // the left joystick controls the speed of the left set of motors,
                // and the right joystick -controls the right set.
                robot.drivetrain.leftFrontDrive.setPower(-gamepad1.right_stick_y * SPEED);
                robot.drivetrain.leftBackDrive.setPower(-gamepad1.right_stick_y * SPEED);
                robot.drivetrain.rightFrontDrive.setPower(-gamepad1.left_stick_y * SPEED);
                robot.drivetrain.rightBackDrive.setPower(-gamepad1.left_stick_y * SPEED);
                break;

            }
            case OMNI: {

                // Really funky. Strafes left or right using the left joystick
                // if the left joystick's "x" value is greater than "y;" runs like tank drive
                // otherwise.
                // This code was developed as a simple test by request of a coach, but the driver
                // responsible for moving the chassis actually liked the way that it worked!
                if (Math.abs(gamepad1.left_stick_x) > Math.abs(gamepad1.left_stick_y)) {
                    robot.drivetrain.leftFrontDrive.setPower(gamepad1.left_stick_x * SPEED);
                    robot.drivetrain.rightFrontDrive.setPower(-gamepad1.left_stick_x * SPEED);
                    robot.drivetrain.leftBackDrive.setPower(-gamepad1.left_stick_x * SPEED);
                    robot.drivetrain.rightBackDrive.setPower(gamepad1.left_stick_x * SPEED);
                }
                else {
                    robot.drivetrain.leftFrontDrive.setPower(-gamepad1.left_stick_y * SPEED);
                    robot.drivetrain.leftBackDrive.setPower(-gamepad1.left_stick_y * SPEED);
                    robot.drivetrain.rightFrontDrive.setPower(-gamepad1.right_stick_y * SPEED);
                    robot.drivetrain.rightBackDrive.setPower(-gamepad1.right_stick_y * SPEED);
                }
                break;

            }
            case MECANUM: {

                // Code taken from http://ftckey.com/programming/advanced-programming/. Also
                // funky; turns with the right joystick and moves/strafes with the left one.
                robot.drivetrain.leftFrontDrive.setPower(clip((-gamepad1.left_stick_y + gamepad1.left_stick_x
                        - gamepad1.right_stick_x), -1., 1) * SPEED);
                robot.drivetrain.leftBackDrive.setPower(clip((-gamepad1.left_stick_y - gamepad1.left_stick_x
                        + gamepad1.right_stick_x), -1., 1) * SPEED);
                robot.drivetrain.rightFrontDrive.setPower(clip((-gamepad1.left_stick_y - gamepad1.left_stick_x
                        - gamepad1.right_stick_x), -1., 1) * SPEED);
                robot.drivetrain.rightBackDrive.setPower(clip((-gamepad1.left_stick_y + gamepad1.left_stick_x
                        + gamepad1.right_stick_x), -1., 1) * SPEED);
                break;
            }
            case SIDESTRAFE: {
                // Strafes with the side triggers; tank drives otherwise.

                if (gamepad1.left_trigger > 0) {
                    robot.drivetrain.leftFrontDrive.setPower(-gamepad1.left_trigger * SPEED);
                    robot.drivetrain.rightFrontDrive.setPower(gamepad1.left_trigger * SPEED);
                    robot.drivetrain.leftBackDrive.setPower(gamepad1.left_trigger * SPEED);
                    robot.drivetrain.rightBackDrive.setPower(-gamepad1.left_trigger * SPEED);
                }
                else if (gamepad1.right_trigger > 0){
                    robot.drivetrain.leftFrontDrive.setPower(gamepad1.right_trigger * SPEED);
                    robot.drivetrain.rightFrontDrive.setPower(-gamepad1.right_trigger * SPEED);
                    robot.drivetrain.leftBackDrive.setPower(-gamepad1.right_trigger * SPEED);
                    robot.drivetrain.rightBackDrive.setPower(gamepad1.right_trigger * SPEED);
                }
                else {
                    robot.drivetrain.leftFrontDrive.setPower(-gamepad1.right_stick_y * SPEED);
                    robot.drivetrain.leftBackDrive.setPower(-gamepad1.right_stick_y * SPEED);
                    robot.drivetrain.rightFrontDrive.setPower(-gamepad1.left_stick_y * SPEED);
                    robot.drivetrain.rightBackDrive.setPower(-gamepad1.left_stick_y * SPEED);
                }

                break;
            }
            default: {

                mode = Mode.TANK;

                robot.drivetrain.leftFrontDrive.setPower(-gamepad1.right_stick_y * SPEED);
                robot.drivetrain.leftBackDrive.setPower(-gamepad1.right_stick_y * SPEED);
                robot.drivetrain.rightFrontDrive.setPower(-gamepad1.left_stick_y * SPEED);
                robot.drivetrain.rightBackDrive.setPower(-gamepad1.left_stick_y * SPEED);
            }
        }

        // Set the position of the wobbleGrabber based on whetherr it is "supposed" to be clamped
        // or unclamped.

        if (wobbleButton.checkState(gamepad2.b)) {

            armRaised = !armRaised;
            if (armRaised) {
                robot.wobbleGrabber.clampAndRaise();

            }
            else {
                robot.wobbleGrabber.lowerAndUnclamp();
            }
        }

        if(gamepad2.y) {
            // robot.ringShooter.towerShot();
            robot.ringShooter.ringShoot(.7, .5);
        }
        else if (gamepad2.x) {
            robot.ringShooter.powerShot();
        }


        // Set the ringClamp to a corresponding state based on if ringClamped is true..
        if (ringClamped.checkState(gamepad2.right_trigger >.2)) {
            robot.ringIntake.clampRing();
        }
        else {
            robot.ringIntake.unclampRing();
        }

        /*
        // Set the clampRotator to a corresponding state based on if collectingRings is true.
        if (collectingRings.checkState(gamepad2.right_trigger >.2)) {
            robot.ringIntake.extendClampRotator();
        }
        else {
            robot.ringIntake.retractClampRotator();
        }

         */

        robot.ringIntake.proportionalClampRotator();

        robot.ringIntake.controlIntakeArm(-gamepad2.left_stick_y*.4);

        // Display the current mode of the robot in Telemetry for reasons deemed obvious.
        for (int i=0; i<(Mode.values().length); i++){
            telemetry.addData("Mode ", Mode.values()[i].ordinal());
            telemetry.addData("Name", Mode.values()[i]);
        }
        telemetry.addData("Intake Position:", robot.ringIntake.returnIntakeArmPosition());

        // Display other information, including the position, speed, and mode of motors.
        telemetry.addData("Robot Mode:", mode.getDescription());

        telemetry.addData("HopperDepth ", hopperDepth);

        telemetry.update();
    }
}
