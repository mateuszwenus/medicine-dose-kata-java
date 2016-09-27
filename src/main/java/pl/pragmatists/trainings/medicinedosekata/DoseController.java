package pl.pragmatists.trainings.medicinedosekata;

import pl.pragmatists.trainings.medicinedosekata.dependencies.AlertService;
import pl.pragmatists.trainings.medicinedosekata.dependencies.DoseUnsuccessfulException;
import pl.pragmatists.trainings.medicinedosekata.dependencies.HealthMonitor;
import pl.pragmatists.trainings.medicinedosekata.dependencies.Medicine;
import pl.pragmatists.trainings.medicinedosekata.dependencies.MedicinePump;

public class DoseController {

	public static final int MIN_MEDICINE_INTERVAL = 31;
	public static final int MIN_NORMAL_PRESSURE = 90;
	public static final int MAX_NORMAL_PRESSURE = 150;
	public static final int MIN_LOW_PRESSURE = 60;
	
	private static final int MAX_DOSE_ATTEMPTS = 8;

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
		int pressure = healthMonitor.getSystolicBloodPressure();
		if (isVeryLowPressure(pressure)) {
			dose(Medicine.PRESSURE_RAISING_MEDICINE, 2);
		} else if (isLowPressure(pressure)) {
			dose(Medicine.PRESSURE_RAISING_MEDICINE, 1);
		} else if (isHighPressure(pressure)) {
			dose(Medicine.PRESSURE_LOWERING_MEDICINE, 1);
		}
	}

	private boolean isHighPressure(int pressure) {
		return pressure > MAX_NORMAL_PRESSURE;
	}

	private boolean isLowPressure(int pressure) {
		return pressure < MIN_NORMAL_PRESSURE;
	}

	private boolean isVeryLowPressure(int pressure) {
		return pressure < MIN_LOW_PRESSURE;
	}

	private void dose(Medicine medicine, int doses) {
		if (medicinePump.getTimeSinceLastDoseInMinutes(medicine) < MIN_MEDICINE_INTERVAL) {
			return;
		}
		int successfullDoses = 0;
		int attempt = 0;
		while (successfullDoses < doses && attempt < MAX_DOSE_ATTEMPTS) {
			attempt++;
			try {
				medicinePump.dose(medicine);
				successfullDoses++;
			} catch (DoseUnsuccessfulException ignored) {
			}
		}
	}
}
