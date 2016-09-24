package edu.bonch.leovs09.timetable.AsynkTasks;

import android.app.Activity;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;

import edu.bonch.leovs09.timetable.MainActivity;
import edu.bonch.leovs09.timetable.ODT.Week;
import edu.bonch.leovs09.timetable.ODT.WeekBuilder;
import edu.bonch.leovs09.timetable.REST.RestRequest;

/**
 * Created by LeoVS09 on 14.09.2016.
 */
public class HttpRequestSetCurrentWeek extends HttpRequest<Object, String, WeekParser> {


    private int idForReplace;



    public HttpRequestSetCurrentWeek id(int idForReplace) {
        this.idForReplace = idForReplace;
        return this;
    }


    @Override
    protected WeekParser doInBackground(Object... pars) {
        try {
            ArrayList<String> params = new ArrayList<>();
            for(Object par:pars) params.add(par.toString());
            Log.i("HttpRequest", "Start");
            RestRequest rest = new RestRequest();
            String response = restRequest.in("currentTimeTable", params.get(0), params.get(1))
                    .GetObjAndStatus(String.class).toString();
            Log.i("HttpRequest", "Response received");

            return new WeekParser(activity.getResources(), response, params.get(1));

        } catch (Exception e) {
            Log.e("HttpRequest::StartError", e.getMessage(), e);
            cancel(true);
        }

        return null;
    }


    @Override
    protected void onPostExecute(WeekParser response) {
//            super.onPostExecute(response);
        Log.i("HttpRequest", "onPost start");
        try {
            MainActivity mainActivity = (MainActivity) activity;
            Week[] mWeeks = mainActivity.getWeeks();
            Log.i("onPostId", Integer.toString(idForReplace));
            mWeeks[response.getNumOfWeek()] = response.getWeek();
            mainActivity.getFragments()[idForReplace].refresh();
            Log.i("HttpRequest", "finished");
        } catch (Exception e) {
            Log.e("HttpRequest::OnPost", e.getMessage(), e);
        }
    }


}

class WeekParser {
    private Week week;
    private int numOfWeek;

    public WeekParser(Resources res, String week, String numOfWeek) throws Exception {
        this.numOfWeek = Integer.parseInt(numOfWeek);
        this.week = new WeekBuilder(res).buildWeek(this.numOfWeek,week);
        this.week.save();
    }

    public Week getWeek() {
        return week;
    }

    public int getNumOfWeek() {
        return numOfWeek;
    }
}