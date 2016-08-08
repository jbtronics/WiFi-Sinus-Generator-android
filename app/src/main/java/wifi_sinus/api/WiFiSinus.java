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

    private onDDSError _dds_error;
    private onDDSResult _dds_result;

    public final static Double[] PHASE_VALS = {0.0, 11.25, 22.5, 33.75, 45.0, 56.25, 67.5, 78.75, 90.0, 101.25, 112.5, 123.75, 135.0, 146.25, 157.5, 168.75,
            180.0, 191.25, 202.5, 213.75, 225.0, 236.25, 247.5, 258.75, 270.0, 281.25, 292.5, 303.75, 315.0, 326.25, 337.5, 348.75};


    public interface onDDSError
    {
        public void onDDSError(DDSAnswer error);
    }

    public interface onDDSResult
    {
        public void onDDSResult(DDSAnswer result);
    }

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
     * Puts DDS Module in the Power-Down Mode
     */
    public void DDSDown()
    {
        //TODO: Implement DDS Down
    }

    /**
     * Reactivate the DDS Output
     */
    public void DDSUp()
    {
        setValue("freq",_frequency.toString());
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
        _frequency = Integer.parseInt(frequency);

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

    /**
     * Sets the green led either on or off.
     * @param value The desired led state.
     */
    public void setGreen(LedState value)
    {
        if(value == LedState.ON)
        {
            setValue("green","on");
        }
        else if (value == LedState.OFF)
        {
            setValue("green","off");
        }
    }

    /**
     * Sets the green led either on or off.
     * @param value The desired led state.
     */
    public void setGreen(Integer value)
    {
        if(value == 1)
        {
            setValue("green","on");
        }
        else if (value == 0)
        {
            setValue("green","off");
        }
    }

    /**
     * Sets the PWM value of the green Led. Must between 0 and 1024
     * @param value The desired PWM value
     */
    public void GreenPWM(Integer value)
    {
        setValue("green",value.toString());
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
                        _result = new DDSAnswer(response,_result_status,set_url,DDSErrorLocation.Server);
                        Log.d("WiFi-DDS",_result.toString());
                        HandleError(_result);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                _finished = true;
                if(error.networkResponse != null) {
                    _result = new DDSAnswer( error.getMessage(), error.networkResponse.statusCode, set_url, DDSErrorLocation.Connection);
                }
                else
                {
                    _result = new DDSAnswer(error.getMessage(), 0, set_url, DDSErrorLocation.Connection);
                }
                HandleError(_result);
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


    private void HandleError(DDSAnswer error)
    {
        if(_dds_error!=null) {
            _dds_error.onDDSError(error);
        }
    }

    private void HandleResult(DDSAnswer result)
    {
        if(_dds_result!=null)
        {
            if(result.getSuccess()) {
                _dds_result.onDDSResult(result);
            }
            else
            {
                HandleError(result);
            }
        }
    }


    public void setOnDDSError(onDDSError handle)
    {
        _dds_error = handle;
    }

    public void setOnDDRResult(onDDSResult handle)
    {
        _dds_result = handle;
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

