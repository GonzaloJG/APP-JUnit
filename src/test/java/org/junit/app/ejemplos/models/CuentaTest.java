package org.junit.app.ejemplos.models;

import lombok.RequiredArgsConstructor;
import org.junit.app.ejemplos.models.exceptions.DineroInsuficienteException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

//@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@RequiredArgsConstructor
class CuentaTest {
    Cuenta cuenta;

    private TestInfo testInfo;
    private TestReporter testReporter;

    @BeforeEach
    void initMetodoTest(TestInfo testInfo, TestReporter testReporter) {
        cuenta = new Cuenta("Andres", new BigDecimal("1000.12345"));
        this.testInfo = testInfo;
        this.testReporter = testReporter;

        System.out.println("iniciando el methodo");
        testReporter.publishEntry("ejecutando: " + testInfo.getDisplayName() + " " + testInfo.getTestMethod().get()
                + " con las etiquetas " + testInfo.getTags());
    }

    @AfterEach
    void tearDown() {
        System.out.println("finalizando el metodo de prueba");
    }

    @BeforeAll
    static void beforeAll() {
        System.out.println("inicializando el test");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("finalizando el test");
    }

    @Tag("cuenta")
    @Nested
    @DisplayName("probando atributos de la cuenta")
    class CuentaTestNombreSaldo {
        @Disabled
        @Test
        @DisplayName("Probando nombre de la cuenta corriente!")
        void testNombreCuenta() {
            testReporter.publishEntry(testInfo.getTags().toString());
            if (testInfo.getTags().contains("cuenta")){
                testReporter.publishEntry("hacer algo con la etiqueta cuenta");
            }
            Cuenta cuenta = new Cuenta();
            cuenta.setPersona("Andres");
            String esperado = "Andres";
            String real = cuenta.getPersona();

            assertNotNull(real, () -> "La cuenta no puede ser nula");
            assertEquals(esperado, real, () -> "El nombre de la cuenta no es el que se esperaba: se esperaba " + esperado
                    + " sin embargo fue " + real);
            assertTrue(real.equals("Andres"), () -> "Esperado debe ser igual al real");
        }

        @Test
        void testSaldoCuenta() {
            assertNotNull(cuenta.getSaldo());
            assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @Test
        void testReferenciaCuenta() {
            Cuenta cuenta1 = new Cuenta("John Doe", new BigDecimal("8900.9997"));
            Cuenta cuenta2 = new Cuenta("John Doe", new BigDecimal("8900.9997"));

//        assertNotEquals(cuenta1, cuenta2);
            assertEquals(cuenta1, cuenta2);
        }
    }

    @Nested
    class CuentaOperacionesTest {
        @Tag("cuenta")
        @Test
        void testDebitoCuenta() throws DineroInsuficienteException {
            cuenta.debito(new BigDecimal(100));

            assertNotNull(cuenta.getSaldo());
            assertEquals(900, cuenta.getSaldo().intValue());
            assertEquals("900.12345", cuenta.getSaldo().toPlainString());

        }

        @Tag("cuenta")
        @Test
        void testCreditoCuenta() {
            cuenta.credito(new BigDecimal(100));

            assertNotNull(cuenta.getSaldo());
            assertEquals(1100, cuenta.getSaldo().intValue());
            assertEquals("1100.12345", cuenta.getSaldo().toPlainString());

        }

        @Tag("cuenta")
        @Tag("error")
        @Test
        void testDineroInsuficienteExceptionCuenta() {
            Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
                cuenta.debito(new BigDecimal(1500));
            });
            String actual = exception.getMessage();
            String esperado = "Dinero Insuficiente";
            assertEquals(esperado, actual);
        }

        @Tag("banco")
        @Test
        void testTransferirDineroCuentas() throws DineroInsuficienteException {
            Cuenta cuenta1 = new Cuenta("Jhon Doe", new BigDecimal("2500"));
            Cuenta cuenta2 = new Cuenta("Andrés", new BigDecimal("1500.8989"));

            Banco banco = new Banco();
            banco.setNombre("Banco del Estado");
            banco.transferir(cuenta2, cuenta1, new BigDecimal("500"));

            assertEquals("1000.8989", cuenta2.getSaldo().toPlainString());
            assertEquals("3000", cuenta1.getSaldo().toPlainString());
        }

