package telran.monitoring;

import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import telran.monitoring.model.PulseProbe;
import telran.monitoring.service.PulseProbesImitator;

@SpringBootApplication
public class PulseProbesImitatorApplication {

	@Autowired
	PulseProbesImitator imitator;
	public static void main(String[] args) {
		SpringApplication.run(PulseProbesImitatorApplication.class, args);
		
	}
	@Bean
	Supplier<PulseProbe> pulseProbeSupplier(){
		return imitator::nextProbe;
	}

}