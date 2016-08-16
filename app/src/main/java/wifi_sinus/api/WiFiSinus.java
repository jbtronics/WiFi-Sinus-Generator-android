/*
 * Copyright (c) 2016 Jan Böhmer
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package wifi_sinus.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 *  A Class to control the WiFi-Sinus Generator.
 */
public class WiFiSinus {

    private final String _url;
    private final RequestQueue _queue;
    private final Context _context;
    private boolean _finished;
    private DDSAnswer _result;
    private int _result_status; //The Status Code of the result

    private Integer _frequency;
    private Byte _phase;

    private onDDSError _dds_error;
    private onDDSResult _dds_result;

    public final static Double[] PHASE_VALS = {0.0, 11.25, 22.5, 33.75, 45.0, 56.25, 67.5, 78.75, 90.0, 101.25, 112.5, 123.75, 135.0, 146.25, 157.5, 168.75,
            180.0, 191.25, 202.5, 213.75, 225.0, 236.25, 247.5, 258.75, 270.0, 281.25, 292.5, 303.75, 315.0, 326.25, 337.5, 348.75};

    private DDSMode _mode;

    public static final int FREQ_MAX = 50000000; //50MHz
    public static final int FREQ_MIN = 1; //1Hz

    public interface onDDSError
    {
        void onDDSError(DDSAnswer error);
    }

    public interface onDDSResult
    {
        void onDDSResult(DDSAnswer result);
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
        _frequency = 1000;
    }

    /**
     * Puts DDS Module in the Power-Down Mode
     */
    public void DDSDown()
    {
        setValue("mode","down");
        _mode = DDSMode.down;
    }

    /**
     * Reactivate the DDS Output
     */
    public void DDSUp()
    {
        setValue("mode","up");
        _mode = DDSMode.up;
    }

    /**
     * Activates the sweep output.
     * @param min The lower Frequency of the sweep (in Hz)
     * @param max The upper Frequency of the sweep (in Hz)
     * @param delay The delay between two steps (in µs)
     * @param resolution The width of a step (in Hz)
     * @param reverse Should the sweep go from max to min?
     * @param pong Should the sweep change its direction after touch a border.
     */
    public void activateSweep(int min,int max, int delay, int resolution, boolean reverse, boolean pong)
    {
        setSweep("min",Integer.toString(min));
        setSweep("max",Integer.toString(max));
        setSweep("delay",Integer.toString(delay));
        setSweep("resolution",Integer.toString(resolution));
        setSweep("reverse",BooleanToString(reverse));
        setSweep("pong",BooleanToString(pong));
        setSweep("mode","on");    //Activate sweep
    }

    /**
     * Activates the sweep output.
     * @param min The lower Frequency of the sweep (in Hz)
     * @param max The upper Frequency of the sweep (in Hz)
     * @param delay The delay between two steps (in µs)
     */
    public void activateSweep(int min, int max, int delay)
    {
        activateSweep(min,max,delay,1,false,false);
    }

    private String BooleanToString(boolean value)
    {
        if(value)
        {
            return "true";
        }
        else
        {
            return "false";
        }
    }

    /**
     * Deactivates the sweep output.
     *
     */
    public void deactivateSweep()
    {
        setSweep("mode","off");
    }

    /**
     * Checks if the Output is active.
     * @return true if Output is active.
     */
    public boolean isUp()
    {
        return (_mode == DDSMode.up);
    }

