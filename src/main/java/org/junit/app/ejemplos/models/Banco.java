package org.junit.app.ejemplos.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.app.ejemplos.models.exceptions.DineroInsuficienteException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class Banco {
    private String nombre;
    private List<Cuenta> cuentas;

    public Banco(){
        cuentas = new ArrayList<>();
    }
    public void addCuenta(Cuenta cuenta){
        cuentas.add(cuenta);
        cuenta.setBanco(this);
    }

    public void transferir(Cuenta origen, Cuenta destino, BigDecimal monto) throws DineroInsuficienteException {
        origen.debito(monto);
        destino.credito(monto);
    }
}
