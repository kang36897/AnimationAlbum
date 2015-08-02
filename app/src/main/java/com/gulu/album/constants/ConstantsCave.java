package com.gulu.album.constants;

public interface ConstantsCave {

	// params for fragment
	public static final String PARAMS_APPLICATION_PACKAGE_INFORMATION = "package_info_for_application";
	public static final String PARAMS_ACTIVITY_INFO_LIST = "activity_info_list";
	public static final String PARAMS_SERVICE_INFO_LIST = "service_info_list";
	public static final String PARAMS_RECEIVER_INFO_LIST = "receiver_info_list";
	public static final String PARAMS_PERMISSION_INFO_LIST = "permission_info_list";
	public static final String PARAMS_PROVIDER_INFO_LIST = "provider_info_list";

	// fragment transaction
	public static final String FT_REQUEST_APPLICATION_DETAILS = "ft_request_applicaton_details";
	public static final String FT_REQUEST_ACTIVITY_LIST = "ft_request_activity_list";
	public static final String FT_REQUEST_SERVICE_LIST = "ft_request_service_list";
	public static final String FT_REQUEST_RECEIVER_LIST = "ft_request_receiver_list";
	public static final String FT_REQUEST_PERMISSION_LIST = "ft_request_permission_list";
	public static final String FT_REQUEST_PROVIDER_LIST = "ft_request_provider_list";

	// date format pattern constants for android platform not java
	public static final String DEFAULT_DATE_FORMAT_PATTERN = "yyyy-MM-dd hh:mm:ss";
}
