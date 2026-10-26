package cn.xm.hanley.iforward.receiver;

import android.content.SharedPreferences;
import android.net.Uri;
import android.telecom.Call;
import android.telecom.CallScreeningService;
import android.telephony.PhoneNumberUtils;
import android.util.Log;

import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Calendar;

import cn.xm.hanley.iforward.constants.Constants;
import cn.xm.hanley.iforward.domain.Contact;
import cn.xm.hanley.iforward.sqlite.BlockSQLite;
import cn.xm.hanley.iforward.sqlite.HistorySQLite;
import cn.xm.hanley.iforward.utils.DataBaseFactoryUtil;

/** Refuses numbers in the existing forwarding list; carrier busy forwarding remains network-side. */
public class ForwardScreeningService extends CallScreeningService {
    private static final String TAG = "iForwardScreening";

    @Override
    public void onScreenCall(Call.Details details) {
        boolean reject = false;
        Contact matchedContact = null;
        try {
            if (details.getCallDirection() == Call.Details.DIRECTION_INCOMING) {
                Uri handle = details.getHandle();
                String incoming = handle == null ? null : handle.getSchemeSpecificPart();
                matchedContact = findForwardContact(incoming);
                reject = matchedContact != null && isWithinForwardTimeWindow();
            }
        } catch (RuntimeException e) {
            Log.e(TAG, "Could not check forwarding list; allowing call", e);
        }

        CallResponse.Builder response = new CallResponse.Builder();
        if (reject) {
            // Reject, rather than silence: this is the action intended to trigger carrier busy forwarding.
            response.setDisallowCall(true).setRejectCall(true);
        }
        respondToCall(details, response.build());
        if (reject) saveRejectedCall(matchedContact);
    }

    private boolean isWithinForwardTimeWindow() {
        SharedPreferences preferences = getSharedPreferences(Constants.SPS_FORWARD, MODE_PRIVATE);
        if (!preferences.getBoolean(Constants.SPS_TIME_WINDOW_ENABLED, false)) return true;

        int start = preferences.getInt(Constants.SPS_TIME_WINDOW_START, 9 * 60);
        int end = preferences.getInt(Constants.SPS_TIME_WINDOW_END, 18 * 60);
        Calendar now = Calendar.getInstance();
        int current = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE);

        // Equal endpoints mean the selected window covers the full day.
        if (start == end) return true;
        if (start < end) return current >= start && current < end;
        // Also support windows that cross midnight, such as 22:00–07:00.
        return current >= start || current < end;
    }

    private Contact findForwardContact(String incoming) {
        if (incoming == null || incoming.trim().isEmpty()) return null;
        BlockSQLite db = DataBaseFactoryUtil.createFordwardDB(this);
        List<Contact> contacts = db.queryAllForwardContact();
        for (Contact contact : contacts) {
            String saved = contact.getContactNumber();
            if (saved != null && PhoneNumberUtils.compare(incoming, saved)) return contact;
        }
        return null;
    }

    private void saveRejectedCall(Contact contact) {
        try {
            SharedPreferences preferences = getSharedPreferences(Constants.SPS_FORWARD, MODE_PRIVATE);
            String forwardedTo = preferences.getString(Constants.SPS_FORWARD_NUMBER, "");
            Date now = new Date();
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(now);
            String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(now);
            HistorySQLite history = DataBaseFactoryUtil.createHistoryDB(this);
            history.insert(contact.getContactName(), contact.getContactNumber(), date, time, forwardedTo);
        } catch (RuntimeException e) {
            // Never let local history storage prevent the call screening response.
            Log.e(TAG, "Could not save rejected call to history", e);
        }
    }
}
