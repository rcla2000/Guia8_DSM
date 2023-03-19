package edu.udb.realtimedatabase

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import edu.udb.realtimedatabase.PersonasActivity.Companion.database
import edu.udb.realtimedatabase.datos.Persona
import java.util.Date

class AddPersonaActivity : AppCompatActivity() {
    private var edtDUI: EditText? = null
    private lateinit var spinnerGeneros : Spinner
    private lateinit var btnFechaNacimiento : ImageButton
    private lateinit var txtFechaNacimiento : EditText
    private lateinit var dpFechaNacimiento : DatePicker
    private lateinit var txtPeso : EditText
    private lateinit var txtAltura : EditText
    private var edtNombre: EditText? = null
    private var key = ""
    private var nombre = ""
    private var dui = ""
    private var accion = ""
    private lateinit var  database:DatabaseReference

     @RequiresApi(Build.VERSION_CODES.O)
     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_persona)
        inicializar()
    }
    private fun obtenerFechaDatePicker() : String {
        var dia = dpFechaNacimiento?.dayOfMonth.toString().padStart(2, '0')
        var mes = (dpFechaNacimiento!!.month+1).toString().padStart(2, '0')
        var anio = dpFechaNacimiento?.year.toString().padStart(4, '0')
        return dia + "/" + mes + "/" + anio
    }

    public fun mostrarCalendario(view: View?) : Unit{
        dpFechaNacimiento?.visibility = View.VISIBLE
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun inicializar() {
        edtNombre = findViewById<EditText>(R.id.edtNombre)
        edtDUI = findViewById<EditText>(R.id.edtDUI)
        spinnerGeneros = findViewById<Spinner>(R.id.listaGeneros)
        val edtNombre = findViewById<EditText>(R.id.edtNombre)
        val edtDUI = findViewById<EditText>(R.id.edtDUI)
        txtFechaNacimiento = findViewById<EditText>(R.id.edtFechaNacimiento)
        btnFechaNacimiento = findViewById<ImageButton>(R.id.btnFechaNacimiento)
        dpFechaNacimiento = findViewById<DatePicker>(R.id.dpFechaNacimiento)
        txtPeso = findViewById<EditText>(R.id.edtPeso)
        txtAltura = findViewById<EditText>(R.id.edtAltura)

        // Cargando listado de generos en spinner desde un archivo de recursos
        ArrayAdapter.createFromResource(
            this,
            R.array.generos_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerGeneros.adapter = adapter
        }

        // Asignando fecha actual del calendario al editText
        txtFechaNacimiento?.setText(obtenerFechaDatePicker())

        // Evento para cuando se seleccione una fecha del datepicker
        dpFechaNacimiento?.setOnDateChangedListener {
           dpFechaNacimiento, anio, mes, dia ->
            txtFechaNacimiento?.setText(obtenerFechaDatePicker())
            dpFechaNacimiento?.visibility = View.GONE
        }

        // Obtención de datos que envia actividad anterior
        val datos: Bundle? = intent.getExtras()
        if (datos != null) {
            key = datos.getString("key").toString()
            edtDUI.setText(intent.getStringExtra("dui").toString())
            edtNombre.setText(intent.getStringExtra("nombre").toString())
            txtAltura.setText(intent.getStringExtra("altura").toString())
            txtPeso.setText(intent.getStringExtra("peso").toString())

            accion = datos.getString("accion").toString()
        }
    }


    fun guardar(v: View?) {
        val nombre: String = edtNombre?.text.toString()
        val dui: String = edtDUI?.text.toString()
        val fechaNacimiento: String = obtenerFechaDatePicker()
        val genero : String = spinnerGeneros.selectedItem.toString()
        val altura : String = txtAltura.text.toString()
        val peso : String = txtPeso.text.toString()

        database = FirebaseDatabase.getInstance().getReference("personas")

        // Se forma objeto persona
        val persona = Persona(dui, nombre, fechaNacimiento, genero, peso, altura)

        if (accion == "a") { //Agregar registro
            database.child(nombre).setValue(persona).addOnSuccessListener {
                Toast.makeText(this,"Se guardó con éxito", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener{
                Toast.makeText(this,"Failed ", Toast.LENGTH_SHORT).show()
            }
        } else  // Editar registro
        {
            val key = database.child("nombre").push().key
            if (key == null) {
                Toast.makeText(this,"Llave vacía", Toast.LENGTH_SHORT).show()
            }
            val personasValues = persona.toMap()
            val childUpdates = hashMapOf<String, Any>(
                "$nombre" to personasValues
            )
            database.updateChildren(childUpdates)
            Toast.makeText(this,"Se actualizó con éxito", Toast.LENGTH_SHORT).show()
        }
        finish()
    }

    fun cancelar(v: View?) {
        finish()
    }
}