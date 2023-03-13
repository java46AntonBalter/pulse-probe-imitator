package telran.monitoring.service;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import telran.monitoring.model.PulseProbe;

@Service
public class PulseProbesImitatorImpl implements PulseProbesImitator {
	@Value("${app.amount.patients: 10}")
	int nPatients;
	@Value("${app.min.pulse.value: 40}")
	int minPulseValue;
	@Value("${app.max.pulse.value: 210}")
	int maxPulseValue;
	@Value("${app.jump.probability: 10}")
	int jumpProb;
	@Value("${app.jump.multiple: 0.4}")
	double jumpMultiple;
	@Value("${app.value.multiple: 0.05}")
	double valueMultiple;
	@Value("${app.delta.increase.probability: 70}")
	int deltaIncreaseProb;
	static long seqNumber = 0;
	ThreadLocalRandom tlr = ThreadLocalRandom.current();
	HashMap<Long, Integer> patientsPulseValues = new HashMap<>();

	@Override
	public PulseProbe nextProbe() {
		long patientId = tlr.nextInt(1, nPatients + 1);
		int value = getValue(patientId);
		patientsPulseValues.put(patientId, value);
		return new PulseProbe( patientId  , System.currentTimeMillis(),
				++seqNumber, value);
	}
	private int getValue(long patientId) {
		Integer value = patientsPulseValues.get(patientId);
		return value == null ? tlr.nextInt(minPulseValue, maxPulseValue) : getNewValue(value);
	}
	private int getNewValue(Integer value) {
		int delta = (int) (isChance(jumpProb) ? value * jumpMultiple :
			value * tlr.nextDouble(0, valueMultiple));
		if (value + delta > maxPulseValue || !isChance(deltaIncreaseProb)) {
			delta = -delta;
		}
		return value + delta;
	}
	private boolean isChance(int prob) {
		
		return tlr.nextInt(0, 100) < prob;
	}

}