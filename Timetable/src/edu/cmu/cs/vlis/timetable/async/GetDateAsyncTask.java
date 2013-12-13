package edu.cmu.cs.vlis.timetable.async;

import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;

import edu.cmu.cs.vlis.timetable.fragment.PlannerFragment;
import edu.cmu.cs.vlis.timetable.obj.JsonDataProvider;
import edu.cmu.cs.vlis.timetable.util.APIAddr;
import edu.cmu.cs.vlis.timetable.util.Constants.NetworkStatus;

public class GetDateAsyncTask extends LoadingDialogAsyncTask<Void, Void, NetworkStatus> {

    private PlannerFragment fragment;
    private JsonNode responseNode;

    public GetDateAsyncTask(PlannerFragment fragment) {
        super(fragment.getActivity());
        this.fragment = fragment;
    }

    @Override
    protected String getProgressDialogTitle() {
        return "Loading semester dates...";
    }

    @Override
    protected NetworkStatus doInBackground(Void... params) {
        JsonDataProvider dataProvider = new JsonDataProvider(getActivity());
        responseNode = dataProvider.readJsonNode(APIAddr.GET_SEMESTER_DATE, null);
        return dataProvider.getLastNetworkStatus();
    }

    @Override
    protected void postExecute(NetworkStatus status) {
        if (status == NetworkStatus.BAD_REQUEST) {
            Toast.makeText(getActivity(), "Start date must be earlier than the end date",
                    Toast.LENGTH_LONG).show();
        }
        else if (status == NetworkStatus.VALID) {
            fragment.updateDatePicker(responseNode.get("start_date").asText(),
                    responseNode.get("end_date").asText());
        }
        else {
            Toast.makeText(getActivity(), "Network or server error...", Toast.LENGTH_LONG).show();
        }
    }
}
