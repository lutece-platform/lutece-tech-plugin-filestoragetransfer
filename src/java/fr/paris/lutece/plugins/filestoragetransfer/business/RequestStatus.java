package fr.paris.lutece.plugins.filestoragetransfer.business;

import java.util.List;
import java.util.Locale;

import fr.paris.lutece.portal.service.i18n.I18nService;

public enum RequestStatus 
{
    STATUS_TODO("TODO", "filestoragetransfer.request_status.todo", "info"), 
    STATUS_DONE("DONE", "filestoragetransfer.request_status.done", "success"),
    STATUS_UNKNOWN("UNKNOWN", "filestoragetransfer.request_status.unknown", "default"),
    STATUS_FAILED("FAILED", "filestoragetransfer.request_status.failed", "warning"), 
    STATUS_ERROR("ERROR", "filestoragetransfer.request_status.error", "danger");


    private final String value;
    private final String code;
    private final String color;

    private RequestStatus(String value, String code, String color) 
    {
        this.value = value;
        this.code = code;
        this.color = color;
    }

    public static RequestStatus getRequestStatusByValue( String value ) 
    {
        for ( RequestStatus requestStatus : RequestStatus.values() ) 
        {
            if ( requestStatus.getValue().equals(value) ) 
            {
                return requestStatus;
            }
        }
        return RequestStatus.STATUS_UNKNOWN;
    }

    public String getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public String getColor() {
        return this.color;
    }

    public String getLocalStatus( Locale locale )
    {
        return I18nService.getLocalizedString( this.code, locale );
    }

    public static List<RequestStatus> getAllStatus() {
        return List.of( STATUS_TODO, STATUS_DONE, STATUS_FAILED, STATUS_ERROR );
    }
}