    /**
     * Gives the active mode of the DDS Generator
     * @return the active Mode
     */
    public DDSMode getMode()
    {
        return _mode;
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
     * @param on The desired led state.
     */
    public void setRed(boolean on)
    {
        if(on)
        {
            setValue("red","on");
        }
        else
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
     * @param on The desired led state.
     */
    public void setGreen(boolean on)
    {
        if(on)
        {
            setValue("green","on");
        }
        else
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
        makeRequest("set",param,value);
    }

    private void setSweep(String param,String value)
    {
        makeRequest("sweep",param,value);
    }

    private void makeRequest(String operation,String param,String value)
    {
        _finished = false;
        final String set_url = String.format(_url + "/" + operation + "?%1$s=%2$s", param, value);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, set_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        _finished = true;
                        _result = new DDSAnswer(response,_result_status,set_url,DDSErrorLocation.Server);
                        Log.d("WiFi-DDS",_result.toString());
                        HandleResult(_result);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                _finished = true;
                if(error.networkResponse != null) {
                    _result = new DDSAnswer(new String(error.networkResponse.data), error.networkResponse.statusCode, set_url, DDSErrorLocation.Connection);
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


    /**
     * Sets a handler, which is called if a error happens.
     * @param handle The handler which should be called in case of error.
     */
    public void setOnDDSError(onDDSError handle)
    {
        _dds_error = handle;
    }

    /**
     * Sets a handler, which is called if a command was executed successful;
     * @param handle The handler which should be called.
     */
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

    /**
     * Returns the answer of the last command.
     * @return The result.
     */
    public DDSAnswer getResult()
    {
        _finished = false;
        return _result;
    }

    /**
     * Get the frequency to show from a given unit
     * @param unit the Unit as a String
     * @return the Frequency in the unit as String
     */
    public String getDisplayFreq(String unit)
    {
        Double d = _frequency.doubleValue();
        switch (unit) {
            case "Hz":
                return d.toString();
            case "kHz":
                d = d / 1000;
                return d.toString();
            case "MHz":
                d = d / 1000000;
                return d.toString();
        }
        return d.toString();
    }

    /**
     * Sets the given frequency.
     * @param d The desired frequency.
     * @param unit The unit of the frequency.
     */
    public void setFrequency(Double d, String unit)
    {
        Integer i = d.intValue();

        switch (unit) {
            case "Hz":
                i = d.intValue();
                setFrequency(i);
                return;
            case "kHz":
                d = d * 1000;
                i = d.intValue();
                break;
            case "MHz":
                d = d * 1000000;
                i = d.intValue();
                break;
        }
        setFrequency(i);
    }

    /**
     * Changes the Frequency with the given difference.
     * @param change The difference between desired freq and momentary freq.
     * @param unit The unit of the given change.
     */
    public void changeFrequency(int change,String unit)
    {
        //Integer i = getRealFrequency(Double.parseDouble(edit_freq.getText().toString()));
        if(_frequency + change <= FREQ_MIN )
        {
            _frequency = FREQ_MIN;          //Freq must be at least 0 Hz
        }
        else if((_frequency + change )> FREQ_MAX)
        {
            _frequency = FREQ_MAX;   //maximum Freq = 50MHz
        }
        else
        {
            _frequency = _frequency + change;
        }
        setFrequency(_frequency);
    }

    /**
     * Changes the Frequency with the given difference.
     * @param change the difference between desired freq and momentary freq. (in Hz)
     */
    public void changeFrequency(int change)
    {
        changeFrequency(change,"Hz");
    }

    /**
     * Converts a  frequency with the given unit into Hz.
     * @param val the frequency value that should be converted
     * @param unit The unit of the given frequency (Hz, kHz, MHz, GHz).
     * @return the frequency converted into Hz.
     */
    public static int convertFreq(Double val, String unit)
    {
        Double d;
        if(unit.contains("k"))  //kHz
        {
            d = val * 1000;
        }
        else if(unit.contains("M")) //MHz
        {
            d = val * 1000000;
        }
        else if(unit.contains("G")) //GHz
        {
            d = val * 1000000000;
        }
        else                        //Hz
        {
            d = val;
        }
        return d.intValue();

    }

    /**
     * Converts a time value with the given unit into µs.
     * @param val The value which should be converted.
     * @param unit The unit of the given value.
     * @return The given value converted to µs.
     */
    public static int convertDelay(Double val, String unit)
    {
        Double d;
        switch (unit) {
            case "ms":
                d = val * 1000;
                break;
            case "s":
                d = val * 1000000;
                break;
            default:
                d = val;
                break;
        }
        return d.intValue();
    }

}

