package com.vincentbrison.openlibraries.android.dualcache.lib.testobjects;

public abstract class Vehicule {
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
        if (o instanceof Vehicule) {
            if (mName.equals(((Vehicule) o).getName())
                    && mWheels == ((Vehicule) o).getWheels()) {
                return true;
            } else {
                return false;
            }
        } else {
            return super.equals(o);
        }
    }
}
