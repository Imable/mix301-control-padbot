package cn.inbot.padbotsdkdemo;
import android.util.Pair;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cn.inbot.padbotsdk.Robot;

import cn.inbot.padbotsdkdemo.Action;

public class Schedule {
    private Robot robot;
    private ArrayList<Action> actions = new ArrayList<Action>();
    private ScheduledExecutorService schedule;

    public Schedule(
        Robot robot,
        String actions
    ) {
        Action move;

        for (String action : this.separateActions(actions)) {
            move = new Action(
                    action,
                    robot
            );

            this.actions.add(move);
        }

        this.robot = robot;
        this.schedule = Executors.newScheduledThreadPool(1);
        this.execute();
    }

    private String[] separateActions(String moves) {
        return moves.split(",");
    }

    public void execute() {
        Integer time = 0;

        for (Action action: this.actions) {
            Pair<Integer, Runnable> move = action.get();
            schedule.schedule(move.second, time, TimeUnit.SECONDS);
            // Calculate the start time of the next action and store it.
            time += move.first;
        }

        // Stop the robot after the sequence
        schedule.schedule(new Runnable() {
            @Override
            public void run() {
                robot.stop();
            }
        }, time, TimeUnit.SECONDS);
    }
}
