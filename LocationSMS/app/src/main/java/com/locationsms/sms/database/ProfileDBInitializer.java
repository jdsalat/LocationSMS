package com.locationsms.sms.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.locationsms.sms.beans.ProfileBean;
import com.locationsms.sms.beans.SMSBean;
import com.locationsms.sms.utils.ClientLogs;

/**
 * Created by Javed.Salat on 5/29/2016.
 */
public class ProfileDBInitializer {
    private static final String TAG = "ProfileDBInitializer";
    private static ProfileDBInitializer profileDBInitializer = null;
    private Context mContext;
    AssetDatabaseOpenHelper assetDatabaseOpenHelper = null;

    public ProfileDBInitializer(Context mContext) {
        this.mContext = mContext;
        this.assetDatabaseOpenHelper = new AssetDatabaseOpenHelper(mContext);
    }

    public static ProfileDBInitializer newInstance(Context context) {
        if (profileDBInitializer == null) {
            profileDBInitializer = new ProfileDBInitializer(context);
        }
        return profileDBInitializer;
    }

    public void insertProfileAndSMSDetails(ProfileBean profileBean, boolean isProfileToSave) {
        StringBuilder profileStringBuilder = new StringBuilder();
        profileStringBuilder.append("insert into profile_table(profile_id, profile_name) values(?,?)");
        printLog(profileStringBuilder.toString());
        SQLiteDatabase db = null;
        StringBuilder smsStringBuilder = new StringBuilder();
        printLog("profile_id:" + profileBean.getProfileId());
        smsStringBuilder.append("insert into sms_table(profile_id, request_code, duration, frequency, first_mobile, second_mobile, record_status) values(?,?,?,?,?,?,1)");
        try {
            this.assetDatabaseOpenHelper.createDatabase();

            db = this.assetDatabaseOpenHelper.getWritableDatabase();
            db.beginTransaction();
            if (isProfileToSave) {
                db.execSQL(profileStringBuilder.toString(), new String[]{profileBean.getProfileId(), profileBean.getProfileName()});
            }
            SMSBean smsBean = profileBean.getSmsBean();
            db.execSQL(smsStringBuilder.toString(), new String[]{profileBean.getProfileId() + "", smsBean.getRequestCode() + "", smsBean.getSmsDuration() + "", smsBean.getSmsFrequency() + "", smsBean.getFirstNumber(), smsBean.getSecondNumber()});
        } catch (Exception e) {
            printLog(e.getMessage());
        } finally {
            if (db != null) {
                db.setTransactionSuccessful();
                db.endTransaction();
                db.close();
                this.assetDatabaseOpenHelper.closeDataBase();
            }
        }

    }

    public Cursor getProfileBean() {
        Cursor cursor = null;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" select p.profile_id, p.profile_name, s.first_mobile,  s.second_mobile,  s.frequency, ");
        stringBuilder.append(" s.duration,  s.request_code, s.sms_id from profile_table p, sms_table s ");
        stringBuilder.append(" where s.profile_id=p.profile_id ");


        SQLiteDatabase sqLiteDatabase = null;
        try {
            assetDatabaseOpenHelper.createDatabase();
            sqLiteDatabase = assetDatabaseOpenHelper.getReadableDatabase();
            cursor = sqLiteDatabase.rawQuery(stringBuilder.toString(), new String[]{});
            cursor.moveToFirst();
        } catch (Exception e) {
            printLog(e.getMessage());
        } finally {
           /* if (sqLiteDatabase != null)
                sqLiteDatabase.close();*/
        }

        return cursor;
    }

    public Cursor getSMSDetails() {
        Cursor cursor = null;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" select   s.first_mobile,  s.second_mobile,  s.frequency, ");
        stringBuilder.append(" s.duration,  s.request_code, s.sms_id from  sms_table s where record_status=1");


        SQLiteDatabase sqLiteDatabase = null;
        try {
            assetDatabaseOpenHelper.createDatabase();
            sqLiteDatabase = assetDatabaseOpenHelper.getReadableDatabase();
            cursor = sqLiteDatabase.rawQuery(stringBuilder.toString(), new String[]{});
            cursor.moveToFirst();
        } catch (Exception e) {
            printLog(e.getMessage());
        } finally {
           /* if (sqLiteDatabase != null)
                sqLiteDatabase.close();*/
        }

        return cursor;
    }

    private static void printLog(String msg) {
        ClientLogs.printLogs(ClientLogs.errorLogType, TAG, msg);
    }

    public void deleteProfile(String profileId) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("delete from profile_table where profile_id=?");
        StringBuilder smsBuilder = new StringBuilder();
        smsBuilder.append("delete from sms_table where profile_id=?");
        SQLiteDatabase sqLiteDatabase = null;
        try {
            this.assetDatabaseOpenHelper.createDatabase();

            sqLiteDatabase = this.assetDatabaseOpenHelper.getWritableDatabase();
            sqLiteDatabase.beginTransaction();


            sqLiteDatabase.execSQL(stringBuilder.toString(), new String[]{profileId});
            sqLiteDatabase.execSQL(smsBuilder.toString(), new String[]{profileId});

        } catch (Exception e) {
            printLog(e.getMessage());
        } finally {
            if (sqLiteDatabase != null) {
                sqLiteDatabase.setTransactionSuccessful();
                sqLiteDatabase.endTransaction();
                sqLiteDatabase.close();
                this.assetDatabaseOpenHelper.closeDataBase();
            }
        }

    }

    public void deleteSMS(String smsId) {

        StringBuilder smsBuilder = new StringBuilder();
        smsBuilder.append("update sms_table set record_status=0 where sms_id=?");
        SQLiteDatabase sqLiteDatabase = null;
        try {
            this.assetDatabaseOpenHelper.createDatabase();

            sqLiteDatabase = this.assetDatabaseOpenHelper.getWritableDatabase();
            sqLiteDatabase.beginTransaction();
            sqLiteDatabase.execSQL(smsBuilder.toString(), new String[]{smsId});

        } catch (Exception e) {
            printLog(e.getMessage());
        } finally {
            if (sqLiteDatabase != null) {
                sqLiteDatabase.setTransactionSuccessful();
                sqLiteDatabase.endTransaction();
                sqLiteDatabase.close();
                this.assetDatabaseOpenHelper.closeDataBase();
            }
        }

    }

    public void updateSMSDetails(ProfileBean profileBean) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("update sms_table set request_code=? where profile_id=?");
        SQLiteDatabase sqLiteDatabase = null;
        try {
            this.assetDatabaseOpenHelper.createDatabase();

            sqLiteDatabase = this.assetDatabaseOpenHelper.getWritableDatabase();
            sqLiteDatabase.beginTransaction();


            sqLiteDatabase.execSQL(stringBuilder.toString(), new String[]{profileBean.getSmsBean().getRequestCode() + "", profileBean.getProfileId()});
        } catch (Exception e) {
            printLog(e.getMessage());
        } finally {
            if (sqLiteDatabase != null) {
                sqLiteDatabase.setTransactionSuccessful();
                sqLiteDatabase.endTransaction();
                sqLiteDatabase.close();
                this.assetDatabaseOpenHelper.closeDataBase();
            }
        }
    }
}
