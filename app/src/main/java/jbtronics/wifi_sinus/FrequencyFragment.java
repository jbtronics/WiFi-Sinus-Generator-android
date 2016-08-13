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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import wifi_sinus.api.DDSAnswer;
import wifi_sinus.api.WiFiSinus;


/**
 * This Fragment controls the Frequency output.
 */
public class FrequencyFragment extends Fragment implements WiFiSinus.onDDSError {

    public static final String TAG = "Frequency Fragment";

    private Context _context;
    private WiFiSinus _dds;

    Integer frequency = 1000;
    Byte phase = 0;

    private Spinner spinner_freq_units;
    private Spinner spinner_phase;
    private EditText edit_freq;
    private Switch switch_active;
    private SeekBar seekBar_freq;

    private WiFiSinus.onDDSError mListener;

    public FrequencyFragment() {
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
        View v = inflater.inflate(R.layout.fragment_fragment_frequency, container, false);


        //Fill Units Spinner with the Data from Units Array
         spinner_freq_units = (Spinner) v.findViewById(R.id.spinner_freq_units);
        ArrayAdapter<CharSequence> units_adapter = ArrayAdapter.createFromResource(_context,
                R.array.freq_units_array, android.R.layout.simple_spinner_item);
        units_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_freq_units.setAdapter(units_adapter);
        //Set Handler what should happen if user Select when he select some Unit
        spinner_freq_units.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Update View for new Unit
                updateFrequency();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Fill Phase Spinner
        spinner_phase = (Spinner) v.findViewById(R.id.spinner_phase);
        ArrayAdapter<CharSequence> phase_adapter = ArrayAdapter.createFromResource(_context,
                R.array.phase_array, android.R.layout.simple_spinner_item);
        units_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_phase.setAdapter(phase_adapter);
        //Set Handler what should happen if user Select when he select some Phase
        spinner_phase.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                _dds.setPhaseRaw((byte) position);    //Position into Phase Raw Value
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        edit_freq = (EditText) v.findViewById(R.id.edit_freq);
        edit_freq.setImeActionLabel("Set", KeyEvent.KEYCODE_ENTER);
        switch_active = (Switch) v.findViewById(R.id.switch_freq_active);
        seekBar_freq = (SeekBar) v.findViewById(R.id.seek_freq);

        seekBar_freq.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        switch_active.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    _dds.DDSUp();
                }
                else
                {
                    _dds.DDSDown();
                }
            }
        });

        edit_freq.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    updateEdit();
                    return true;
                }
                return false;
            }
        });

        setBtnHandlers(v);

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
            _context = context;


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


    private void updateFrequency()
    {
        edit_freq.setText(_dds.getDisplayFreq(spinner_freq_units.getSelectedItem().toString()));
        updateSeekbar();
        //switch_active.setChecked(true);
        //TODO: Uncomment this to activate DDS Update
        _dds.setFrequency(frequency);
    }

    /**
     * Called when the Frequency should be updated via the EditBox
     */
    private void updateEdit()
    {
        Double d = Double.parseDouble(edit_freq.getText().toString());
        String unit = spinner_freq_units.getSelectedItem().toString();
        _dds.setFrequency(d,unit);
    }


    private void setBtnHandlers(View v)
    {
        View.OnClickListener cl = new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                Double freq = Double.parseDouble(edit_freq.getText().toString());
                String unit = spinner_freq_units.getSelectedItem().toString();
                _dds.setFrequency(freq,unit);
                switch(view.getId()) {
                    case R.id.btn_m1:
                        _dds.changeFrequency(-1);
                        break;
                    case R.id.btn_m1k:
                        _dds.changeFrequency(-1000);
                        break;
                    case R.id.btn_m1M:
                        _dds.changeFrequency(-1000000);
                        break;
                    case R.id.btn_m10:
                        _dds.changeFrequency(-10);
                        break;
                    case R.id.btn_m10k:
                        _dds.changeFrequency(-10000);
                        break;
                    case R.id.btn_m100:
                        _dds.changeFrequency(-100);
                        break;
                    case R.id.btn_m100k:
                        _dds.changeFrequency(-100000);
                        break;

                    case R.id.btn_p1:
                        _dds.changeFrequency(1);
                        break;
                    case R.id.btn_p1k:
                        _dds.changeFrequency(1000);
                        break;
                    case R.id.btn_p1M:
                        _dds.changeFrequency(1000000);
                        break;
                    case R.id.btn_p10:
                        _dds.changeFrequency(10);
                        break;
                    case R.id.btn_p10k:
                        _dds.changeFrequency(10000);
                        break;
                    case R.id.btn_p100:
                        _dds.changeFrequency(100);
                        break;
                    case R.id.btn_p100k:
                        _dds.changeFrequency(100000);
                        break;
                }
                edit_freq.setText(_dds.getDisplayFreq(spinner_freq_units.getSelectedItem().toString()));
            }
        };

        v.findViewById(R.id.btn_m1).setOnClickListener(cl);
        v.findViewById(R.id.btn_m1k).setOnClickListener(cl);
        v.findViewById(R.id.btn_m1M).setOnClickListener(cl);
        v.findViewById(R.id.btn_m10).setOnClickListener(cl);
        v.findViewById(R.id.btn_m10k).setOnClickListener(cl);
        v.findViewById(R.id.btn_m100).setOnClickListener(cl);
        v.findViewById(R.id.btn_m100k).setOnClickListener(cl);

        v.findViewById(R.id.btn_p1).setOnClickListener(cl);
        v.findViewById(R.id.btn_p1k).setOnClickListener(cl);
        v.findViewById(R.id.btn_p1M).setOnClickListener(cl);
        v.findViewById(R.id.btn_p10).setOnClickListener(cl);
        v.findViewById(R.id.btn_p10k).setOnClickListener(cl);
        v.findViewById(R.id.btn_p100).setOnClickListener(cl);
        v.findViewById(R.id.btn_p100k).setOnClickListener(cl);
    }
    

    private void updateFreqFromSeekBar(int progress)
    {
        //TODO:
    }

    private void updateSeekbar()
    {
        seekBar_freq.setProgress((int) (Math.sqrt(((frequency - 1) /(50000000 - 1)) * 1000.0f * 1000.0f)));
    }



}