    /*@Test
    void testRelacionBancoCuentas() throws DineroInsuficienteException {
        Cuenta cuenta1 = new Cuenta("Jhon Doe", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("Andrés", new BigDecimal("1500.8989"));

        Banco banco = new Banco();
        banco.addCuenta(cuenta1);
        banco.addCuenta(cuenta2);

        banco.setNombre("Banco del Estado");
        banco.transferir(cuenta2, cuenta1, new BigDecimal("500"));

        assertAll(
                () -> assertEquals("1000.8989", cuenta2.getSaldo().toPlainString(),
                        () -> "El valor del saldo de la cuenta2 no es el esperado"),
                () -> assertEquals("3000", cuenta1.getSaldo().toPlainString(),
                        () -> "El valor del saldo de la cuenta1 no es el esperado")),
                () -> assertEquals(2, banco.getCuentas().size(),
                        () -> "El banco no tiene las cuentas esperadas"),
                () -> assertEquals("Banco del Estado", cuenta1.getBanco().getNombre()),
                () -> assertEquals("Andrés",
                        banco.getCuentas().stream()
                                .filter(c -> c.getPersona().equals("Andrés"))
                                .findFirst()
                                .get().getPersona()),
                () -> assertTrue(
                        banco.getCuentas().stream()
                        .anyMatch(c -> c.getPersona().equals("Jhon Doe"))));
    }*/
    }


    @Nested
    class SistemaOperativoTest {
        @Test
        @EnabledOnOs(OS.WINDOWS)
        void testSoloWindows() {
        }

        @Test
        @EnabledOnOs({OS.LINUX, OS.MAC})
        void testSoloLinuxMac() {
        }

        @Test
        @DisabledOnOs(OS.WINDOWS)
        void testNoWindows() {
        }
    }

    @Nested
    class JavaVersionTest {
        @Test
        @EnabledOnJre(JRE.JAVA_8)
        void soloJdk8() {
        }
    }

    @Nested
    class SystemPropertiesTest {
        @Test
        void imprimirProperties() {
            Properties properties = System.getProperties();
            properties.forEach((k, v) -> System.out.println(k + ":" + v));
        }
    }

    @Nested
    class VariableAmbienteTest {
        @Test
        void imprimirVariableAmbiente() {
            Map<String, String> getenv = System.getenv();
            getenv.forEach((k, v) -> System.out.println(k + " = " + v));
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "JAVA_HOME", matches = "C:\\Program Files\\Java\\jdk-11")
        void testJavaHome() {
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "NUMBER_OF_PROCESSOR", matches = "12")
        void testProcesadores() {
        }

        @Test
        @DisabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "prod")
        void testEnvProdDisabled() {
        }
    }

    @Nested
    class CuentaTestDev {
        @Test
        @DisplayName("testSaldoCuentaDev")
        void testSaldoCuentaDev() {
            Boolean esDev = "dev".equals(System.getProperty("ENV"));
            assumeTrue(esDev);
            assertNotNull(cuenta.getSaldo());
            assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @Test
        @DisplayName("testSaldoCuentaDev2")
        void testSaldoCuentaDev2() {
            Boolean esDev = "dev".equals(System.getProperty("ENV"));
            assumingThat(esDev, () -> {
                assertNotNull(cuenta.getSaldo());
                assertEquals(1000.123456, cuenta.getSaldo().doubleValue());
            });
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);

        }

        @DisplayName("Probando Debito Cuenta repetition")
        @RepeatedTest(value = 5, name = "{displayName} - Repeticion numero {currentRepetition} de {totalRepetitions}")
        void testDebitoCuentaRepetir(RepetitionInfo info) throws DineroInsuficienteException {
            if (info.getCurrentRepetition() == 3){
                System.out.println("estamos en la repeticion " + info.getCurrentRepetition());
            }
            cuenta.debito(new BigDecimal(100));

            assertNotNull(cuenta.getSaldo());
            assertEquals(900, cuenta.getSaldo().intValue());
            assertEquals("900.12345", cuenta.getSaldo().toPlainString());

        }

        @Tag("param")
        @Nested
        class PruebasParametrizadasTest{
            @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
            @ValueSource(strings = {"100", "200", "300", "500", "700", "1000"})
            void testDebitoCuentaValueSource(String monto) throws DineroInsuficienteException {
                cuenta.debito(new BigDecimal(monto));
                assertNotNull(cuenta.getSaldo());
                assertTrue( cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);

            }

            @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
            @CsvSource({"1,100", "2,200", "3,300", "4,500", "5,700", "6,1000"})
            void testDebitoCuentaCsvSource(String index, String monto) throws DineroInsuficienteException {
                System.out.println(index + " -> " + monto);
                cuenta.debito(new BigDecimal(monto));
                assertNotNull(cuenta.getSaldo());
                assertTrue( cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);

            }

            @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
            @CsvSource({"200,100,John,Andres", "250,200,Pepe,Pepe", "300,300,maria,Maria", "510,500,Pepa,Pepa", "750,700,Lucas,Luca", "1000,1000,Cata,Cata"})
            void testDebitoCuentaCsvSource2(String saldo, String monto, String esperado, String actual) throws DineroInsuficienteException {
                System.out.println(saldo + " -> " + monto);
                cuenta.setSaldo(new BigDecimal(saldo));
                cuenta.debito(new BigDecimal(monto));

                assertNotNull(cuenta.getSaldo());
                assertNotNull(cuenta.getPersona());
                assertEquals(esperado, actual);
                assertTrue( cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);

            }
        }

        @Tag("param")
        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @MethodSource("montoList")
        void testDebitoCuentaMethodSource(String monto) throws DineroInsuficienteException {
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue( cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        static List<String> montoList(){
            return Arrays.asList("100", "200", "300", "500", "700", "1000");
        }

    }

    @Nested
    @Tag("timeout")
    class EjemplosTimeoutTest {
        @Test
        @Timeout(1)
        void pruebaTimeout() throws InterruptedException {
            TimeUnit.MILLISECONDS.sleep(100);
        }

        @Test
        @Timeout(value = 1000, unit = TimeUnit.MILLISECONDS)
        void pruebaTimeout2() throws InterruptedException {
            TimeUnit.MILLISECONDS.sleep(900);
        }

        @Test
        void testTimeoutAssertions() {
            assertTimeout(Duration.ofSeconds(5), () -> {
                TimeUnit.MILLISECONDS.sleep(4000);
            });
        }
    }


}