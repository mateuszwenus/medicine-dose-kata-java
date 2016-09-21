package pl.pragmatists.trainings.medicinedosekata;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import org.junit.Test;

import pl.pragmatists.trainings.medicinedosekata.dependencies.AlertService;
import pl.pragmatists.trainings.medicinedosekata.dependencies.HealthMonitor;
import pl.pragmatists.trainings.medicinedosekata.dependencies.MedicinePump;

public class MedicineDosingTest {

    @Test
    public void DoseController_should_throw_NPE_when_healthMonitor_is_null() {
    	// given
    	MedicinePump medicinePump = mock(MedicinePump.class);
		AlertService alertService = mock(AlertService.class);
		try {
			// when
			new DoseController(null, medicinePump, alertService);
			fail();
		} catch (NullPointerException expected) {
			// then
		}
    }
    
    @Test
    public void DoseController_should_throw_NPE_when_medicinePump_is_null() {
    	// given
    	HealthMonitor healthMonitor = mock(HealthMonitor.class);
		AlertService alertService = mock(AlertService.class);
		try {
			// when
			new DoseController(healthMonitor, null, alertService);
			fail();
		} catch (NullPointerException expected) {
			// then
		}
    }

}
