package com.nurgulmantarci.realmdatabaseapp.apps;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class RaalmApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration configuration = new RealmConfiguration.Builder().name("nurgul.realm").deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(configuration);
    }
}
