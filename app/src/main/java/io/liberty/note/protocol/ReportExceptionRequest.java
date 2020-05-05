package io.liberty.note.protocol;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class ReportExceptionRequest {

    /**
     * Manufacturer name
     */
    @JsonProperty("deviceManufacturer")
    public String deviceManufacturer;

    /**
     * Manufacturer-defined model name for the device
     */
    @JsonProperty("deviceModel")
    public String deviceModel;

    /**
     * Operating system name, i.e. Android
     */
    @JsonProperty("systemName")
    public String systemName;

    /**
     * Operating system version, build number, or both
     */
    @JsonProperty("systemVersion")
    public String systemVersion;

    /**
     * Application id
     */
    @JsonProperty("applicationId")
    public String applicationId;

    /**
     * Application version
     */
    @JsonProperty("applicationVersion")
    public String applicationVersion;

    /**
     * Name of class that reported the exception; typically an activity class
     */
    @JsonProperty("className")
    public String className;

    /**
     * Class constant indicating the location or nature of the problem
     */
    @JsonProperty("fault")
    public String fault;

    /**
     * Stack trace from the exception
     */
    @JsonProperty("stacktrace")
    public String stacktrace;


    /**
     * Additional information that may be useful in diagnosing the problem
     */
    @JsonProperty("info")
    public String info;
}
