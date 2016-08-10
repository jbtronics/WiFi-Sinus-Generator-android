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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;

import wifi_sinus.api.DDSAnswer;
import wifi_sinus.api.LedState;
import wifi_sinus.api.WiFiSinus;


/**
 * This Fragments shows helper tools.
 */
public class ToolsFragment extends Fragment implements WiFiSinus.onDDSError{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final String TAG = "Tool Fragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RadioGroup group_red;
    private RadioGroup group_green;
    private EditText edit_red_pwm;
    private EditText edit_green_pwm;

    private WiFiSinus.onDDSError mListener;

    private WiFiSinus _dds;

    public ToolsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ToolsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ToolsFragment newInstance(String param1, String param2) {
        ToolsFragment fragment = new ToolsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
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

        group_red.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId)
                {
                    case R.id.radio_red_on:
                        edit_red_pwm.setEnabled(false);
                        _dds.setRed(LedState.ON);
                        break;
                    case R.id.radio_red_off:
                        edit_red_pwm.setEnabled(false);
                        _dds.setRed(LedState.OFF);
                        break;
                    case R.id.radio_red_pwm:
                        edit_red_pwm.setEnabled(true);
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
                        //edit_green_pwm.setInputType(InputType.TYPE_NULL);
                        _dds.setGreen(LedState.ON);
                        break;
                    case R.id.radio_green_off:
                        edit_green_pwm.setEnabled(false);
                        //edit_green_pwm.setInputType(InputType.TYPE_NULL);
                        _dds.setGreen(LedState.OFF);
                        break;
                    case R.id.radio_green_pwm:
                        edit_green_pwm.setEnabled(true);
                        //edit_green_pwm.setInputType(InputType.TYPE_CLASS_NUMBER);
                        break;



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
