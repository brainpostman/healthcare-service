import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoRepository;
import ru.netology.patient.service.alert.SendAlertService;
import ru.netology.patient.service.medical.MedicalServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Stream;


public class TestMedicalService {

    PatientInfo patient = new PatientInfo("1", "Ivan", "Ivanov", LocalDate.of(1996, 1, 1),
            new HealthInfo(BigDecimal.valueOf(36.6), new BloodPressure(120, 80)));

    @ParameterizedTest
    @MethodSource()
    public void testCheckBloodPressure(BloodPressure bp) {
        String expected = String.format("Warning, patient with id: %s, need help", patient.getId());
        PatientInfoRepository pir = Mockito.mock(PatientInfoRepository.class);
        SendAlertService sas = Mockito.mock(SendAlertService.class);
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.when(pir.getById(patient.getId()))
                .thenReturn(patient);
        MedicalServiceImpl msi = new MedicalServiceImpl(pir, sas);
        msi.checkBloodPressure("1", bp);
        if (!bp.equals(patient.getHealthInfo().getBloodPressure())) {
            Mockito.verify(sas).send(argumentCaptor.capture());
            String result = argumentCaptor.getValue();
            Assertions.assertEquals(expected, result.trim());
        } else {
            Mockito.verify(sas, Mockito.never()).send(expected);
        }
    }

    static Stream<BloodPressure> testCheckBloodPressure() {
        return Stream.of(new BloodPressure(150, 100),
                new BloodPressure(80, 50),
                new BloodPressure(120, 80));
    }

    @ParameterizedTest
    @MethodSource()
    public void testCheckTemperature(Double temp) {
        String expected = String.format("Warning, patient with id: %s, need help", patient.getId());
        PatientInfoRepository pir = Mockito.mock(PatientInfoRepository.class);
        SendAlertService sas = Mockito.mock(SendAlertService.class);
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.when(pir.getById(patient.getId()))
                .thenReturn(patient);
        MedicalServiceImpl msi = new MedicalServiceImpl(pir, sas);
        msi.checkTemperature("1", BigDecimal.valueOf(temp));
        if (!BigDecimal.valueOf(temp).equals(BigDecimal.valueOf(36.6))) {
            Mockito.verify(sas).send(argumentCaptor.capture());
            String result = argumentCaptor.getValue();
            Assertions.assertEquals(expected, result.trim());
        } else {
            Mockito.verify(sas, Mockito.never()).send(expected);
        }
    }

    static Stream<Double> testCheckTemperature() {
        return Stream.of(
                29.0,
                36.6,
                40.0);
    }

}
