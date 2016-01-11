package com.zuehlke.carrera.javapilot.akka.rapidtweak.service;

import com.zuehlke.carrera.javapilot.akka.rapidtweak.android.MessageDispatcher;
import com.zuehlke.carrera.javapilot.akka.rapidtweak.power.PowerService;
import lombok.Getter;

/**
 * Created by Markus on 30.09.2015.
 */
public class ServiceManager {
    @Getter
    MessageDispatcher messageDispatcher;
    @Getter
    PowerService powerService;


    private static ServiceManager instance;

    private ServiceManager() {
        messageDispatcher = new MessageDispatcher();
        powerService = new PowerService();
    }

    public static ServiceManager getInstance() {
        if (instance == null) {
            synchronized (ServiceManager.class) {
                if (instance == null) {
                    instance = new ServiceManager();
                }
            }
        }
        return instance;
    }


}
