package pl.pragmatists.trainings.medicinedosekata;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import pl.pragmatists.trainings.medicinedosekata.dependencies.AlertService;
import pl.pragmatists.trainings.medicinedosekata.dependencies.HealthMonitor;
import pl.pragmatists.trainings.medicinedosekata.dependencies.Medicine;
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

	@Test
	public void should_dose_one_raising_for_low_pressure() {
		// given
		when(healthMonitor.getSystolicBloodPressure()).thenReturn(DoseController.MIN_NORMAL_PRESSURE - 1);
		DoseController doseController = new DoseController(healthMonitor, medicinePump, alertService);
		// when
		doseController.checkHealthAndApplyMedicine();
		// then
		verify(medicinePump).dose(Medicine.PRESSURE_RAISING_MEDICINE);
	}

	@Test
	public void should_do_nothing_for_correct_pressure() {
		// given
		when(healthMonitor.getSystolicBloodPressure()).thenReturn(DoseController.MIN_NORMAL_PRESSURE);
		DoseController doseController = new DoseController(healthMonitor, medicinePump, alertService);
		// when
		doseController.checkHealthAndApplyMedicine();
		// then
		verify(medicinePump, never()).dose(Medicine.PRESSURE_LOWERING_MEDICINE);
		verify(medicinePump, never()).dose(Medicine.PRESSURE_RAISING_MEDICINE);
	}
}
