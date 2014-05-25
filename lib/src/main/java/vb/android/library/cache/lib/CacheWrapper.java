package vb.android.library.cache.lib;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Vincent Brison.
 */
public class CacheWrapper implements Serializable{

    Date mExpiryDate;
    Serializable mObject;

    /**
     * Create a wrapper to cache a object.
     * @param object is the object to cache.
     * @param expiryDate is the date of validity of the object. If the date is null,
     *                   the object is valid until the end of times.
     */
    public CacheWrapper(Serializable object, Date expiryDate) {
        mObject = object;
        mExpiryDate = expiryDate;
    }

}
