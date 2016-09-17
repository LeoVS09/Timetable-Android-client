package edu.bonch.leovs09.timetable.AsynkTasks;

import android.app.Activity;
import android.os.AsyncTask;

import edu.bonch.leovs09.timetable.MainActivity;
import edu.bonch.leovs09.timetable.REST.RestRequest;

/**
 * Created by LeoVS09 on 14.09.2016.
 */
public abstract class HttpRequest<Params, Progress, Result> extends AsyncTask<Params, Progress, Result>{
    protected RestRequest restRequest = new RestRequest();
    protected Activity activity;

    public HttpRequest activity(Activity activity) {
        this.activity = activity;
        return this;
    }
}
