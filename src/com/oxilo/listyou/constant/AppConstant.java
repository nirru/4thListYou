package com.oxilo.listyou.constant;

public class AppConstant {
    /*------------------------------------TO DELETE ALL THE ENTRY FROM DATABASE-----------------------------------------
    http://accelortech.com/im_chat/remove_logins.php?t=all
    */

    public static final String GCMSENDER = "gcm_id";
    /*------------------------------------URL------------------------------------*/
    public static final String SNS_REGISTRATION_URL = "http://www.accelortech.com/im_chat/api.php?q=sns_registration";
    public static final String URL_REGISTRATION_URL = "http://www.accelortech.com/im_chat/api.php?q=register";
    public static final String URL_LOGIN_URL = "http://www.accelortech.com/im_chat/api.php?q=login";
    public static final String SNS_LOGIN_URL = "http://www.accelortech.com/im_chat/api.php?q=sns_login";
    public static final String URL_EDIT_PROFILE_URL = "http://www.accelortech.com/im_chat/api.php?q=update_profile";
    public static final String URL_SHOW_PROFILE_URL = "http://www.accelortech.com/im_chat/api.php?q=get_detail";
    public static final String URL_FORGOT_PASSWORD_URL = "http://www.accelortech.com/im_chat/api.php?q=forgot_password";
    public static final String URL_QR_CODE_GET_CONTENT = "http://www.accelortech.com/im_chat/api.php?q=get_qrcode_url";
    public static final String GET_QR_CODE_USER_DETAIL = "http://www.accelortech.com/im_chat/api.php?q=get_qrcode_user_detail";
    public static final String ADD_AS_FRIENDS_API = "http://www.accelortech.com/im_chat/api.php?q=add_friend";
    public static final String RECEIVE_REQUEST_FRIENDS_API = "http://www.accelortech.com/im_chat/api.php?q=get_received_request";
    public static final String ACCEPT_INVITATION_FRIENDS_API = "http://www.accelortech.com/im_chat/api.php?q=accept_invitation";
    public static final String GET_FRIEND_LIST = "http://www.accelortech.com/im_chat/api.php?q=get_friend_list";
    public static final String GET_RECENT_FRIEND_LIST = "http://www.accelortech.com/im_chat/api.php?q=recent_added_friends";
    public static final String DENY_REQUEST = "http://www.accelortech.com/im_chat/api.php?q=deny_invitation";
    public static final String USER_LISTYOU_ID_URL = "http://www.accelortech.com/im_chat/api.php?q=add_listyou_uid";
    public static final String SEARCH_LISTYOU_USER = "http://www.accelortech.com/im_chat/api.php?q=search_listyouid";
    public static final String USER_DEFINED_GROUP = "http://www.accelortech.com/im_chat/api.php?q=add_user_group";
    public static final String ADD_MEMEBER_TO_GROUP = "http://accelortech.com/im_chat/api.php?q=add_friend_group";
    public static final String GET_GROUP_LIST = "http://www.accelortech.com/im_chat/api.php?q=get_user_groups";
    public static final String GET_COMMON_GROUP = "http://www.accelortech.com/im_chat/api.php?q=get_user_same_group";
    public static final String UPDATE_USER_SATUS = "http://www.accelortech.com/im_chat/api.php?q=update_user_status";
    public static final String GET_USER_SATUS = "http://www.accelortech.com/im_chat/api.php?q=get_user_status";
    public static final String MOVE_GROUP = "http://www.accelortech.com/im_chat/api.php?q=move_user_system_group";
    public static final String GET_USER_SYTEM_GROUP =  "http://www.accelortech.com/im_chat/api.php?q=get_user_system_group";
    public static final String DELETE_USER =  "http://www.accelortech.com/im_chat/api.php?q=remove_friend";
    public static final String REPORT_USER = "http://www.accelortech.com/im_chat/api.php?q=add_user_report";
    /**
     * ------------------------------------REGISTRATION AND LOGIN TYPE----------------------------------
     */
    public static final String REG_LOGIN_TYPE = "type";

