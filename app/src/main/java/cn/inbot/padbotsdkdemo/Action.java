package cn.inbot.padbotsdkdemo;
import android.util.Pair;

import cn.inbot.padbotsdk.Robot;

public class Action {
    private String action;
    private Robot robot;
    private Pair<Integer, Runnable> result;

    public Action(
            // Syntax: f-1
            String action,
            Robot robot
    ) {
        this.robot  = robot;
        this.action = action;

        this.result = this.parse();
    }

    public Pair<Integer, Runnable> get() {
        return this.result;
    }

    private Pair<Integer, Runnable> parse() {

        // Parse action string to action pair here
        final String[] parts = this.action.split("-");

        return new Pair<Integer, Runnable>(
                Integer.parseInt(parts[1]),
                interpretMove(parts[0])
        );
    }

    private Runnable interpretMove(String move) {
        Runnable response;
        final Robot robot = this.robot;

        switch (move.toLowerCase()) {
            case "f":
                response = new Runnable() {
                    public void run() { robot.goForward(); }
                };
                break;
            case "b":
                response = new Runnable() {
                    public void run() { robot.goBackward(); }
                };
                break;
            case "r":
                response = new Runnable() {
                    public void run() { robot.turnRight(); }
                };
                break;
            case "l":
                response = new Runnable() {
                    public void run() { robot.turnLeft(); }
                };
                break;
            default:
                response = new Runnable() {
                    public void run() { robot.stop(); }
                };
                break;
        }

        return response;
    }
}
