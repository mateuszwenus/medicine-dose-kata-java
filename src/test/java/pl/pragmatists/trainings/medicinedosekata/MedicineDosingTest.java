package pl.pragmatists.trainings.medicinedosekata;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import pl.pragmatists.trainings.medicinedosekata.dependencies.AlertService;
import pl.pragmatists.trainings.medicinedosekata.dependencies.HealthMonitor;
import pl.pragmatists.trainings.medicinedosekata.dependencies.MedicinePump;

@RunWith(MockitoJUnitRunner.class)
public class MedicineDosingTest {

	@Mock
	private HealthMonitor healthMonitor;
	@Mock
	private MedicinePump medicinePump;
	@Mock
	private AlertService alertService;
	
    @Test
    public void DoseController_should_throw_NPE_when_healthMonitor_is_null() {
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
		try {
			// when
			new DoseController(healthMonitor, null, alertService);
			fail();
		} catch (NullPointerException expected) {
			// then
		}
    }
    
    @Test
    public void DoseController_should_throw_NPE_when_alertService_is_null() {
		try {
			// when
			new DoseController(healthMonitor, medicinePump, null);
			fail();
		} catch (NullPointerException expected) {
			// then
		}
    }

}
