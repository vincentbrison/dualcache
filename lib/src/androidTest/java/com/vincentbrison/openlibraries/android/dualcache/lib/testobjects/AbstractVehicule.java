package com.vincentbrison.openlibraries.android.dualcache.lib.testobjects;

public abstract class AbstractVehicule {
    protected String mName;
    protected int mWheels;

    public String getName() {
        return mName;
    }

    public int getWheels() {
        return mWheels;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof AbstractVehicule) {
            if (mName.equals(((AbstractVehicule) o).getName())
                    && mWheels == ((AbstractVehicule) o).getWheels()) {
                return true;
            } else {
                return false;
            }
        } else {
            return super.equals(o);
        }
    }
}
