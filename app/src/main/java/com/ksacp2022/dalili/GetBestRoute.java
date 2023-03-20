package com.ksacp2022.dalili;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpEntity;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpResponse;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.HttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpPost;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.entity.BasicHttpEntity;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.DefaultHttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.util.EntityUtils;

import org.apache.http.params.HttpParams;
import org.json.JSONObject;

 class GetBestRoute extends AsyncTask<String, Void, JSONObject> {
     ProgressDialog progressDialog;
     Context context;
     GoogleMap mMap;

     public GetBestRoute(Context context, GoogleMap mMap) {
         this.context = context;
         this.mMap = mMap;
         progressDialog = ProgressDialog.show(context, "Fetching Data", "Please wait");
     }

     @Override
    protected void onPreExecute() {
        super.onPreExecute();


    }

    @Override
    protected JSONObject doInBackground(String... params) {
        String response;

        try {
            HttpEntity httpEntity=new BasicHttpEntity();

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(params[0]);
            httppost.setHeader("Authorization","5b3ce3597851110001cf6248f3c27f1b4b1a447d9d3bda2eb29cd9f1");
            httppost.setHeader("Content-Type","application/json");

            HttpResponse responce = httpclient.execute(httppost);

            response = EntityUtils.toString(httpEntity);
            Log.d("response is", response);

            return new JSONObject(response);

        } catch (Exception ex) {

            ex.printStackTrace();

        }

        return null;
    }

    @Override
    protected void onPostExecute(JSONObject result)
    {
        super.onPostExecute(result);

        progressDialog.dismiss();

        if(result != null)
        {
            try
            {
                JSONObject jobj = result.getJSONObject("result");

                String status = jobj.getString("status");


            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            Toast.makeText(context, "Network Problem", Toast.LENGTH_LONG).show();
        }
    }
}
