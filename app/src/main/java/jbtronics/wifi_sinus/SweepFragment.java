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

package jbtronics.wifi_sinus;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.ToggleButton;

import wifi_sinus.api.DDSAnswer;
import wifi_sinus.api.WiFiSinus;


/**
 *  This Fragment controls Sweep Operations
 */
public class SweepFragment extends Fragment  implements WiFiSinus.onDDSError  {
    public static final String TAG = "SweepFragment";

    //Spinners
    private Spinner spinner_max_units;
    private Spinner spinner_min_units;
    private Spinner spinner_delay_units;
    private Spinner spinner_res_units;

    //EditTexts
    private EditText edit_sweep_max;
    private EditText edit_sweep_min;
    private EditText edit_sweep_delay;
    private EditText edit_sweep_res;

    //Switches
    private Switch switch_reverse;
    private Switch switch_pong;

    //Toggle
    private ToggleButton toogle_active;

    private WiFiSinus _dds;

    private WiFiSinus.onDDSError mListener;

    public SweepFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_sweep, container, false);

        //Populate objects with widgets
        spinner_max_units = (Spinner) v.findViewById(R.id.spinner_sweep_max);
        spinner_min_units = (Spinner) v.findViewById(R.id.spinner_sweep_min);
        spinner_delay_units = (Spinner) v.findViewById(R.id.spinner_sweep_delay);
        spinner_res_units = (Spinner) v.findViewById(R.id.spinner_sweep_resolution);
        edit_sweep_delay = (EditText) v.findViewById(R.id.edit_sweep_delay);
        edit_sweep_max = (EditText) v.findViewById(R.id.edit_sweep_max);
        edit_sweep_min = (EditText) v.findViewById(R.id.edit_sweep_min);
        edit_sweep_res = (EditText) v.findViewById(R.id.edit_sweep_res);
        switch_reverse = (Switch) v.findViewById(R.id.switch_sweep_reverse);
        switch_pong = (Switch) v.findViewById(R.id.switch_sweep_pong);
        toogle_active = (ToggleButton) v.findViewById(R.id.toggle_sweep_active);

        //Fill Units Spinner with the Data from Units Array
        ArrayAdapter<CharSequence> freq_units_adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.freq_units_array, android.R.layout.simple_spinner_item);
        freq_units_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_max_units.setAdapter(freq_units_adapter);
        spinner_min_units.setAdapter(freq_units_adapter);
        spinner_res_units.setAdapter(freq_units_adapter);

        ArrayAdapter<CharSequence> delay_units_adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.delay_units_array, android.R.layout.simple_spinner_item);
        delay_units_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_delay_units.setAdapter(delay_units_adapter);


        //Handle ToggleButton changes
        toogle_active.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)   //Button active
                {

                    Integer max = WiFiSinus.convertFreq(Double.valueOf(edit_sweep_max.getText().toString()), spinner_max_units.getSelectedItem().toString());
                    Integer min = WiFiSinus.convertFreq(Double.valueOf(edit_sweep_min.getText().toString()), spinner_min_units.getSelectedItem().toString());
                    Integer res = WiFiSinus.convertFreq(Double.valueOf(edit_sweep_res.getText().toString()), spinner_res_units.getSelectedItem().toString());
                    Integer delay = WiFiSinus.convertDelay(Double.valueOf(edit_sweep_delay.getText().toString()), spinner_delay_units.getSelectedItem().toString());

                    Boolean reverse = switch_reverse.isChecked();
                    Boolean pong = switch_pong.isChecked();

                    //Activate Sweep
                    _dds.activateSweep(min,max,delay,res,reverse,pong);
                }
                else   //Deactivate Sweep
                {
                    _dds.deactivateSweep();
                }

            }
        });


        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof WiFiSinus.onDDSError) {
            mListener = (WiFiSinus.onDDSError) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDDSError Interface");
        }
        if(_dds == null)
        {
            SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
            String s = prefs.getString("client_server_address","http://192.168.1.125");
            _dds = new WiFiSinus(s,getActivity());
            _dds.setOnDDSError(this);
        }

        AppCompatActivity main = ((AppCompatActivity) getActivity());
        main.getSupportActionBar().setTitle(R.string.action_bar_sweep);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDDSError(DDSAnswer error) {
        if(mListener!=null) {
            mListener.onDDSError(error);
        }
    }
}
