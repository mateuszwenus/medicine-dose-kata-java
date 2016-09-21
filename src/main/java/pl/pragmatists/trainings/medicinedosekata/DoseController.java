package pl.pragmatists.trainings.medicinedosekata;

import pl.pragmatists.trainings.medicinedosekata.dependencies.AlertService;
import pl.pragmatists.trainings.medicinedosekata.dependencies.HealthMonitor;
import pl.pragmatists.trainings.medicinedosekata.dependencies.MedicinePump;

public class DoseController {

    public DoseController(HealthMonitor healthMonitor, MedicinePump medicinePump, AlertService alertService) {
    	checkNotNull(healthMonitor, "healthMonitor");
    	checkNotNull(medicinePump, "medicinePump");
    	checkNotNull(alertService, "alertService");
    }
    
    private void checkNotNull(Object obj, String variableName) {
    	if (obj == null) {
    		throw new NullPointerException(variableName + " must not be null");
    	}
    }

    public void checkHealthAndApplyMedicine() {

    }

}
