package cn.inbot.padbotsdkdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import cn.inbot.padbotsdk.Robot;
import cn.inbot.padbotsdk.RobotManager;
import cn.inbot.padbotsdk.constant.RobotDisconnectType;
import cn.inbot.padbotsdk.listener.RobotConnectionListener;
import cn.inbot.padbotsdk.listener.RobotListener;
import cn.inbot.padbotsdk.model.ObstacleDistanceData;

public class ControlActivity extends AppCompatActivity implements RobotConnectionListener,RobotListener {

    private Robot robot;
    private String serialNumber;
    private int model;

    private TextView nameValueTv;
    private TextView connectStatusValueTv;
    private TextView obstacleValueTv;
    private TextView batteryValueTv;
    private TextView hardwareVersionTv;
    private TextView soundSourceTv;

    private EditText distanceEt;
    private EditText angleEt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        setTitle("Control Robot");

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        RobotManager.getInstance(getApplication()).setRobotConnectionListener(this);
        RobotManager.getInstance(getApplication()).openSoundSourceAngleListener();

        Intent intent = getIntent();
        model = intent.getIntExtra("model", 0);
        serialNumber = intent.getStringExtra("serialNumber");

        connectStatusValueTv = (TextView) findViewById(R.id.control_connect_status_value_tv);
        nameValueTv = (TextView) findViewById(R.id.control_name_value_tv);
        nameValueTv.setText(serialNumber);


        obstacleValueTv = (TextView) findViewById(R.id.control_obstacle_tv);
        batteryValueTv = (TextView) findViewById(R.id.control_battery_tv);
        hardwareVersionTv = (TextView) findViewById(R.id.control_hardware_version_tv);
        soundSourceTv = (TextView) findViewById(R.id.control_sound_source_tv);

        distanceEt = (EditText) findViewById(R.id.control_move_distance_et);
        angleEt = (EditText) findViewById(R.id.control_turn_angle_et);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Control the robot
     * @param view
     */

    public void onClick(View view) throws InterruptedException {

        switch (view.getId()) {

            case R.id.control_connect_bt:

                connectStatusValueTv.setText("Connecting...");

                if (1 == model) {
                    RobotManager.getInstance(getApplication()).connectRobotByBluetooth(serialNumber);
                }
                else if (2 == model) {
                    RobotManager.getInstance(getApplication()).connectRobotBySerialPort();
                }

                break;

            case R.id.control_disconnect_bt:

                connectStatusValueTv.setText("Disconnecting...");

                RobotManager.getInstance(getApplication()).disconnectRobot();

                break;

            case R.id.control_stop_bt:

                if (null != robot) {
                    robot.stop();
                }
                break;

// Assignment 1
            case R.id.control_forward_bt:

                ScheduledExecutorService mover = Executors.newScheduledThreadPool(1);

                ArrayList<Pair<Integer, Runnable>> schedule = new ArrayList<Pair<Integer, Runnable>>() {
                    {
                        add(
                                new Pair<Integer, Runnable> (
                                    1,
                                    new Runnable() {
                                        public void run() { robot.goForward(); }
                                    }
                                )
                        );

                        add(
                                new Pair<Integer, Runnable> (
                                        4,
                                        new Runnable() {
                                            public void run() { robot.goBackward(); }
                                        }
                                )
                        );

                        add(
                                new Pair<Integer, Runnable> (
                                        7,
                                        new Runnable() {
                                            public void run() { robot.turnLeft(); }
                                        }
                                )
                        );
                    }
                };

                final Runnable stop = new Runnable() {
                    public void run() { robot.stop(); }
                };

                if (null != robot) {
                    for (Pair<Integer, Runnable> move : schedule) {
                        Integer delay   = move.first;
                        Runnable action = move.second;
                        mover.schedule(stop, delay - 1, TimeUnit.SECONDS);
                        mover.schedule(action, delay, TimeUnit.SECONDS);
                    }
                }

                break;

            case R.id.control_back_bt:

                if (null != robot) {
                    robot.goBackward();
                }

                break;

            case R.id.control_left_bt:

                if (null != robot) {
                    robot.turnLeft();
                }

                break;

            case R.id.control_right_bt:

                if (null != robot) {
                    robot.turnRight();
                }

                break;

            case R.id.control_left_front_bt:

                if (null != robot) {
                    robot.goForwardLeft(4);
                }

                break;

            case R.id.control_right_front_bt:

                if (null != robot) {
                    robot.goForwardRight(4);
                }

                break;

            case R.id.control_left_back_bt:

                if (null != robot) {
                    robot.goBackwardLeft(4);
                }

                break;

            case R.id.control_right_back_bt:

                if (null != robot) {
                    robot.goBackwardRight(4);
                }

                break;

            case R.id.control_head_rise_bt:

                if (null != robot) {
                    robot.headRise();
                }

                break;

            case R.id.control_head_down_bt:

                if (null != robot) {
                    robot.headDown();
                }

                break;

            case R.id.control_go_charging_bt:

                if (null != robot) {
                    robot.goCharging();
                }

                break;

            case R.id.control_stop_charging_bt:

                if (null != robot) {
                    robot.stopCharging();
                }

                break;


            case R.id.control_obstacle_on_bt:

                if (null != robot) {
                    robot.turnOnObstacleDetection();
                }

                break;

            case R.id.control_obstacle_off_bt:

                if (null != robot) {
                    robot.turnOffObstacleDetection();
                }

                break;

            case R.id.control_1st_speed_bt:

                if (null != robot) {
                    robot.setMovementSpeed(1);
                }

                break;

            case R.id.control_3rd_speed_bt:

                if (null != robot) {
                    robot.setMovementSpeed(3);
                }

                break;

            case R.id.control_obstacle_bt:

                if (null != robot) {
                    robot.queryObstacleDistanceData();
                }

                break;

            case R.id.control_battery_bt:

                if (null != robot) {
                    robot.queryBatteryPercentage();
                }

                break;

            case R.id.control_hardware_version_bt:

                if (null != robot) {
                    robot.queryRobotHardwareVersion();
                }

                break;

                /* Look at this below for inspiration for assignment 2:
                Ass.2= 2. Input route with the textfield.
                Goal here: text input to a list of actions.

                    Read string and parse,
                    1. get the input= Input field with submit button
                    2. Fetch text to a variable -> string
                    Ex. "F100, b100, lf45"
                    3. String to actions: split string, seperate by comma and remove spaces
                    4. Put meaning to the letters (f=forward)
                    (4.5- class action(function, delay, args?) -> robot.goForward)
                    5. ["f100", "b100",..] Find the chars and numbers -
                        5.5- pair them together
                    ------- above is the goal for monday ------------
                    6. List of pairs?
                    7. for action in actions -> queue(action).
                 */

            case R.id.control_go_forward_with_arg_bt:{
                String distanceStr = distanceEt.getText().toString();
                int distance = 0;
                try {
                    distance = Integer.parseInt(distanceStr);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Please enter a positive integer", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (null != robot) {
                    robot.goForward(distance);
                }

                break;
            }
            case R.id.control_go_backward_with_arg_bt:{

                String distanceStr = distanceEt.getText().toString();
                int distance = 0;
                try {
                    distance = Integer.parseInt(distanceStr);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Please enter a positive integer", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (null != robot) {
                    robot.goBackward(distance);
                }

                break;
            }
            case R.id.control_turn_left_with_arg_bt:{
                String angleStr = angleEt.getText().toString();
                int angle = 0;
                try {
                    angle = Integer.parseInt(angleStr);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Please enter a positive integer", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (null != robot) {
                    robot.turnLeft(angle);
                }

                break;
            }
            case R.id.control_turn_right_with_arg_bt:{
                String angleStr = angleEt.getText().toString();
                int angle = 0;
                try {
                    angle = Integer.parseInt(angleStr);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Please enter a positive integer", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (null != robot) {
                    robot.turnRight(angle);
                }

                break;
            }
            case R.id.control_sound_source_bt:

                if (null != robot) {
                    robot.querySoundSourceAngle();
                }

                break;
            default:
                break;

        }

    }

