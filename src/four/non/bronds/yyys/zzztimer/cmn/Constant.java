package four.non.bronds.yyys.zzztimer.cmn;

public class Constant {

	public static final	String	APP_NAME = "Zzz Timer";
	public static final	String	FS_APP_DIR_NAME = "zzztimer";
	public static final	String	SHARED_PREF = "zzztimer";
	
	
	public static final int		SEL_APP_MODE_KILL = 0;
	public static final int		SEL_APP_MODE_STAT = 1;

	public static final	String	INTENT_TAG_TEIMER_ITEM= "TEIMER_ITEM";
	public static final	String	INTENT_TAG_SEL_APP_MODE = "MODE";
	
	
	public static final int		RQ_CODE_KILL_APP = 600;
	public static final int		RQ_CODE_STARTL_APP = 700;
	public static final int		RQ_CODE_WOL = 800;
	public static final int		RQ_CODE_ADD_COMPUTER = 900;
	public static final int		RQ_CODE_EDT_COMPUTER = 901;
	public static final int		RQ_CODE_MUSIC_PLAY_LIST_EDIT = 1000;
	public static final int		repeat_val[] = {0,10,30,60,120,300, 600,1800,3600};

	public static final	String	PACKAGE_PIPOPA_ROID = "four.non.bronds.yyys.pipoparoid";
	public static final	String	PACKAGE_DEBUG = "com.with21.laaex";
	public static final	String	PACKAGE_ME = "four.non.bronds.yyys.zzztimer";
	
	public static final int		REC_FORMAT_PCM		= 0;
	public static final int		REC_FORMAT_OGG		= 1;
	
	/* SharedPrefの設定 */
	public static final	String	SHARED_PREF_REC_BIT_PER_SEC 			= "rec_sampling_rate";
	public static final	String	SHARED_PREF_REC_FORMAT					= "rec_format";
	public static final	String	SHARED_PREF_DBG_PLAYLIST_WHERE			= "play_list_wehere";
	public static final	String	SHARED_PREF_PLAYMUSIC_VOLUM				= "play_music_volum";
	public static final	String	SHARED_PREF_DBG_LOG_DBGMODE				= "log_dbgmode";
	public static final	String	SHARED_PREF_DBG_LOG_OPT_ERR				= "log_opt_err";
	public static final	String	SHARED_PREF_DBG_LOG_OPT_FUNC			= "log_opt_func";
	public static final	String	SHARED_PREF_DBG_LOG_OPT_INF				= "log_opt_inf";
	public static final	String	SHARED_PREF_DBG_LOG_OPT_TRACE			= "log_opt_trace";
	public static final	String	SHARED_PREF_DBG_LOG_OPT_SEQ				= "log_opt_seq";
	public static final	String	SHARED_PREF_DBG_LOG_OPT_SQL				= "log_opt_sql";
	public static final	String	SHARED_PREF_DBG_LOG_OPT_W_FILE			= "log_opt_write_file";
	public static final	String	SHARED_PREF_DBG_LOG_OPT_W_CAT			= "log_opt_write_cat";
	public static final	String	SHARED_PREF_DBG_LOG_FILE_NAME			= "log_file_name";
	public static final String SHARED_PREF_DB_INITIALIZED 				= "db_initialized";
	
	
	//public static final String  	PURCHASE_OGG						= "android.test.purchased";//"ogg";
	public static final String  	PURCHASE_OGG						= "ogg";
	
	
	//--------------------------------------------------------------------------------------------
	//	内部課金の設定情報
	//--------------------------------------------------------------------------------------------
    // The response codes for a request, defined by Android Market.
    public enum ResponseCode {
        RESULT_OK,
        RESULT_USER_CANCELED,
        RESULT_SERVICE_UNAVAILABLE,
        RESULT_BILLING_UNAVAILABLE,
        RESULT_ITEM_UNAVAILABLE,
        RESULT_DEVELOPER_ERROR,
        RESULT_ERROR;

