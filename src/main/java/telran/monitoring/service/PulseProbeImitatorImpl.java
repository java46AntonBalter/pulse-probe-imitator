package telran.monitoring.service;

import java.time.Instant;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Value;

import telran.monitoring.model.PulseProbe;

public class PulseProbeImitatorImpl implements PulseProbesImitator {
	@Value("${app.patients.amount:100}")
	private int PATIENTS_AMOUNT;
	@Value("${app.pulse.value.min:40}")
	private int PULSE_MIN;
	@Value("${app.pulse.value.max:480}")
	private int PULSE_MAX;
	@Value("${app.jump.probability:20}")
	private double JUMP_PROBABILITY;
	@Value("${app.increase.probability:50}")
	private double INCREASE_PROBABILITY;
	@Value("${app.jump.multiplier:2}")
	private double JUMP_MULTIPLIER;
	@Value("${app.no-jump.multiplier:1.2}")
	private double NO_JUMP_MULTIPLIER;
	private Random random;

	private static Map<Long, PulseProbe> patientsProbes = new ConcurrentHashMap<Long, PulseProbe>();
	private static long seqNumber = 0;

	@Override
	public PulseProbe nextProbe() {
		random = ThreadLocalRandom.current();
		long patientId = random.nextLong(PATIENTS_AMOUNT);
		int newPulseValue;
		PulseProbe newProbe;

		if (patientsProbes.containsKey(patientId)) {
			PulseProbe oldProbe = patientsProbes.get(patientId);
			int oldPulseValue = oldProbe.value;

			if (random.nextDouble(100) < JUMP_PROBABILITY) {
				if (random.nextDouble(100) < INCREASE_PROBABILITY) {
					newPulseValue = (int) (oldPulseValue * JUMP_MULTIPLIER);
				} else {
					newPulseValue = (int) (oldPulseValue / JUMP_MULTIPLIER);
				}
			} else {
				if (random.nextDouble(100) < INCREASE_PROBABILITY) {
					newPulseValue = (int) (oldPulseValue * NO_JUMP_MULTIPLIER);
				} else {
					newPulseValue = (int) (oldPulseValue / NO_JUMP_MULTIPLIER);
				}
			}
			newProbe = new PulseProbe(patientId, Instant.now().toEpochMilli(), oldProbe.sequenceNumber + 1,
					newPulseValue);
		} else {
			newPulseValue = random.nextInt(PULSE_MIN, PULSE_MAX);
			newProbe = new PulseProbe(patientId, Instant.now().toEpochMilli(), seqNumber, newPulseValue);
		}
		patientsProbes.put(patientId, newProbe);
		return newProbe;

	}

}