    /**
     * invoke when connect the robot successfully
     * @param robot The connected robot instance.
     */
    @Override
    public void onRobotConnected(final Robot robot) {

        this.robot = robot;
        this.robot.setListener(this);

        nameValueTv.setText(robot.getRobotSerialNumber());
        connectStatusValueTv.setText("Connected");
    }

    /**
     * invoken when connect the robot unsuccessfully
     * @param robotSerialNumber The serial number of the robot.
     */
    @Override
    public void onRobotConnectFailed(final String robotSerialNumber) {

        this.robot = null;

        connectStatusValueTv.setText("Connect failed");
    }

    /**
     * Invoke when the robot disconnected
     * @param robotSerialNumber The serial number of the robot.
     * @param disconnectedType  disconnect type.
     */
    @Override
    public void onRobotDisconnected(String robotSerialNumber, RobotDisconnectType disconnectedType) {

        this.robot = null;

        connectStatusValueTv.setText("Disconnected");
    }


    @Override
    public void onReceivedRobotObstacleDistanceData(final ObstacleDistanceData obstacleDistanceData) {

        obstacleValueTv.setText("result:" + obstacleDistanceData.getFirstDistance() + ","
                + obstacleDistanceData.getSecondDistance() + ","
                + obstacleDistanceData.getThirdDistance() + ","
                + obstacleDistanceData.getFourthDistance() + ","
                + obstacleDistanceData.getFifthDistance());
    }

    @Override
    public void onReceivedRobotBatteryPercentage(final int batteryPercentage) {
        batteryValueTv.setText("result:" + batteryPercentage);
    }

    @Override
    public void onReceivedRobotHardwareVersion(final int version) {
        hardwareVersionTv.setText("result:" + version);
    }

    @Override
    public void onReceivedRobotSerialNumber(String serialNumber) {
        nameValueTv.setText(serialNumber);
    }

    @Override
    public void onReceivedCustomData(String data) {

    }

    @Override
    public void onReceivedSoundSourceAngle(int angle) {

        soundSourceTv.setText("" + angle);

    }
}
