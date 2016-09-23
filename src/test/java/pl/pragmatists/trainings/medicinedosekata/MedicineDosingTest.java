package pl.pragmatists.trainings.medicinedosekata;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import pl.pragmatists.trainings.medicinedosekata.dependencies.AlertService;
import pl.pragmatists.trainings.medicinedosekata.dependencies.DoseUnsuccessfulException;
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

	@Test
	public void should_dose_two_raising_for_very_low_pressure() {
		// given
		when(healthMonitor.getSystolicBloodPressure()).thenReturn(DoseController.MIN_PRESSURE_FOR_ONE_DOSE - 1);
		DoseController doseController = new DoseController(healthMonitor, medicinePump, alertService);
		// when
		doseController.checkHealthAndApplyMedicine();
		// then
		verify(medicinePump, times(2)).dose(Medicine.PRESSURE_RAISING_MEDICINE);
	}

	@Test
	public void should_dose_one_lowering_for_high_pressure() {
		// given
		when(healthMonitor.getSystolicBloodPressure()).thenReturn(DoseController.MAX_NORMAL_PRESSURE + 1);
		DoseController doseController = new DoseController(healthMonitor, medicinePump, alertService);
		// when
		doseController.checkHealthAndApplyMedicine();
		// then
		verify(medicinePump).dose(Medicine.PRESSURE_LOWERING_MEDICINE);
	}

	@Test
	public void should_retry_dose_when_pump_does_not_work() {
		// given
		when(healthMonitor.getSystolicBloodPressure()).thenReturn(DoseController.MAX_NORMAL_PRESSURE + 1);
		doThrow(DoseUnsuccessfulException.class).doNothing().when(medicinePump)
				.dose(Medicine.PRESSURE_LOWERING_MEDICINE);
		DoseController doseController = new DoseController(healthMonitor, medicinePump, alertService);
		// when
		doseController.checkHealthAndApplyMedicine();
		// then
		verify(medicinePump, times(2)).dose(Medicine.PRESSURE_LOWERING_MEDICINE);
	}

	@Test
	public void should_not_overdose_when_pump_does_not_work() {
		// given
		when(healthMonitor.getSystolicBloodPressure()).thenReturn(DoseController.MIN_PRESSURE_FOR_ONE_DOSE - 1);
		DoseController doseController = new DoseController(healthMonitor, medicinePump, alertService);
		doNothing().doThrow(DoseUnsuccessfulException.class).doNothing().when(medicinePump).dose(Medicine.PRESSURE_RAISING_MEDICINE);
		doseController.checkHealthAndApplyMedicine();
		// then
		verify(medicinePump, times(3)).dose(Medicine.PRESSURE_RAISING_MEDICINE);
	}
}
