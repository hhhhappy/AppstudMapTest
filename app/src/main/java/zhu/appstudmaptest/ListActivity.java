package zhu.appstudmaptest;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2017/5/30.
 */
public class ListActivity extends Activity {
    private List<Bar> barList = new ArrayList<Bar>();
    private List<String> barNameList = new ArrayList<String>();
    private ListView listView;
    private BarAdapter adapter;

    private LocationManager locationManager;
    private String provider;
    private double latitude;
    private double longitude;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        //load the views
        listView = (ListView) findViewById(R.id.list_place);
        Button button = (Button) findViewById(R.id.go_to_map);

        //test if location provider is available and get location
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providerList = locationManager.getProviders(true);
        checkCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION");
        if(providerList.contains(LocationManager.GPS_PROVIDER)){
            provider = LocationManager.GPS_PROVIDER;
        }
        else if(providerList.contains(LocationManager.NETWORK_PROVIDER)){
            provider = LocationManager.NETWORK_PROVIDER;
        }
        else{
            Toast.makeText(this, "No location provider", Toast.LENGTH_SHORT).show();
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null){
            getBars(location.getLatitude(), location.getLongitude());
        }
        locationManager.requestLocationUpdates(provider, 5000, 1, locationListener);
        //put list into lisView
        adapter = new BarAdapter(ListActivity.this, R.layout.list_item, barList);
        listView.setAdapter(adapter);

    }
    //add listener for the location
    LocationListener locationListener = new LocationListener(){
        @Override
        public void onLocationChanged(Location location) {
            barList.clear();    // clear the list of bars
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            getBars(latitude, longitude);
            /*reset the datas*/
            adapter = new BarAdapter(ListActivity.this, R.layout.list_item, barList);
            listView.setAdapter(adapter);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };
    private void getBars(final double lat, final double lon) {
        //get the bars' names and image urls
        new Thread(
                new Runnable(){

                    @Override
                    public void run() {
                        try{
                            StringBuilder url = new StringBuilder();
                            url.append("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=");
                            url.append(lat + ",");
                            url.append(lon);
                            url.append("&radius=2000&type=bar&keyword=bar&key=AIzaSyAxZCzOEbgGeEuSPkT7bWHe9AsAWB1bY6Q");
                            Log.v("url", url.toString());
                            HttpClient httpClient = new DefaultHttpClient();
                            HttpGet httpGet = new HttpGet(url.toString());

                            httpGet.addHeader("Accept-Language", "EN");
                            HttpResponse httpResponse = httpClient.execute(httpGet);    //send request

                            if(httpResponse.getStatusLine().getStatusCode() == 200){
                                HttpEntity entity = httpResponse.getEntity();
                                String response = EntityUtils.toString(entity, "utf-8");
                                JSONObject jsonObject = new JSONObject(response);

                                JSONArray resultArray = jsonObject.getJSONArray("results");

                                //iterate the result
                                if(resultArray.length() > 0) {
                                    String res = "";
                                    for (int i = 0, size = resultArray.length(); i < size; i++) {
                                        if (i >= 21) {
                                            //already find 20 bars
                                            break;
                                        }
                                        JSONObject bar = resultArray.getJSONObject(i);
                                        /*get the name of bar*/
                                        String name = bar.get("name").toString();
                                        /*get the photo reference of bar*/
                                        JSONArray photos = (JSONArray) bar.get("photos");
                                        String referencePhoto = photos.getJSONObject(0).get("photo_reference").toString();
                                        String urlImage = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + referencePhoto + "&key=AIzaSyAxZCzOEbgGeEuSPkT7bWHe9AsAWB1bY6Q";
                                        res = res + name + "," + urlImage + ";";
                                    }
                                    Message message = new Message();
                                    message.what = RESULT_OK;
                                    message.obj = res;
                                    handler.sendMessage(message);
                                }
                                else{
                                    Log.e("API_Error", "result is null");
                                }
                            }

                        } catch(Exception e){
                            e.printStackTrace();
                        }
                    }

                }
        ).start();

    }
    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            switch(msg.what){
                case RESULT_OK:
                    String result = (String)msg.obj;
                    String[] arrayResult = result.split(";");
                    for(String s : arrayResult){
                        String[] infoBar = s.split(",");
                        if(!barNameList.contains(infoBar[0]) && !infoBar[0].equals("")) {
                            barNameList.add(infoBar[0]);
                            Log.v("photos",infoBar[0]+","+infoBar[1]);
                            barList.add(new Bar(infoBar[0], infoBar[1]));
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };
}
