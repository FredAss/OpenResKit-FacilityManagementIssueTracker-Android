package de.htwberlin.f2.FacilityManagementIssueTracker;

import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class HubAccess {

    public static class WriteData extends AsyncTask<Task, Void, Task> {

        private CaptureTask captureTask;

        public WriteData(CaptureTask captureTask) {
            this.captureTask = captureTask;
        }

        @Override
        protected Task doInBackground(Task... params) {

            Task result = params[0];

            try {

                HttpResponse response;
                HttpParams httpParams = new BasicHttpParams();
                HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
                HttpProtocolParams.setContentCharset(httpParams, HTTP.UTF_8);
                httpParams.setBooleanParameter("http.protocol.expect-continue",
                        false);
                DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);

                JSONObject taskJson = new JSONObject();
                SimpleDateFormat df = new SimpleDateFormat(
                        "yyyy-MM-dd'T'HH:mm");

                taskJson.put("Location", params[0].getLocation());
                taskJson.put("Description", params[0].getDescription());
                taskJson.put("ReportDate", df.format(params[0].getReportDate()));
                taskJson.put("DueDate", df.format(params[0].getDueDate()));
                taskJson.put("Image", params[0].getImage());
                taskJson.put("IsTaskFixed", params[0].getIsTaskFixed());

                HttpPost request;

                if (params[0].getId() > 0) {
                    request = new HttpPost("http://141.45.165.154:7000/OpenResKitHub/Tasks(" + params[0].getId() + ")");
                    request.setHeader("X-HTTP-Method", "MERGE");
                } else {
                    request = new HttpPost("http://141.45.165.154:7000/OpenResKitHub/Tasks");
                    request.setHeader("X-HTTP-Method-Override", "PUT");
                }

                request.setHeader("Accept", "application/json");
                request.setHeader("Content-type", "application/json");

                StringEntity stringEntity = new StringEntity(taskJson.toString(), HTTP.UTF_8);
                stringEntity.setContentType("application/json");
                request.setEntity(stringEntity);
                response = httpClient.execute(request);
                HttpEntity responseEntity = response.getEntity();


                if (responseEntity != null) {
                    String jsonText = EntityUtils.toString(responseEntity, HTTP.UTF_8);

                    JSONObject answer = new JSONObject(jsonText);
                    ObjectMapper mapper = new ObjectMapper();
                    final DateFormat dfs = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    mapper.setDateFormat(dfs);
                    result = mapper.readValue(answer.toString(),
                            Task.class);
                    }
            } catch (Exception e) {
                e.printStackTrace();
            }
            result.setDueDate(NotifyHub(result.getId()));
            return result;
        }

        private Date NotifyHub(int id){

            final String NAMESPACE = "http://tempuri.org/";
            final String URL = "http://141.45.165.154:7000/ComputeEndDate";
            final String METHOD_NAME = "Calculate";
            final String SOAP_ACTION = "http://tempuri.org/ComputeEndDate/Calculate";

            Date calculatedDate = new Date();

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            PropertyInfo pi = new PropertyInfo();
            pi.setName("id");
            pi.setValue(id);
            request.addProperty(pi);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
            try {
                androidHttpTransport.call(SOAP_ACTION, envelope);

                SoapObject   resultsRequestSOAP = (SoapObject) envelope.bodyIn;

                String dateString = resultsRequestSOAP.getPropertyAsString("CalculateResult");

                Long l = Long.parseLong(dateString,10);
                calculatedDate = new Date(l-(120*60*1000));


            } catch (Exception e) {
                System.out.println("******* THERE WAS AN ERROR ACCESSING THE WEB SERVICE");
                e.printStackTrace();
            }
            return calculatedDate;
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Task result) {
            super.onPostExecute(result);

            captureTask.finishTask(result);
        }
    }

    public static class GetHubData extends AsyncTask<Void, Void, JSONArray> {

        private ListTasks listTasks;

        public GetHubData(ListTasks listTasks) {
            this.listTasks = listTasks;
        }

        @Override
        protected JSONArray doInBackground(Void... params) {

            HttpParams httpParams = new BasicHttpParams();
            HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
            httpParams.setBooleanParameter("http.protocol.expect-continue", false);
            HttpGet request = new HttpGet(
                    "http://141.45.165.154:7000/OpenResKitHub/Tasks?$format=json");
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");
            HttpClient httpClient = new DefaultHttpClient(httpParams);

            try {

                HttpResponse response = httpClient.execute(request);
                HttpEntity responseEntity = response.getEntity();
                String jsonText = EntityUtils.toString(responseEntity, HTTP.UTF_8);
                JSONObject serverFootprints = new JSONObject(jsonText);

                return serverFootprints.getJSONArray("value");
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }



        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            super.onPostExecute(result);
            listTasks.Initialize(result);
        }
    }

}