    /*------------------------------------REGISTRATION AND LOGIN PREFS CONSTANT--------------*/
    public static final String PREFS_APP_LAUNCHES = "pref_app_launche";
    public static final String PREFS_FIRST_LAUNCHES = "pref_first_launche";
    public static final String PREFS_FIRST_LAUNCHES_CONTACT_LISTING = "pref_first_launche_contact_listing";
    public static final String PREFS_USER_ID = "user_id";
    public static final String PREFS_SCREEN_ID = "screen_id";
    public static final String USER = "user";
    public static final String PARCEABLE = "parceable_pref";
    public static final String PREFS_USER_DETAILS = "pref_User_Details";
    public static final String PREFS_FREINDS_LIST = "pref_friends_list";
    public static final String PREFS_RECENT_FREINDS_LIST = "pref__recent_friends_list";
    public static final String PREFS_GROUP = "pref_group_list";
    public static final String PREFS_FREINDS_DETAILS = "pref_freinds_detail";
    public static final String PREFS_COWORKER_SYTEM_GROUP = "pref_coworker_system_group";
    public static final String PREFS_CLIENT_SYTEM_GROUP = "pref_client_system_group";
    public static final String PREFS_PARTNER_SYTEM_GROUP = "pref_partner_system_group";
    public static final String PREFS_AQUITANCE_SYTEM_GROUP = "pref_aquitance_system_group";
    public static final String PREFS_GOOD_TO_KNOW_SYTEM_GROUP = "pref_good_to_know_system_group";
    public static final String PREFS_FAMILY_SYTEM_GROUP = "pref_family_system_group";
    public static final String PREFS_OTHERS_SYTEM_GROUP = "pref_others_system_group";

    /*------------------------------------REGISTRATION APP CONSTANT--------------*/
    public static final String REG_SNS_TOKEN = "sns_unique_token";
    public static final String REG_TYPE = "listyou";
    public static final String REG_ID = "uid";
    public static final String REG_FIRST_NAME = "fname";
    public static final String REG_LAST_NAME = "lname";
    public static final String REG_EMAIL = "email";
    public static final String REG_PASSWORD = "password";
    public static final String REG_MESSAGE = "message";
    public static final String REG_SUCCESSFULL_MESSAGE = "Registration Success";
    public static final String REG_SNS_SUCCESSFULL_MESSAGE = "Registration Success";
    public static final String REG_SNS_ALREADY_EXIST = "User already exist";

	/*------------------------------------LOGIN APP CONSTANT--------------*/

