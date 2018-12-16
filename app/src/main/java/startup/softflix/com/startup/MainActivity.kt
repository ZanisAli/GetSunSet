package startup.softflix.com.startup

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }


      fun GetSunset(view: View)
      {
          //toast is to check that function is being called or not
          Toast.makeText(this,"Checking", Toast.LENGTH_LONG)
        var city=etCityName.toString()
        val url="https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22"+ city +"%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys"
        //this will directly fire function of asynch class that is do in bg
        MyAsynchTask().execute(url)
    }



    //sending httprequest and getting data will take alot of time and app can crash so use thread or asynch class

    //3 instances can send but do in bg is using first instance
    inner class MyAsynchTask: AsyncTask<String, String, String>()
    {
        override fun onPreExecute() {
            //will be executed before task started
        }
        override fun doInBackground(vararg params: String?): String {
            //TODO http call
            try {
                //0 means first instance
                val url=URL(params[0])
                val urlConnect=url.openConnection() as HttpURLConnection //data coming is stream
                urlConnect.connectTimeout=7000
                //convert to string
                var inString= ConvertStreamToString(urlConnect.inputStream)

                //can't access ui so have to do in onprogressupdate

                publishProgress(inString)
            }catch (ex:Exception){}

            return " "

        }

        override fun onProgressUpdate(vararg values: String?) {
            try {
               var json=JSONObject(values[0]) //input is string so convert into json object
                val query= json.getJSONObject("query") //as in weather api in JAVA, first element we have to get is query so have to get that also
                val results= query.getJSONObject("results")
                val channel= results.getJSONObject("channel")
                val astronomy= channel.getJSONObject("astronomy")
                val sunrise=astronomy.getString("sunrise")
                //showing it to ui now
                tvSunSetTime.text="Sunrise time is "+ sunrise

            }catch (ex:Exception){}
        }
        override fun onPostExecute(result: String?) {
            //after task done
        }

    }

    fun ConvertStreamToString(inputStream:InputStream):String{
        val bufferReader=BufferedReader(InputStreamReader(inputStream))
        var line:String
        var AllString:String=""


        try {
            do {
                line = bufferReader.readLine()
                if (line != null) {
                    AllString += line

                }


            }

            while (line != null)
            inputStream.close()
        }catch (ex:Exception){}

        return AllString
    }
}
