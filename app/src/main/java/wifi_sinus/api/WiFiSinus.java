package wifi_sinus.api;

import android.content.Context;
import android.util.Log;

import java.net.URL;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 * Created by janhb on 05.08.2016.
 *
 */
public class WiFiSinus {

    private String _url;
    private RequestQueue _queue;
    private Context _context;
    private boolean _finished;
    private DDSAnswer _result;
    private int _result_status; //The Status Code of the result

    private Integer _frequency;
    private Byte _phase;


    /**
     * Creates a new WiFi Sinus Object to interface with the Wifi Sinus Generator
     * @param url The Address of the Generator
     * @param context The Context of the App.
     */
    public WiFiSinus(String url, Context context)
    {
        _queue = Volley.newRequestQueue(context);
        _context = context;
        _url = url;
        _finished = false;
        _frequency = 0;
    }

    /**
     * Sets the Output Frequency.
     * @param frequency The desired Output Frequency
     */
    public void setFrequency(Integer frequency)
    {
        setValue("freq",frequency.toString());
        _frequency = frequency;
    }

    public void setFrequency(String frequency)
    {
        setValue("freq",frequency);

    }

    /**
     * Sets the Phase in "raw" format
     * @param phase The desired phase in the "raw" the AD9850 gets directly
     */
    public void setPhaseRaw(Byte phase)
    {
        setValue("phase",phase.toString());
        _phase = phase;
    }

    /**
     * Sets the red led either on or off.
     * @param value The desired led state.
     */
    public void setRed(LedState value)
    {
        if(value == LedState.ON)
        {
            setValue("red","on");
        }
        else if (value == LedState.OFF)
        {
            setValue("red","off");
        }
    }

    /**
     * Sets the red led either on or off.
     * @param value The desired led state.
     */
    public void setRed(Integer value)
    {
        if(value == 1)
        {
            setValue("red","on");
        }
        else if (value == 0)
        {
            setValue("red","off");
        }
    }

    /**
     * Sets the PWM value of the red Led. Must between 0 and 1024
     * @param value The desired PWM value
     */
    public void RedPWM(Integer value)
    {
        setValue("red",value.toString());
    }


    private void setValue(String param,String value)
    {
        _finished = false;
        final String set_url = String.format(_url + "/set?%1$s=%2$s", param, value);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, set_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        _finished = true;
                        _result = new DDSAnswer(_result_status,response,set_url,DDSErrorLocation.Server);
                        Log.d("WiFi-DDS",_result.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                _finished = true;
                _result = new DDSAnswer(false,error.getMessage(),error.networkResponse.statusCode,set_url,DDSErrorLocation.Connection);
                Log.w("WiFi-DDS",_result.toString());
            }
        }) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                if (response != null) {
                     _result_status =  response.statusCode;
                }
                return super.parseNetworkResponse(response);
            }
        };


// Add the request to the RequestQueue.
        _queue.add(stringRequest);
    }

    /**
     * Checks if the last Operation was finished
     * @return true if the last operation was finished
     */
    public boolean isFinished()
    {
        return _finished;
    }

    public DDSAnswer getResult()
    {
        _finished = false;
        return _result;
    }

}

