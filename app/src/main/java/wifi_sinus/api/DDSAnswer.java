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

/**
 * Server Answers
 */

public class DDSAnswer {

    private final boolean _success;
    private final String _message;
    private final Integer _http_code;
    private final String _target;
    private DDSErrorLocation _err_location;

    public DDSAnswer(boolean success,String message,Integer http_code,String target,DDSErrorLocation err_location)
    {
        _http_code = http_code;
        _message = message;
        _success = success;
        _target = target;
        _err_location = err_location;
    }

    /*
    public DDSAnswer(boolean success,String message,Integer http_code,String target)
    {
        _http_code = http_code;
        _message = message;
        _success = success;
        _target = target;
        _err_location = DDSErrorLocation.Server;
    }
*/
    public DDSAnswer(String message,Integer http_code,String target,DDSErrorLocation err_location)
    {
        _http_code = http_code;
        _success = _http_code == 200;
        _message = message;
        _target = target;
        _err_location = err_location;
    }

    public String toString()
    {
        if(_success)
        {
            return "Request on target " + _target + " successful! Got message: " + _message;
        }
        else
        {
            return "Request on target " + _target + " failed! Status Code: " + _http_code.toString() + " Got message: " + _message;
        }
    }

    public boolean getSuccess()
    {

        return _success;
    }
}
