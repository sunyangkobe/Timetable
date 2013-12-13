package edu.cmu.cs.vlis.timetable.obj;

import java.util.Map;

import android.app.Activity;
import edu.cmu.cs.vlis.timetable.util.Constants.NetworkStatus;

/**
 * Provide an unified interface to access data.
 * 
 * This class is the base class for all data sources. Currently we have two data
 * sources:
 * <ul>
 * <li> {@link JsonDataProvider} fetches data from the remote server through
 * JSON.
 * <li> {@link MockDataProvider} generates mock data for local test.
 * </ul>
 */
public abstract class DataProvider {
	/**
	 * Read an object from the data source.
	 * 
	 * @param apiUrl
	 *            remote apiUrl, if any
	 * @param params
	 *            parameters for this read
	 * @param valueClassType
	 *            class type for the object to be read
	 * @return object read from the data source.
	 */
    protected Activity currentActivity;
    
    public DataProvider(Activity currentActivity) {
        this.currentActivity = currentActivity;
    }
    
	public abstract <T> T readObject(String apiUrl, Map<String, String> params, Class<T> objectClassType);

	/**
	 * @return the last network status code for reading.
	 */
	public abstract NetworkStatus getLastNetworkStatus();

	public static final DataProvider getDefaultProvider(Activity currentActivity) {
		return new MockDataProvider(currentActivity);
	}
}
