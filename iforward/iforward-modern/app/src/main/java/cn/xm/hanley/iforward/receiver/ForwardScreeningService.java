package cn.xm.hanley.iforward.receiver;

import android.app.role.RoleManager;
import android.net.Uri;
import android.telecom.Call;
import android.telecom.CallScreeningService;
import android.telephony.PhoneNumberUtils;
import android.util.Log;

import java.util.List;

import cn.xm.hanley.iforward.domain.Contact;
import cn.xm.hanley.iforward.sqlite.BlockSQLite;
import cn.xm.hanley.iforward.utils.DataBaseFactoryUtil;

/** Refuses numbers in the existing forwarding list; carrier busy forwarding remains network-side. */
public class ForwardScreeningService extends CallScreeningService {
    private static final String TAG = "iForwardScreening";

    @Override
    public void onScreenCall(Call.Details details) {
        boolean reject = false;
        try {
            if (details.getCallDirection() == Call.Details.DIRECTION_INCOMING) {
                Uri handle = details.getHandle();
                String incoming = handle == null ? null : handle.getSchemeSpecificPart();
                reject = isOnForwardList(incoming);
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
    }

    private boolean isOnForwardList(String incoming) {
        if (incoming == null || incoming.trim().isEmpty()) return false;
        BlockSQLite db = DataBaseFactoryUtil.createFordwardDB(this);
        List<Contact> contacts = db.queryAllForwardContact();
        for (Contact contact : contacts) {
            String saved = contact.getContactNumber();
            if (saved != null && PhoneNumberUtils.compare(incoming, saved)) return true;
        }
        return false;
    }
}
