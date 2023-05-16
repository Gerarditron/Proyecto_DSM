package com.example.login_dsm.datos

class Invoice {
    fun key(key: String?) {
    }
    var invoiceID: String? = null
    var numero: String? = null
    var tipo: String? = null
    var fecha: String? = null
    var cliente: String? = null
    var foto: String? = null
    var concepto: String? = null
    var total: String? = null
    var key: String? = null
    var tipoMov: String? = null
    var userID: String? = null
    var inv: MutableMap<String, Boolean> = HashMap()

    constructor() {}
    constructor(numero: String?, tipo: String?, fecha: String?, cliente: String?, concepto: String?, total: String?, foto: String?, tipoMov: String?, userID: String?,invoiceID: String?) {
        this.numero = numero
        this.tipo = tipo
        this.fecha = fecha
        this.cliente = cliente
        this.concepto = concepto
        this.total = total
        this.foto = foto
        this.tipoMov = tipoMov
        this.userID = userID
        this.invoiceID = invoiceID
    }

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "numero" to numero,
            "tipo" to tipo,
            "fecha" to fecha,
            "cliente" to cliente,
            "concepto" to concepto,
            "total" to total,
            "foto" to foto,
            "key" to key,
            "tipoMov" to tipoMov,
            "userID" to userID,
            "inv" to inv
        )
    }
}