    public static final String LOGIN_EMAIL = "email";
    public static final String LOGIN_PASSWORD = "password";
    public static final String LOGIN_ID = "uid";
    public static final String LOGIN_MESSAGE = "message";
    public static final String LOGIN_SUCCESSFULL_MESSAGE = "LOGIN_SUCCESS";
    public static final String LOGIN_USE_ID = "uid";
    public static final String OS_TYPE = "os";
    public static final String DEVICE_ID = "device_id";
    /*------------------------------------EDIT APP CONSTANT--------------*/
    public static final String EDIT_PROFILE_MESSAGE = "message";
    public static final String EDIT_PROFILE_SUCCESSFULL_MESSAGE = "Updated successfully";
    public static final String EDIT_PROFILE_ALREADY_UPDATED = "Already updated";
    public static final String EDIT_PROFILE_LOGIN_ID = "uid";
    public static final String EDIT_PROFILE_FIRST_NAME = "fname";
    public static final String EDIT_PROFILE_LAST_NAME = "lname";
    public static final String EDIT_PROFILE_FIRST_NAME_IN_ANOTHER_LANGUAGE = "lang_fname";
    public static final String EDIT_PROFILE_LAST_NAME_IN_ANOTHER_LANGUAGE = "lang_lname";
    public static final String EDIT_PROFILE_COMPANY_EMAIL = "email";
    public static final String EDIT_PROFILE_ANOTHER_EMAIL = "email2";
    public static final String EDIT_PROFILE_COMPANY_TITLE = "designation";
    public static final String EDIT_PROFILE_COMPANY_NAME = "company_name";
    public static final String EDIT_PROFILE_COUNTRY_CODE = "country_code";
    public static final String EDIT_PROFILE_MOBILE_NUMBER = "mobile";
    public static final String EDIT_PROFILE_FAX_NUMBER = "fax";
    public static final String EDIT_PROFILE_TELEPHONE_NUMBER = "telephone";
    public static final String EDIT_PROFILE_COUNTRY = "country";
    public static final String EDIT_PROFILE_CITY = "city";
    public static final String EDIT_PROFILE_ADDRESS = "address";
    public static final String EDIT_PROFILE_WEBSITE = "website";
    public static final String EDIT_PROFILE_PROFILE_PIC = "profile_picture";
    public static final String EDIT_PROFILE_COMPANY_LOGO_PIC = "company_logo";
    public static final String EDIT_PROFILE_BUSSINESS_LOGO_PIC = "business_logo";
    public static final String EDIT_PROFILE_QR_LOGO_PIC = "qr_image";
    public static final String EDIT_PROFILE_SKYPE_ID = "skype";
    public static final String QR_IMAGE_URL = "qr_image_url";
    public static final String QR_UID = "qr_uid";
    public static final String LIST_YOU_ID = "listyou_uid";
    public static final String LIST_YOU_ID_CREATION_DATE = "signup_date";

	
	/*------------------------------------FORGOT PASSWORD CONSTANT--------------*/

    public static final String FORGOT_EMAIL = "email";
    public static final String FORGOT_MESSAGE = "message";
    public static final String FORGOT_PASSWORD_SUCCESSFULL_MESSAGE = "An Email with changing password link has been sent to you. Please check your inbox.";
    public static final String FORGOT_PASSWORD_MESSAGE = "Reset password link has been sent.";
    /*------------------------------------SHOW PROFILE CONSTANT--------------*/
    public static final String SHOW_PROFILE_PROFILE_PIC_URL = "profile_pic_url";
    public static final String SHOW_PROFILE_COMPANY_PIC_URL = "company_logo_url";
    public static final String SHOW_PROFILE_BUSSINESS_PIC_URL = "business_card_url";
    public static final String SHOW_PROFILE_MESSAGE = "Invalid user id";


    /*------------------------------------DECODER ACTIVITY CONSTANT--------------*/

    public static final String DECODER_USERNAME = "userName";
    public static final String DECODER_DESIGNATION = "designation";
    public static final String DECODER_COMPANY_NAME = "company_name";
    public static final String DECODER_PROFILE_PIC_URL = "profile_pic";
    public static final String DECODER_COMPANY_PIC_URL = "company_logo_url";
    public static final String SENDER_UID = "uid";
    /*------------------------------------ADD LIST FRAGEMENT--------------*/
    public static final String RECEIVER_UID = "to_uid";
    public static final String RECEIVER_USERNAME = "userName";
    public static final String RECEIVER_PROFILE_PIC_URL = "profile_pic";
    public static final String RECEIVER_COMPANY_NAME = "company_name";
    public static final String UIDS = "uids";

    /*------------------------------------GROUP CONSTANT--------------*/
    public static final String GROUP_NAME = "group";
    public static final String GROUP_IMAGE = "image";
    public static final String NAME_SET_TO_GROUP = "group_name";
    public static final String IMAGE_SET_TO_GROUP = "image_url";
    public static final String GROUP_MEMBER_COUNT = "usercount";

    /*------------------------------------Move GROUP CONSTANT--------------*/
    public static final String OLD_GROUP_NAME = "group";
    public static final String NEW_GROUP_NAME = "new_group";

    /*-----------------------------------SELF PROFILE DETAILS CONSTANT--------------*/
    public static final String SELF_STATUS = "status";
    public static final String APP_USER_STATUS = "user_status";

       /*------------------------------------Report USer CONSTANT--------------*/
       public static final String REPORT_MEMBER_MESSAGE = "message";
}
