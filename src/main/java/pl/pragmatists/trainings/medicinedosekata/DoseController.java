package pl.pragmatists.trainings.medicinedosekata;

import pl.pragmatists.trainings.medicinedosekata.dependencies.AlertService;
import pl.pragmatists.trainings.medicinedosekata.dependencies.HealthMonitor;
import pl.pragmatists.trainings.medicinedosekata.dependencies.MedicinePump;

public class DoseController {

    public DoseController(HealthMonitor healthMonitor, MedicinePump medicinePump, AlertService alertService) {
    	if (healthMonitor == null) {
    		throw new NullPointerException("healthMonitor must not be null");
    	}
    	if (medicinePump == null) {
    		throw new NullPointerException("medicinePump must not be null");
    	}
    }

    public void checkHealthAndApplyMedicine() {

    }

}