        // Converts from an ordinal value to the ResponseCode
        public static ResponseCode valueOf(int index) {
            ResponseCode[] values = ResponseCode.values();
            if (index < 0 || index >= values.length) {
                return RESULT_ERROR;
            }
            return values[index];
        }
    }

    // The possible states of an in-app purchase, as defined by Android Market.
    public enum PurchaseState {
        // Responses to requestPurchase or restoreTransactions.
        PURCHASED,   // ユーザーは、注文を請求しました.
        CANCELED,    // The charge failed on the server.
        REFUNDED;    // ユーザーは、注文の払い戻しを受けた。

        // Converts from an ordinal value to the PurchaseState
        public static PurchaseState valueOf(int index) {
            PurchaseState[] values = PurchaseState.values();
            if (index < 0 || index >= values.length) {
                return CANCELED;
            }
            return values[index];
        }
    }

    /** This is the action we use to bind to the MarketBillingService. */
    public static final String MARKET_BILLING_SERVICE_ACTION =
        "com.android.vending.billing.MarketBillingService.BIND";

    // Intent actions that we send from the BillingReceiver to the
    // BillingService.  Defined by this application.
    public static final String ACTION_CONFIRM_NOTIFICATION =
            "com.example.subscriptions.CONFIRM_NOTIFICATION";
    public static final String ACTION_GET_PURCHASE_INFORMATION =
            "com.example.subscriptions.GET_PURCHASE_INFORMATION";
    public static final String ACTION_RESTORE_TRANSACTIONS =
            "com.example.subscriptions.RESTORE_TRANSACTIONS";

    // Intent actions that we receive in the BillingReceiver from Market.
    // These are defined by Market and cannot be changed.
    public static final String ACTION_NOTIFY = "com.android.vending.billing.IN_APP_NOTIFY";
    public static final String ACTION_RESPONSE_CODE =
        "com.android.vending.billing.RESPONSE_CODE";
    public static final String ACTION_PURCHASE_STATE_CHANGED =
        "com.android.vending.billing.PURCHASE_STATE_CHANGED";

    // These are the names of the extras that are passed in an intent from
    // Market to this application and cannot be changed.
    public static final String NOTIFICATION_ID = "notification_id";
    public static final String INAPP_SIGNED_DATA = "inapp_signed_data";
    public static final String INAPP_SIGNATURE = "inapp_signature";
    public static final String INAPP_REQUEST_ID = "request_id";
    public static final String INAPP_RESPONSE_CODE = "response_code";

    // These are the names of the fields in the request bundle.
    public static final String BILLING_REQUEST_METHOD = "BILLING_REQUEST";
    public static final String BILLING_REQUEST_API_VERSION = "API_VERSION";
    public static final String BILLING_REQUEST_PACKAGE_NAME = "PACKAGE_NAME";
    public static final String BILLING_REQUEST_ITEM_ID = "ITEM_ID";
    public static final String BILLING_REQUEST_ITEM_TYPE = "ITEM_TYPE";
    public static final String BILLING_REQUEST_DEVELOPER_PAYLOAD = "DEVELOPER_PAYLOAD";
    public static final String BILLING_REQUEST_NOTIFY_IDS = "NOTIFY_IDS";
    public static final String BILLING_REQUEST_NONCE = "NONCE";

    public static final String BILLING_RESPONSE_RESPONSE_CODE = "RESPONSE_CODE";
    public static final String BILLING_RESPONSE_PURCHASE_INTENT = "PURCHASE_INTENT";
    public static final String BILLING_RESPONSE_REQUEST_ID = "REQUEST_ID";
    public static long BILLING_RESPONSE_INVALID_REQUEST_ID = -1;

    // These are the types supported in the IAB v2
    public static final String ITEM_TYPE_INAPP = "inapp";
    public static final String ITEM_TYPE_SUBSCRIPTION = "subs";
	
}

