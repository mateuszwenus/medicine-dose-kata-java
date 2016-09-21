package pl.pragmatists.trainings.medicinedosekata;

import pl.pragmatists.trainings.medicinedosekata.dependencies.AlertService;
import pl.pragmatists.trainings.medicinedosekata.dependencies.HealthMonitor;
import pl.pragmatists.trainings.medicinedosekata.dependencies.Medicine;
import pl.pragmatists.trainings.medicinedosekata.dependencies.MedicinePump;

public class DoseController {
	
	public static final int MIN_NORMAL_PRESSURE = 90;
	public static final int MAX_NORMAL_PRESSURE = 150;
	public static final int MIN_PRESSURE_FOR_ONE_DOSE = 60;

	private final HealthMonitor healthMonitor;
	private final MedicinePump medicinePump;
	
    public DoseController(HealthMonitor healthMonitor, MedicinePump medicinePump, AlertService alertService) {
    	this.healthMonitor = checkNotNull(healthMonitor, "healthMonitor");
    	this.medicinePump = checkNotNull(medicinePump, "medicinePump");
    	checkNotNull(alertService, "alertService");
    }
    
    private <T> T checkNotNull(T obj, String variableName) {
    	if (obj == null) {
    		throw new NullPointerException(variableName + " must not be null");
    	}
    	return obj;
    }

    public void checkHealthAndApplyMedicine() {
    	if (healthMonitor.getSystolicBloodPressure() < MIN_NORMAL_PRESSURE) {
    		medicinePump.dose(Medicine.PRESSURE_RAISING_MEDICINE);
    	}
    	if (healthMonitor.getSystolicBloodPressure() < MIN_PRESSURE_FOR_ONE_DOSE) {
    		medicinePump.dose(Medicine.PRESSURE_RAISING_MEDICINE);
    	}
    	if (healthMonitor.getSystolicBloodPressure() > MAX_NORMAL_PRESSURE) {
    		medicinePump.dose(Medicine.PRESSURE_LOWERING_MEDICINE);
    	}
    }
}
