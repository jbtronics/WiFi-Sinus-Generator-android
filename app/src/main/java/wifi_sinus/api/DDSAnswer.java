package wifi_sinus.api;

/**
 * Server Answers
 */

public class DDSAnswer {

    private boolean _success;
    private String _message;
    private Integer _http_code;
    private String _target;
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
        if(_http_code == 200)
        {
            _success = true;
        }
        else
        {
            _success = false;
        }
        _message = message;
        _target = target;
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
