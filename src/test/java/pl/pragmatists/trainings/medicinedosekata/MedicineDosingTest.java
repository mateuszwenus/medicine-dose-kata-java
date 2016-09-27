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
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
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
		when(medicinePump.getTimeSinceLastDoseInMinutes(Medicine.PRESSURE_RAISING_MEDICINE)).thenReturn(DoseController.MIN_MEDICINE_INTERVAL);
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
		when(healthMonitor.getSystolicBloodPressure()).thenReturn(DoseController.MIN_LOW_PRESSURE - 1);
		when(medicinePump.getTimeSinceLastDoseInMinutes(Medicine.PRESSURE_RAISING_MEDICINE)).thenReturn(DoseController.MIN_MEDICINE_INTERVAL);
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
		when(medicinePump.getTimeSinceLastDoseInMinutes(Medicine.PRESSURE_LOWERING_MEDICINE)).thenReturn(DoseController.MIN_MEDICINE_INTERVAL);
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
		when(medicinePump.getTimeSinceLastDoseInMinutes(Medicine.PRESSURE_LOWERING_MEDICINE)).thenReturn(DoseController.MIN_MEDICINE_INTERVAL);
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
		when(healthMonitor.getSystolicBloodPressure()).thenReturn(DoseController.MIN_LOW_PRESSURE - 1);
		when(medicinePump.getTimeSinceLastDoseInMinutes(Medicine.PRESSURE_RAISING_MEDICINE)).thenReturn(DoseController.MIN_MEDICINE_INTERVAL);
		DoseController doseController = new DoseController(healthMonitor, medicinePump, alertService);
		doNothing().doThrow(DoseUnsuccessfulException.class).doNothing().when(medicinePump).dose(Medicine.PRESSURE_RAISING_MEDICINE);
		doseController.checkHealthAndApplyMedicine();
		// then
		verify(medicinePump, times(3)).dose(Medicine.PRESSURE_RAISING_MEDICINE);
	}

	@Test
	public void should_dose_medicine_only_after_30min_from_last_medicine() {
		// given
		when(healthMonitor.getSystolicBloodPressure()).thenReturn(DoseController.MIN_NORMAL_PRESSURE - 1);
		when(medicinePump.getTimeSinceLastDoseInMinutes(Medicine.PRESSURE_RAISING_MEDICINE)).thenReturn(DoseController.MIN_MEDICINE_INTERVAL - 1);
		DoseController doseController = new DoseController(healthMonitor, medicinePump, alertService);
		// when
		doseController.checkHealthAndApplyMedicine();
		// then
		verify(medicinePump, never()).dose(Medicine.PRESSURE_RAISING_MEDICINE);
	}
	
	@Test
	public void should_notify_doctor_and_then_dose_three_lowering_for_critically_low_pressure() {
		// given
		when(healthMonitor.getSystolicBloodPressure()).thenReturn(54);
		when(medicinePump.getTimeSinceLastDoseInMinutes(Medicine.PRESSURE_RAISING_MEDICINE)).thenReturn(DoseController.MIN_MEDICINE_INTERVAL);
		DoseController doseController = new DoseController(healthMonitor, medicinePump, alertService);
		// when
		doseController.checkHealthAndApplyMedicine();
		// then
		InOrder inOrder = Mockito.inOrder(alertService, medicinePump);
		inOrder.verify(alertService).notifyDoctor();
		inOrder.verify(medicinePump, times(3)).dose(Medicine.PRESSURE_RAISING_MEDICINE);
	}
}
