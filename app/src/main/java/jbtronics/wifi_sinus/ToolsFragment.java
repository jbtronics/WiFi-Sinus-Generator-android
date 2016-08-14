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
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import wifi_sinus.api.DDSAnswer;
import wifi_sinus.api.LedState;
import wifi_sinus.api.WiFiSinus;


/**
 * This Fragments shows helper tools.
 */
public class ToolsFragment extends Fragment implements WiFiSinus.onDDSError{

    public static final String TAG = "Tool Fragment";

    private RadioGroup group_red;
    private RadioGroup group_green;
    private EditText edit_red_pwm;
    private EditText edit_green_pwm;

    private WiFiSinus.onDDSError mListener;

    private WiFiSinus _dds;


    public ToolsFragment() {
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
        View v = inflater.inflate(R.layout.fragment_tools, container, false);

        group_red = (RadioGroup) v.findViewById(R.id.radioGroup_red);
        group_green = (RadioGroup) v.findViewById(R.id.radioGroup_green);

        edit_green_pwm = (EditText) v.findViewById(R.id.edit_green_pwm);
        edit_red_pwm = (EditText) v.findViewById(R.id.edit_red_pwm);

        v.findViewById(R.id.edit_red_pwm).setEnabled(false);
        v.findViewById(R.id.edit_green_pwm).setEnabled(false);
        ((EditText) v.findViewById(R.id.edit_red_pwm)).setText("512");
        ((EditText) v.findViewById(R.id.edit_green_pwm)).setText("512");

        final SeekBar seek_red = (SeekBar) v.findViewById(R.id.seek_red_pwm);
        final SeekBar seek_green = (SeekBar) v.findViewById(R.id.seek_green_pwm);

        seek_red.setEnabled(false);
        seek_green.setEnabled(false);

        group_red.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId)
                {
                    case R.id.radio_red_on:
                        edit_red_pwm.setEnabled(false);
                        seek_red.setEnabled(false);
                        _dds.setRed(LedState.ON);
                        break;
                    case R.id.radio_red_off:
                        edit_red_pwm.setEnabled(false);
                        seek_red.setEnabled(false);
                        _dds.setRed(LedState.OFF);
                        break;
                    case R.id.radio_red_pwm:
                        edit_red_pwm.setEnabled(true);
                        seek_red.setEnabled(true);
                        break;
                }
            }
        });

        group_green.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId)
                {
                    case R.id.radio_green_on:
                        edit_green_pwm.setEnabled(false);
                        seek_green.setEnabled(false);
                        _dds.setGreen(LedState.ON);
                        break;
                    case R.id.radio_green_off:
                        edit_green_pwm.setEnabled(false);
                        seek_green.setEnabled(false);
                        _dds.setGreen(LedState.OFF);
                        break;
                    case R.id.radio_green_pwm:
                        edit_green_pwm.setEnabled(true);
                        seek_green.setEnabled(true);
                        break;
                }
            }
        });



        seek_red.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                edit_red_pwm.setText(Integer.valueOf(i).toString());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                updateRedPWM();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seek_green.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                edit_green_pwm.setText(Integer.valueOf(i).toString());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                updateGreenPWM();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return v;
    }


    private void updateRedPWM()
    {
        Integer pwm = Integer.valueOf(edit_red_pwm.getText().toString());
        _dds.RedPWM(pwm);
    }

    private void updateGreenPWM()
    {
        Integer pwm = Integer.valueOf(edit_green_pwm.getText().toString());
        _dds.GreenPWM(pwm);
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
        main.getSupportActionBar().setTitle(R.string.action_bar_tools);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDDSError(DDSAnswer error) {
        mListener.onDDSError(error);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
}
