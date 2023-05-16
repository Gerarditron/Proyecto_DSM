package com.example.login_dsm.datos

class Supplier {
    fun key(key: String?) {
    }

    var nombrecomercial: String? = null
    var nombrelegal: String? = null
    var nrc: String? = null
    var nit: String? = null
    var telefonos: String? = null
    var pais: String? = null
    var userID: String? = null
    var key: String? = null
    var inv: MutableMap<String, Boolean> = HashMap()

    constructor() {}
    constructor(nombrecomercial: String?, nombrelegal: String?, nrc: String?, nit: String?, telefonos: String?, pais: String?,  userID: String?) {
        this.nombrecomercial = nombrecomercial
        this.nombrelegal = nombrelegal
        this.nrc = nrc
        this.nit = nit
        this.telefonos = telefonos
        this.pais = pais
        this.userID = userID
    }

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "nombrecomercial" to nombrecomercial,
            "nombrelegal" to nombrelegal,
            "nrc" to nrc,
            "nit" to nit,
            "telefonos" to telefonos,
            "pais" to pais,
            "userID" to userID,
            "key" to key,
            "inv" to inv
        )
    }
}