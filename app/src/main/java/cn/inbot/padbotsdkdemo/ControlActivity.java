package cn.inbot.padbotsdkdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

        distanceEt = (EditText) findViewById(R.id.control_move_distance_et);
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

            case R.id.dance_monkey:
                if (null != robot) {
                    new Schedule(robot, "f-2,b-4");
                }
                break;

            case R.id.control_assignment_2: {

                String actionString = distanceEt.getText().toString();

                if (null != robot) {
                    new Schedule(robot, actionString);
                }

                break;
            }
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
